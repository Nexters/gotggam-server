package com.nexters.death.policy.repository;

import com.nexters.death.policy.entity.AgeWeight;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgeWeightRepository extends JpaRepository<AgeWeight, Long> {

    List<AgeWeight> findAllByOrderByStartAgeAsc();
}
