package com.nexters.death.question.entity;

import com.nexters.death.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "question_option")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuestionOption extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(nullable = false, length = 150)
    private String content;

    @Column(length = 255)
    private String feedback;

    @Column(name = "is_positive", nullable = false)
    private boolean positive;

    @Column(name = "life_penalty", nullable = false, precision = 5, scale = 2)
    private BigDecimal lifePenalty;

    @Builder
    private QuestionOption(
        Question question,
        String content,
        String feedback,
        boolean positive,
        BigDecimal lifePenalty
    ) {
        this.question = question;
        this.content = content;
        this.feedback = feedback;
        this.positive = positive;
        this.lifePenalty = lifePenalty;
    }

}
