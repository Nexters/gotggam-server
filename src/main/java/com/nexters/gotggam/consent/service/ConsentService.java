package com.nexters.gotggam.consent.service;

import com.nexters.gotggam.consent.dto.ConsentRequest;
import com.nexters.gotggam.consent.entity.Consent;
import com.nexters.gotggam.consent.entity.ConsentType;
import com.nexters.gotggam.consent.exception.ConsentErrorCode;
import com.nexters.gotggam.consent.repository.ConsentRepository;
import com.nexters.gotggam.global.exception.BusinessException;
import com.nexters.gotggam.result.entity.Result;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConsentService {

    private static final Set<ConsentType> REQUIRED_TYPES = EnumSet.of(
        ConsentType.PRIVACY_POLICY,
        ConsentType.TERMS_OF_SERVICE
    );

    private final ConsentRepository consentRepository;

    public void validateAgreed(List<ConsentRequest> consents) {
        // 1. 필수 동의 유형이 중복/누락 없이 정확히 한 번씩 왔는지 검증
        Set<ConsentType> requestedTypes = consents.stream()
            .map(ConsentRequest::type)
            .collect(Collectors.toSet());
        if (requestedTypes.size() != consents.size() || !requestedTypes.equals(REQUIRED_TYPES)) {
            throw new BusinessException(ConsentErrorCode.INCOMPLETE_CONSENT);
        }

        // 2. 모든 항목이 동의(agreed=true)했는지 검증 - 하나라도 미동의면 거부
        boolean allAgreed = consents.stream().allMatch(ConsentRequest::agreed);
        if (!allAgreed) {
            throw new BusinessException(ConsentErrorCode.CONSENT_REQUIRED);
        }
    }

    @Transactional
    public void recordConsents(Result result, List<ConsentRequest> consents, LocalDateTime consentedAt) {
        List<Consent> savedConsents = consents.stream()
            .map(consent -> Consent.builder()
                .result(result)
                .type(consent.type())
                .version(consent.version())
                .consentedAt(consentedAt)
                .build())
            .toList();
        consentRepository.saveAll(savedConsents);
    }
}
