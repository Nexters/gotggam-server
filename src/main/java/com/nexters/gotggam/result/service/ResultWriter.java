package com.nexters.gotggam.result.service;

import com.nexters.gotggam.consent.dto.ConsentRequest;
import com.nexters.gotggam.consent.service.ConsentService;
import com.nexters.gotggam.question.entity.SpecialRule;
import com.nexters.gotggam.result.dto.CharacterRequest;
import com.nexters.gotggam.result.entity.Result;
import com.nexters.gotggam.result.entity.ResultAnswer;
import com.nexters.gotggam.result.entity.ResultCharacter;
import com.nexters.gotggam.result.entity.ResultSpecialRule;
import com.nexters.gotggam.result.repository.ResultAnswerRepository;
import com.nexters.gotggam.result.repository.ResultCharacterRepository;
import com.nexters.gotggam.result.repository.ResultRepository;
import com.nexters.gotggam.result.repository.ResultSpecialRuleRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 결과 저장만 담당하는 쓰기 전용 트랜잭션 경계.
// 외부 API(Gemini) 호출은 ResultService에서 이 write 호출 전에 끝나므로, DB 커넥션을 잡은 채 외부 응답을 기다리지 않는다.
@Service
@RequiredArgsConstructor
public class ResultWriter {

    private final ResultRepository resultRepository;
    private final ResultAnswerRepository resultAnswerRepository;
    private final ResultCharacterRepository resultCharacterRepository;
    private final ResultSpecialRuleRepository resultSpecialRuleRepository;
    private final ConsentService consentService;

    @Transactional
    public PersistedResult write(
        Result result,
        List<AnsweredQuestion> answeredQuestions,
        CharacterRequest character,
        List<SpecialRule> selectedRules,
        List<ConsentRequest> consents,
        LocalDateTime consentedAt
    ) {
        Result savedResult = resultRepository.save(result);
        saveAnswers(savedResult, answeredQuestions);
        ResultCharacter savedCharacter = saveCharacter(savedResult, character);
        saveSpecialRules(savedResult, selectedRules);
        consentService.recordConsents(savedResult, consents, consentedAt);
        return new PersistedResult(savedResult, savedCharacter);
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

    public record PersistedResult(Result result, ResultCharacter character) {
    }
}
