package com.nexters.death.question.service;

import com.nexters.death.global.exception.BusinessException;
import com.nexters.death.question.dto.ActQuestionsResponse;
import com.nexters.death.question.dto.ActResponse;
import com.nexters.death.question.dto.QuestionResponse;
import com.nexters.death.question.entity.Act;
import com.nexters.death.question.entity.Question;
import com.nexters.death.question.entity.QuestionOption;
import com.nexters.death.question.entity.SpecialRule;
import com.nexters.death.question.exception.QuestionErrorCode;
import com.nexters.death.question.repository.QuestionOptionRepository;
import com.nexters.death.question.repository.QuestionRepository;
import com.nexters.death.question.repository.SpecialRuleRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
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
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final SpecialRuleRepository specialRuleRepository;

    @Transactional(readOnly = true)
    public List<ActQuestionsResponse> getQuestionnaire() {
        // 1. act, 문항 순서(displayOrder) 기준으로 활성 문항 조회
        List<Question> questions = questionRepository.findAllActiveWithActOrderByDisplayOrder();
        List<Long> questionIds = questions.stream().map(Question::getId).toList();

        // 2. 문항별 선택지를 한 번에 조회해 문항 ID로 그룹핑
        Map<Long, List<QuestionOption>> optionsByQuestionId = questionOptionRepository
            .findAllByQuestionIdInOrderByPositiveAscIdAsc(questionIds).stream()
            .collect(Collectors.groupingBy(option -> option.getQuestion().getId()));

        // 3. 문항을 순서를 유지하며 act 단위로 그룹핑
        Map<Act, List<QuestionResponse>> questionsByAct = new LinkedHashMap<>();
        for (Question question : questions) {
            List<QuestionOption> options = optionsByQuestionId.getOrDefault(question.getId(), List.of());
            questionsByAct
                .computeIfAbsent(question.getAct(), act -> new ArrayList<>())
                .add(QuestionResponse.from(question, options));
        }

        // 4. act별 문항 목록을 응답 DTO로 변환
        return questionsByAct.entrySet().stream()
            .map(entry -> new ActQuestionsResponse(ActResponse.from(entry.getKey()), entry.getValue()))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<Long> getActiveQuestionIds() {
        return questionRepository.findAllByActiveTrue().stream()
            .map(Question::getId)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<QuestionOption> findOptionsByIds(Collection<Long> optionIds) {
        List<QuestionOption> options = questionOptionRepository.findAllWithQuestionByIdIn(optionIds);
        Set<Long> distinctIds = Set.copyOf(optionIds);
        if (options.size() != distinctIds.size()) {
            throw new BusinessException(QuestionErrorCode.QUESTION_OPTION_NOT_FOUND);
        }
        return options;
    }

    @Transactional(readOnly = true)
    public Map<Long, SpecialRule> findSpecialRulesByQuestionIds(Collection<Long> questionIds) {
        return specialRuleRepository.findByQuestionIdIn(questionIds).stream()
            .collect(Collectors.toMap(specialRule -> specialRule.getQuestion().getId(), Function.identity()));
    }
}
