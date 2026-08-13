package com.nexters.death.result.service;

import com.nexters.death.global.exception.BusinessException;
import com.nexters.death.policy.entity.AgeWeight;
import com.nexters.death.policy.entity.LifeExpectancyPolicy;
import com.nexters.death.policy.service.AgeWeightService;
import com.nexters.death.policy.service.LifeExpectancyPolicyService;
import com.nexters.death.question.entity.Category;
import com.nexters.death.question.entity.QuestionOption;
import com.nexters.death.question.entity.SpecialRule;
import com.nexters.death.question.service.QuestionService;
import com.nexters.death.result.client.WarningMessageClient;
import com.nexters.death.result.client.WarningMessageRequest;
import com.nexters.death.result.dto.AnswerRequest;
import com.nexters.death.result.dto.CategoryPenaltyResponse;
import com.nexters.death.result.dto.CharacterResponse;
import com.nexters.death.result.dto.ResultCountResponse;
import com.nexters.death.result.dto.SurveyResultRequest;
import com.nexters.death.result.dto.SurveyResultResponse;
import com.nexters.death.result.entity.Gender;
import com.nexters.death.result.entity.Result;
import com.nexters.death.result.exception.ResultErrorCode;
import com.nexters.death.result.repository.ResultRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.time.Period;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ResultService {

    private static final String DEFAULT_TODAY_MESSAGE = "오늘도 무사한 하루 되세요";
    private static final String DEFAULT_SPECIAL_RULE = "지금처럼만 지내세요. 특별히 고칠 점은 없습니다.";
    private static final int MAX_SPECIAL_RULES = 3;
    private static final BigDecimal FLOOR_YEARS_ABOVE_INPUT_AGE = BigDecimal.valueOf(5);

    private final QuestionService questionService;
    private final LifeExpectancyPolicyService lifeExpectancyPolicyService;
    private final AgeWeightService ageWeightService;
    private final WarningMessageClient warningMessageClient;
    private final ResultWriter resultWriter;
    private final ResultRepository resultRepository;
    private final Clock clock;

    // 조회와 계산, 외부 API(Gemini) 호출은 트랜잭션 밖에서 수행하고, 실제 저장만 ResultWriter의 트랜잭션에 위임한다.
    public SurveyResultResponse createResult(SurveyResultRequest request) {
        List<AnsweredQuestion> answeredQuestions = resolveAnswers(request.answers());

        int inputAge = ageOf(request.birthDate());
        AgeWeight weights = ageWeightService.getWeightsForAge(inputAge);
        Map<Category, BigDecimal> penaltyByCategory = sumPenaltyByCategory(answeredQuestions);
        Map<CategoryPillar, BigDecimal> penaltyByPillar = sumPenaltyByPillar(penaltyByCategory);
        BigDecimal bodyPenalty = applyWeight(penaltyByPillar, CategoryPillar.BODY, weights.getBodyWeight());
        BigDecimal mindPenalty = applyWeight(penaltyByPillar, CategoryPillar.MIND, weights.getMindWeight());
        BigDecimal attitudePenalty = applyWeight(penaltyByPillar, CategoryPillar.ATTITUDE, weights.getAttitudeWeight());

        LifeExpectancyPolicy policy = lifeExpectancyPolicyService.getPolicy();
        BigDecimal baseLife = request.gender() == Gender.MALE
            ? policy.getMaleExpectancy()
            : policy.getFemaleExpectancy();
        BigDecimal totalPenalty = bodyPenalty.add(mindPenalty).add(attitudePenalty);
        BigDecimal expectedLife = calculateExpectedLife(baseLife, totalPenalty, inputAge);

        String warningMessage = warningMessageClient.generateWarningMessage(
            new WarningMessageRequest(request.name(), bodyPenalty, mindPenalty, attitudePenalty));
        String todayMessage = resolveTodayMessage(request.todayMessage());
        List<SpecialRule> selectedRules = selectSpecialRules(answeredQuestions);

        Result result = Result.builder()
            .name(request.name())
            .birthDate(request.birthDate())
            .gender(request.gender())
            .baseLife(baseLife)
            .bodyPenalty(bodyPenalty)
            .mindPenalty(mindPenalty)
            .attitudePenalty(attitudePenalty)
            .expectedLife(expectedLife)
            .todayMessage(todayMessage)
            .warningMessage(warningMessage)
            .build();
        ResultWriter.PersistedResult persisted =
            resultWriter.write(result, answeredQuestions, request.character(), selectedRules);
        Result savedResult = persisted.result();

        return new SurveyResultResponse(
            savedResult.getId(),
            savedResult.getShareToken(),
            savedResult.getName(),
            savedResult.getBirthDate(),
            savedResult.getGender(),
            toDisplayYears(savedResult.getExpectedLife()),
            savedResult.getTodayMessage(),
            savedResult.getWarningMessage(),
            CharacterResponse.from(persisted.character()),
            toCategoryPenaltyResponses(penaltyByCategory, weights),
            toSpecialRuleDescriptions(selectedRules)
        );
    }

    @Transactional(readOnly = true)
    public ResultCountResponse countParticipants() {
        long totalParticipants = resultRepository.count();
        return new ResultCountResponse(totalParticipants);
    }

    private List<AnsweredQuestion> resolveAnswers(List<AnswerRequest> answers) {
        List<Long> questionIds = answers.stream().map(AnswerRequest::questionId).toList();
        Set<Long> distinctQuestionIds = Set.copyOf(questionIds);
        if (distinctQuestionIds.size() != questionIds.size()) {
            throw new BusinessException(ResultErrorCode.INVALID_ANSWER);
        }

        Set<Long> activeQuestionIds = Set.copyOf(questionService.getActiveQuestionIds());
        if (!distinctQuestionIds.equals(activeQuestionIds)) {
            throw new BusinessException(ResultErrorCode.INCOMPLETE_SURVEY);
        }

        List<Long> optionIds = answers.stream().map(AnswerRequest::optionId).toList();
        Map<Long, QuestionOption> optionsById = questionService.findOptionsByIds(optionIds).stream()
            .collect(Collectors.toMap(QuestionOption::getId, Function.identity()));

        return answers.stream()
            .map(answer -> {
                QuestionOption option = optionsById.get(answer.optionId());
                if (!option.getQuestion().getId().equals(answer.questionId())) {
                    throw new BusinessException(ResultErrorCode.INVALID_ANSWER);
                }
                return new AnsweredQuestion(option.getQuestion(), option);
            })
            .toList();
    }

    private int ageOf(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now(clock)).getYears();
    }

    // 카테고리(pillar) 원본 페널티 합에 나이대별 가중치를 곱해 실제 차감량을 구한다.
    private BigDecimal applyWeight(
        Map<CategoryPillar, BigDecimal> penaltyByPillar,
        CategoryPillar pillar,
        BigDecimal weight
    ) {
        BigDecimal rawPenalty = penaltyByPillar.getOrDefault(pillar, BigDecimal.ZERO);
        return rawPenalty.multiply(weight).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal weightOf(AgeWeight weights, CategoryPillar pillar) {
        return switch (pillar) {
            case BODY -> weights.getBodyWeight();
            case MIND -> weights.getMindWeight();
            case ATTITUDE -> weights.getAttitudeWeight();
        };
    }

    private Map<Category, BigDecimal> sumPenaltyByCategory(List<AnsweredQuestion> answeredQuestions) {
        return answeredQuestions.stream()
            .collect(Collectors.groupingBy(
                answeredQuestion -> answeredQuestion.question().getCategory(),
                Collectors.reducing(
                    BigDecimal.ZERO,
                    answeredQuestion -> answeredQuestion.option().getLifePenalty(),
                    BigDecimal::add)
            ));
    }

    private Map<CategoryPillar, BigDecimal> sumPenaltyByPillar(Map<Category, BigDecimal> penaltyByCategory) {
        Map<CategoryPillar, BigDecimal> penaltyByPillar = new EnumMap<>(CategoryPillar.class);
        penaltyByCategory.forEach((category, penalty) ->
            penaltyByPillar.merge(CategoryPillar.from(category.getCategoryKey()), penalty, BigDecimal::add));
        return penaltyByPillar;
    }

    private BigDecimal calculateExpectedLife(
        BigDecimal baseLife,
        BigDecimal totalPenalty,
        int inputAge
    ) {
        BigDecimal expectedLife = baseLife.subtract(totalPenalty);
        BigDecimal inputAgeYears = BigDecimal.valueOf(inputAge);
        if (expectedLife.compareTo(inputAgeYears) <= 0) {
            return inputAgeYears.add(FLOOR_YEARS_ABOVE_INPUT_AGE);
        }
        return expectedLife;
    }

    private String resolveTodayMessage(String todayMessage) {
        if (todayMessage == null || todayMessage.isBlank()) {
            return DEFAULT_TODAY_MESSAGE;
        }
        return todayMessage;
    }

    private List<SpecialRule> selectSpecialRules(List<AnsweredQuestion> answeredQuestions) {
        List<AnsweredQuestion> negativeAnswers = answeredQuestions.stream()
            .filter(answeredQuestion -> !answeredQuestion.option().isPositive())
            .toList();
        if (negativeAnswers.isEmpty()) {
            return List.of();
        }

        List<Long> questionIds = negativeAnswers.stream()
            .map(answeredQuestion -> answeredQuestion.question().getId())
            .toList();
        Map<Long, SpecialRule> rulesByQuestionId = questionService.findSpecialRulesByQuestionIds(questionIds);

        return negativeAnswers.stream()
            .filter(answeredQuestion -> rulesByQuestionId.containsKey(answeredQuestion.question().getId()))
            .sorted(Comparator
                .comparing((AnsweredQuestion answeredQuestion) -> answeredQuestion.option().getLifePenalty())
                .reversed()
                .thenComparing(answeredQuestion -> answeredQuestion.question().getDisplayOrder()))
            .limit(MAX_SPECIAL_RULES)
            .map(answeredQuestion -> rulesByQuestionId.get(answeredQuestion.question().getId()))
            .toList();
    }

    private List<CategoryPenaltyResponse> toCategoryPenaltyResponses(
        Map<Category, BigDecimal> penaltyByCategory,
        AgeWeight weights
    ) {
        return penaltyByCategory.entrySet().stream()
            .sorted(Comparator.comparing(entry -> CategoryPillar.from(entry.getKey().getCategoryKey())))
            .map(entry -> {
                CategoryPillar pillar = CategoryPillar.from(entry.getKey().getCategoryKey());
                BigDecimal weightedPenalty = entry.getValue().multiply(weightOf(weights, pillar));
                return new CategoryPenaltyResponse(
                    entry.getKey().getId(),
                    entry.getKey().getName(),
                    toDisplayYears(weightedPenalty));
            })
            .toList();
    }

    private int toDisplayYears(BigDecimal years) {
        return years.setScale(0, RoundingMode.HALF_UP).intValueExact();
    }

    private List<String> toSpecialRuleDescriptions(List<SpecialRule> selectedRules) {
        if (selectedRules.isEmpty()) {
            return List.of(DEFAULT_SPECIAL_RULE);
        }
        return selectedRules.stream().map(SpecialRule::getDescription).toList();
    }
}
