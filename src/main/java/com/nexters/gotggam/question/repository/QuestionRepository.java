package com.nexters.gotggam.question.repository;

import com.nexters.gotggam.question.entity.Question;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findAllByActiveTrue();

    @Query("""
        select q from Question q
        join fetch q.act a
        where q.active = true
        order by a.displayOrder asc, q.displayOrder asc
        """)
    List<Question> findAllActiveWithActOrderByDisplayOrder();
}
