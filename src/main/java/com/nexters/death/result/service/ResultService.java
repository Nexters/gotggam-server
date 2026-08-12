package com.nexters.death.result.service;

import com.nexters.death.global.exception.BusinessException;
import com.nexters.death.policy.entity.LifeExpectancyPolicy;
import com.nexters.death.policy.service.LifeExpectancyPolicyService;
import com.nexters.death.question.entity.Category;
import com.nexters.death.question.entity.Question;
import com.nexters.death.question.entity.QuestionOption;
import com.nexters.death.question.entity.SpecialRule;
import com.nexters.death.question.service.QuestionService;
import com.nexters.death.result.client.WarningMessageClient;
import com.nexters.death.result.client.WarningMessageRequest;
import com.nexters.death.result.dto.AnswerRequest;
import com.nexters.death.result.dto.CategoryPenaltyResponse;
import com.nexters.death.result.dto.CharacterRequest;
import com.nexters.death.result.dto.CharacterResponse;
import com.nexters.death.result.dto.SurveyResultRequest;
import com.nexters.death.result.dto.SurveyResultResponse;
import com.nexters.death.result.entity.Gender;
import com.nexters.death.result.entity.Result;
import com.nexters.death.result.entity.ResultAnswer;
import com.nexters.death.result.entity.ResultCharacter;
import com.nexters.death.result.entity.ResultSpecialRule;
import com.nexters.death.result.exception.ResultErrorCode;
import com.nexters.death.result.repository.ResultAnswerRepository;
import com.nexters.death.result.repository.ResultCharacterRepository;
import com.nexters.death.result.repository.ResultRepository;
import com.nexters.death.result.repository.ResultSpecialRuleRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ResultService {

    private static final String DEFAULT_TODAY_MESSAGE = "오늘도 무사한 하루 되세요";
    private static final String DEFAULT_SPECIAL_RULE = "지금처럼만 지내세요. 특별히 고칠 점은 없습니다.";
    private static final int MAX_SPECIAL_RULES = 3;

    private final QuestionService questionService;
    private final LifeExpectancyPolicyService lifeExpectancyPolicyService;
    private final WarningMessageClient warningMessageClient;
    private final ResultRepository resultRepository;
    private final ResultAnswerRepository resultAnswerRepository;
    private final ResultCharacterRepository resultCharacterRepository;
    private final ResultSpecialRuleRepository resultSpecialRuleRepository;

    @Transactional
    public SurveyResultResponse createResult(SurveyResultRequest request) {
        List<AnsweredQuestion> answeredQuestions = resolveAnswers(request.answers());

        Map<Category, BigDecimal> penaltyByCategory = sumPenaltyByCategory(answeredQuestions);
        Map<CategoryPillar, BigDecimal> penaltyByPillar = sumPenaltyByPillar(penaltyByCategory);
        BigDecimal bodyPenalty = penaltyByPillar.getOrDefault(CategoryPillar.BODY, BigDecimal.ZERO);
        BigDecimal mindPenalty = penaltyByPillar.getOrDefault(CategoryPillar.MIND, BigDecimal.ZERO);
        BigDecimal attitudePenalty = penaltyByPillar.getOrDefault(CategoryPillar.ATTITUDE, BigDecimal.ZERO);

        LifeExpectancyPolicy policy = lifeExpectancyPolicyService.getPolicy();
        BigDecimal baseLife = request.gender() == Gender.MALE
            ? policy.getMaleExpectancy()
            : policy.getFemaleExpectancy();
        BigDecimal totalPenalty = bodyPenalty.add(mindPenalty).add(attitudePenalty);
        BigDecimal expectedLife = calculateExpectedLife(
            baseLife, totalPenalty, policy.getMinRemainingLife());

        String warningMessage = warningMessageClient.generateWarningMessage(
            new WarningMessageRequest(request.name(), bodyPenalty, mindPenalty, attitudePenalty));
        String todayMessage = resolveTodayMessage(request.todayMessage());
        List<SpecialRule> selectedRules = selectSpecialRules(answeredQuestions);

        Result result = resultRepository.save(Result.builder()
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
            .build());
        saveAnswers(result, answeredQuestions);
        ResultCharacter character = saveCharacter(result, request.character());
        saveSpecialRules(result, selectedRules);

        return new SurveyResultResponse(
            result.getId(),
            result.getShareToken(),
            result.getName(),
            result.getBirthDate(),
            result.getGender(),
            toDisplayYears(result.getExpectedLife()),
            result.getTodayMessage(),
            result.getWarningMessage(),
            CharacterResponse.from(character),
            toCategoryPenaltyResponses(penaltyByCategory),
            toSpecialRuleDescriptions(selectedRules)
        );
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
        BigDecimal minRemainingLife
    ) {
        return baseLife.subtract(totalPenalty).max(minRemainingLife);
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

    private void saveAnswers(Result result, List<AnsweredQuestion> answeredQuestions) {
        List<ResultAnswer> resultAnswers = answeredQuestions.stream()
            .map(answeredQuestion -> ResultAnswer.builder()
                .result(result)
                .question(answeredQuestion.question())
                .option(answeredQuestion.option())
                .build())
            .toList();
        resultAnswerRepository.saveAll(resultAnswers);
    }

    private ResultCharacter saveCharacter(Result result, CharacterRequest request) {
        return resultCharacterRepository.save(ResultCharacter.builder()
            .result(result)
            .faceType(request.faceType())
            .hairType(request.hairType())
            .eyeType(request.eyeType())
            .noseType(request.noseType())
            .mouthType(request.mouthType())
            .build());
    }

    private void saveSpecialRules(Result result, List<SpecialRule> selectedRules) {
        List<ResultSpecialRule> resultSpecialRules = IntStream.range(0, selectedRules.size())
            .mapToObj(index -> ResultSpecialRule.builder()
                .result(result)
                .specialRule(selectedRules.get(index))
                .displayOrder((short) (index + 1))
                .build())
            .toList();
        resultSpecialRuleRepository.saveAll(resultSpecialRules);
    }

    private List<CategoryPenaltyResponse> toCategoryPenaltyResponses(Map<Category, BigDecimal> penaltyByCategory) {
        return penaltyByCategory.entrySet().stream()
            .sorted(Comparator.comparing(entry -> CategoryPillar.from(entry.getKey().getCategoryKey())))
            .map(entry -> new CategoryPenaltyResponse(
                entry.getKey().getId(),
                entry.getKey().getName(),
                toDisplayYears(entry.getValue())))
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

    private record AnsweredQuestion(Question question, QuestionOption option) {
    }
}
