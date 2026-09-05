package com.nexters.gotggam.policy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.nexters.gotggam.global.exception.BusinessException;
import com.nexters.gotggam.policy.entity.AgeWeight;
import com.nexters.gotggam.policy.repository.AgeWeightRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AgeWeightServiceTest {

    @Mock
    private AgeWeightRepository ageWeightRepository;

    @InjectMocks
    private AgeWeightService ageWeightService;

    @Test
    void 나이가_구간_안에_들면_해당_구간을_반환한다() {
        given(ageWeightRepository.findAllByOrderByStartAgeAsc()).willReturn(bands());

        AgeWeight weight = ageWeightService.getWeightsForAge(35);

        assertThat(weight.getStartAge()).isEqualTo((short) 30);
        assertThat(weight.getEndAge()).isEqualTo((short) 39);
    }

    @Test
    void 나이가_최소_구간보다_어리면_가장_어린_구간으로_클램프한다() {
        given(ageWeightRepository.findAllByOrderByStartAgeAsc()).willReturn(bands());

        AgeWeight weight = ageWeightService.getWeightsForAge(6);

        assertThat(weight.getStartAge()).isEqualTo((short) 10);
    }

    @Test
    void 나이가_최대_구간보다_많으면_가장_많은_구간으로_클램프한다() {
        given(ageWeightRepository.findAllByOrderByStartAgeAsc()).willReturn(bands());

        AgeWeight weight = ageWeightService.getWeightsForAge(96);

        assertThat(weight.getEndAge()).isEqualTo((short) 89);
    }

    @Test
    void 가중치가_하나도_설정되지_않았으면_예외를_던진다() {
        given(ageWeightRepository.findAllByOrderByStartAgeAsc()).willReturn(List.of());

        assertThatThrownBy(() -> ageWeightService.getWeightsForAge(30))
            .isInstanceOf(BusinessException.class);
    }

    private List<AgeWeight> bands() {
        return List.of(
            band(10, 19),
            band(30, 39),
            band(80, 89)
        );
    }

    private AgeWeight band(int startAge, int endAge) {
        return AgeWeight.builder()
            .startAge((short) startAge)
            .endAge((short) endAge)
            .bodyWeight(new BigDecimal("1.00"))
            .mindWeight(new BigDecimal("1.00"))
            .attitudeWeight(new BigDecimal("1.00"))
            .build();
    }
}
