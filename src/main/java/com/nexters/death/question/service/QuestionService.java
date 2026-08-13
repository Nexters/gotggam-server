package com.nexters.death.question.service;

import com.nexters.death.global.exception.BusinessException;
import com.nexters.death.question.entity.Question;
import com.nexters.death.question.entity.QuestionOption;
import com.nexters.death.question.entity.SpecialRule;
import com.nexters.death.question.exception.QuestionErrorCode;
import com.nexters.death.question.repository.QuestionOptionRepository;
import com.nexters.death.question.repository.QuestionRepository;
import com.nexters.death.question.repository.SpecialRuleRepository;
import java.util.Collection;
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
