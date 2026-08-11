package com.nexters.death.result.entity;

import com.nexters.death.global.entity.BaseCreatedAtEntity;
import com.nexters.death.question.entity.SpecialRule;
import jakarta.persistence.Column;
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
    name = "result_special_rule",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"result_id", "special_rule_id"}),
        @UniqueConstraint(columnNames = {"result_id", "display_order"})
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResultSpecialRule extends BaseCreatedAtEntity {

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

    public ResultSpecialRule(Result result, SpecialRule specialRule, Short displayOrder) {
        this.result = result;
        this.specialRule = specialRule;
        this.displayOrder = displayOrder;
    }

}
