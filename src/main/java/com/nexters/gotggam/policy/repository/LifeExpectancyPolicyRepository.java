package com.nexters.gotggam.policy.repository;

import com.nexters.gotggam.policy.entity.LifeExpectancyPolicy;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LifeExpectancyPolicyRepository extends JpaRepository<LifeExpectancyPolicy, Long> {

    Optional<LifeExpectancyPolicy> findFirstByOrderByIdAsc();
}
