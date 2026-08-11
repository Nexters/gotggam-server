package com.nexters.death.result.entity;

import com.nexters.death.global.entity.BaseCreatedAtEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "result")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Result extends BaseCreatedAtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "share_token", nullable = false, unique = true, updatable = false)
    private UUID shareToken;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Gender gender;

    @Column(name = "base_life", nullable = false, precision = 5, scale = 2)
    private BigDecimal baseLife;

    @Column(name = "body_penalty", nullable = false, precision = 5, scale = 2)
    private BigDecimal bodyPenalty;

    @Column(name = "mind_penalty", nullable = false, precision = 5, scale = 2)
    private BigDecimal mindPenalty;

    @Column(name = "attitude_penalty", nullable = false, precision = 5, scale = 2)
    private BigDecimal attitudePenalty;

    // 3개 영역(body/mind/attitude) 페널티 합계. 저장 시점에 계산해서 넣는다.
    @Column(name = "total_penalty", nullable = false, precision = 5, scale = 2)
    private BigDecimal totalPenalty;

    @Column(name = "expected_life", nullable = false, precision = 5, scale = 2)
    private BigDecimal expectedLife;

    @Column(name = "today_message", length = 255)
    private String todayMessage;

    @Column(name = "warning_message", length = 255)
    private String warningMessage;

    public Result(
        String name,
        LocalDate birthDate,
        Gender gender,
        BigDecimal baseLife,
        BigDecimal bodyPenalty,
        BigDecimal mindPenalty,
        BigDecimal attitudePenalty,
        BigDecimal expectedLife,
        String todayMessage,
        String warningMessage
    ) {
        this.shareToken = UUID.randomUUID();
        this.name = name;
        this.birthDate = birthDate;
        this.gender = gender;
        this.baseLife = baseLife;
        this.bodyPenalty = bodyPenalty;
        this.mindPenalty = mindPenalty;
        this.attitudePenalty = attitudePenalty;
        this.totalPenalty = bodyPenalty.add(mindPenalty).add(attitudePenalty);
        this.expectedLife = expectedLife;
        this.todayMessage = todayMessage;
        this.warningMessage = warningMessage;
    }

}
