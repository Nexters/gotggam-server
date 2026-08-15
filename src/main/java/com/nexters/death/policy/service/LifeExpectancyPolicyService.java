package com.nexters.death.policy.service;

import com.nexters.death.global.exception.BusinessException;
import com.nexters.death.policy.entity.LifeExpectancyPolicy;
import com.nexters.death.policy.exception.PolicyErrorCode;
import com.nexters.death.policy.repository.LifeExpectancyPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LifeExpectancyPolicyService {

    private final LifeExpectancyPolicyRepository lifeExpectancyPolicyRepository;

    @Transactional(readOnly = true)
    public LifeExpectancyPolicy getPolicy() {
        return lifeExpectancyPolicyRepository.findFirstByOrderByIdAsc()
            .orElseThrow(() -> new BusinessException(PolicyErrorCode.LIFE_EXPECTANCY_POLICY_NOT_FOUND));
    }
}
