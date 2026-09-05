package com.nexters.gotggam.policy.service;

import com.nexters.gotggam.global.exception.BusinessException;
import com.nexters.gotggam.policy.entity.LifeExpectancyPolicy;
import com.nexters.gotggam.policy.exception.PolicyErrorCode;
import com.nexters.gotggam.policy.repository.LifeExpectancyPolicyRepository;
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
