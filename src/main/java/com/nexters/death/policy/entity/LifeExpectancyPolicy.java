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

@Getter
@Entity
@Table(name = "life_expectancy_policy")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LifeExpectancyPolicy extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "male_expectancy", nullable = false, precision = 5, scale = 2)
    private BigDecimal maleExpectancy;

    @Column(name = "female_expectancy", nullable = false, precision = 5, scale = 2)
    private BigDecimal femaleExpectancy;

    @Column(name = "min_remaining_life", nullable = false, precision = 5, scale = 2)
    private BigDecimal minRemainingLife;

    public LifeExpectancyPolicy(
        BigDecimal maleExpectancy,
        BigDecimal femaleExpectancy,
        BigDecimal minRemainingLife
    ) {
        this.maleExpectancy = maleExpectancy;
        this.femaleExpectancy = femaleExpectancy;
        this.minRemainingLife = minRemainingLife;
    }

}
