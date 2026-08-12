package com.nexters.death.question.repository;

import com.nexters.death.question.entity.SpecialRule;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecialRuleRepository extends JpaRepository<SpecialRule, Long> {

    List<SpecialRule> findByQuestionIdIn(Collection<Long> questionIds);
}
