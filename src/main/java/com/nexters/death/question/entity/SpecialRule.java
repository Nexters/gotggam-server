package com.nexters.death.question.entity;

import com.nexters.death.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "special_rule")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SpecialRule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false, unique = true)
    private Question question;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    public SpecialRule(Question question, String description) {
        this.question = question;
        this.description = description;
    }

}
