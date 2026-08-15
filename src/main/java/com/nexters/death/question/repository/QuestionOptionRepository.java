package com.nexters.death.question.repository;

import com.nexters.death.question.entity.QuestionOption;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuestionOptionRepository extends JpaRepository<QuestionOption, Long> {

    @Query("""
        select qo from QuestionOption qo
        join fetch qo.question q
        join fetch q.category
        where qo.id in :ids
        """)
    List<QuestionOption> findAllWithQuestionByIdIn(@Param("ids") Collection<Long> ids);

    List<QuestionOption> findAllByQuestionIdInOrderByPositiveDescIdAsc(Collection<Long> questionIds);
}
