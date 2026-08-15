package com.nexters.death.policy.service;

import com.nexters.death.global.exception.BusinessException;
import com.nexters.death.policy.entity.AgeWeight;
import com.nexters.death.policy.exception.PolicyErrorCode;
import com.nexters.death.policy.repository.AgeWeightRepository;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AgeWeightService {

    private final AgeWeightRepository ageWeightRepository;

    // 나이에 해당하는 구간을 반환하되, 구간 밖(예: 9세 이하, 90세 이상)이면 가장 가까운 구간으로 클램프한다.
    @Transactional(readOnly = true)
    public AgeWeight getWeightsForAge(int age) {
        List<AgeWeight> weights = ageWeightRepository.findAllByOrderByStartAgeAsc();
        if (weights.isEmpty()) {
            throw new BusinessException(PolicyErrorCode.AGE_WEIGHT_NOT_CONFIGURED);
        }

        return weights.stream()
            .filter(weight -> weight.getStartAge() <= age && age <= weight.getEndAge())
            .findFirst()
            .orElseGet(() -> clampToNearest(weights, age));
    }

    private AgeWeight clampToNearest(List<AgeWeight> weights, int age) {
        AgeWeight first = weights.getFirst();
        if (age < first.getStartAge()) {
            return first;
        }
        AgeWeight last = weights.getLast();
        if (age > last.getEndAge()) {
            return last;
        }
        // 구간 사이 공백에 떨어진 경우: 구간 경계까지의 거리가 가장 가까운 구간을 고른다.
        return weights.stream()
            .min(Comparator.comparingInt(weight -> distanceTo(weight, age)))
            .orElse(first);
    }

    private int distanceTo(AgeWeight weight, int age) {
        if (age < weight.getStartAge()) {
            return weight.getStartAge() - age;
        }
        if (age > weight.getEndAge()) {
            return age - weight.getEndAge();
        }
        return 0;
    }
}
