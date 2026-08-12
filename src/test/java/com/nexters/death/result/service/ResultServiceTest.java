package com.nexters.death.result.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.nexters.death.result.dto.ResultCountResponse;
import com.nexters.death.result.repository.ResultRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ResultServiceTest {

    @Mock
    private ResultRepository resultRepository;

    @InjectMocks
    private ResultService resultService;

    @Test
    void 참여자_수는_result_테이블의_행_수를_그대로_반환한다() {
        // given
        given(resultRepository.count()).willReturn(42L);

        // when
        ResultCountResponse response = resultService.countParticipants();

        // then
        assertThat(response.totalParticipants()).isEqualTo(42L);
    }
}
