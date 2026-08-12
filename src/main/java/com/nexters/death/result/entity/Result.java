package com.nexters.death.result.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Table(name = "result")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Result {

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
    // 정책상 합계가 999.99를 넘지는 않지만, 안전하게 precision을 한 자리 더 여유있게 잡는다.
    @Column(name = "total_penalty", nullable = false, precision = 6, scale = 2)
    private BigDecimal totalPenalty;

    @Column(name = "expected_life", nullable = false, precision = 5, scale = 2)
    private BigDecimal expectedLife;

    @Column(name = "today_message", length = 255)
    private String todayMessage;

    @Column(name = "warning_message", length = 255)
    private String warningMessage;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

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
