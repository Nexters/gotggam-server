package com.nexters.gotggam.consent.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.nexters.gotggam.consent.dto.ConsentRequest;
import com.nexters.gotggam.consent.entity.Consent;
import com.nexters.gotggam.consent.entity.ConsentType;
import com.nexters.gotggam.consent.repository.ConsentRepository;
import com.nexters.gotggam.global.exception.BusinessException;
import com.nexters.gotggam.result.entity.Gender;
import com.nexters.gotggam.result.entity.Result;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConsentServiceTest {

    @Mock
    private ConsentRepository consentRepository;

    @InjectMocks
    private ConsentService consentService;

    @Test
    void 두_동의_항목_모두_동의하면_예외_없이_통과한다() {
        consentService.validateAgreed(List.of(
            new ConsentRequest(ConsentType.PRIVACY_POLICY, "1.0", true),
            new ConsentRequest(ConsentType.TERMS_OF_SERVICE, "1.0", true)
        ));
    }

    @Test
    void 동의_항목_중_하나라도_agreed가_false이면_예외를_던진다() {
        assertThatThrownBy(() -> consentService.validateAgreed(List.of(
            new ConsentRequest(ConsentType.PRIVACY_POLICY, "1.0", false),
            new ConsentRequest(ConsentType.TERMS_OF_SERVICE, "1.0", true)
        ))).isInstanceOf(BusinessException.class);
    }

    @Test
    void 필수_동의_유형_중_일부가_누락되면_예외를_던진다() {
        assertThatThrownBy(() -> consentService.validateAgreed(List.of(
            new ConsentRequest(ConsentType.PRIVACY_POLICY, "1.0", true)
        ))).isInstanceOf(BusinessException.class);
    }

    @Test
    void 동일한_유형이_중복으로_오면_예외를_던진다() {
        assertThatThrownBy(() -> consentService.validateAgreed(List.of(
            new ConsentRequest(ConsentType.PRIVACY_POLICY, "1.0", true),
            new ConsentRequest(ConsentType.PRIVACY_POLICY, "1.1", true)
        ))).isInstanceOf(BusinessException.class);
    }

    @Test
    void 동의_기록을_요청의_타입_버전_동의시각으로_저장한다() {
        Result result = Result.builder()
            .name("김철수")
            .birthDate(LocalDate.of(1990, 6, 15))
            .gender(Gender.MALE)
            .baseLife(BigDecimal.TEN)
            .bodyPenalty(BigDecimal.ZERO)
            .mindPenalty(BigDecimal.ZERO)
            .attitudePenalty(BigDecimal.ZERO)
            .expectedLife(BigDecimal.TEN)
            .build();
        LocalDateTime consentedAt = LocalDateTime.of(2026, 8, 17, 12, 0);

        consentService.recordConsents(
            result,
            List.of(new ConsentRequest(ConsentType.PRIVACY_POLICY, "1.0", true)),
            consentedAt);

        ArgumentCaptor<List<Consent>> captor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(consentRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(1);
        assertThat(captor.getValue().getFirst().getType()).isEqualTo(ConsentType.PRIVACY_POLICY);
        assertThat(captor.getValue().getFirst().getVersion()).isEqualTo("1.0");
        assertThat(captor.getValue().getFirst().getConsentedAt()).isEqualTo(consentedAt);
    }
}
