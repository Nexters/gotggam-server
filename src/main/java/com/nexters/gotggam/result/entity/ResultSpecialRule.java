package com.nexters.gotggam.result.entity;

import com.nexters.gotggam.question.entity.SpecialRule;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Table(
    name = "result_special_rule",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"result_id", "special_rule_id"}),
        @UniqueConstraint(columnNames = {"result_id", "display_order"})
    }
)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResultSpecialRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_id", nullable = false)
    private Result result;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "special_rule_id", nullable = false)
    private SpecialRule specialRule;

    @Column(name = "display_order", nullable = false)
    private Short displayOrder;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private ResultSpecialRule(Result result, SpecialRule specialRule, Short displayOrder) {
        this.result = result;
        this.specialRule = specialRule;
        this.displayOrder = displayOrder;
    }

}
