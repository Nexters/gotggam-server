package com.nexters.gotggam.policy.repository;

import com.nexters.gotggam.policy.entity.AgeWeight;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgeWeightRepository extends JpaRepository<AgeWeight, Long> {

    List<AgeWeight> findAllByOrderByStartAgeAsc();
}
