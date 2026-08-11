package com.nexters.death.policy.entity;

import com.nexters.death.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 연령 구간(start_age ~ end_age)이 다른 행과 겹치면 안 되는데, DB 제약만으로는
// 표현할 수 없어 서비스 계층에서 검증해야 한다.
@Getter
@Entity
@Table(name = "age_weight")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgeWeight extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_age", nullable = false)
    private Short startAge;

    @Column(name = "end_age", nullable = false)
    private Short endAge;

    @Column(name = "body_weight", nullable = false, precision = 3, scale = 2)
    private BigDecimal bodyWeight;

    @Column(name = "mind_weight", nullable = false, precision = 3, scale = 2)
    private BigDecimal mindWeight;

    @Column(name = "attitude_weight", nullable = false, precision = 3, scale = 2)
    private BigDecimal attitudeWeight;

    public AgeWeight(
        Short startAge,
        Short endAge,
        BigDecimal bodyWeight,
        BigDecimal mindWeight,
        BigDecimal attitudeWeight
    ) {
        this.startAge = startAge;
        this.endAge = endAge;
        this.bodyWeight = bodyWeight;
        this.mindWeight = mindWeight;
        this.attitudeWeight = attitudeWeight;
    }

}
