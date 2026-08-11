package com.nexters.death.result.entity;

import com.nexters.death.global.entity.BaseCreatedAtEntity;
import com.nexters.death.question.entity.Question;
import com.nexters.death.question.entity.QuestionOption;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
    name = "result_answer",
    uniqueConstraints = @UniqueConstraint(columnNames = {"result_id", "question_id"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResultAnswer extends BaseCreatedAtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_id", nullable = false)
    private Result result;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    // option이 question과 같은 질문에 속하는지는 DB 제약이 아니라 서비스 계층에서 검증한다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", nullable = false)
    private QuestionOption option;

    public ResultAnswer(Result result, Question question, QuestionOption option) {
        this.result = result;
        this.question = question;
        this.option = option;
    }

}
