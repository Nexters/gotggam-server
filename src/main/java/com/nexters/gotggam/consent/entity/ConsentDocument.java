package com.nexters.gotggam.consent.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    name = "consent_document",
    uniqueConstraints = @UniqueConstraint(columnNames = {"type", "version"})
)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConsentDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ConsentType type;

    // 현재 v1.0.0 형식. 시행일(날짜) 형식으로 바뀔 수 있음 - 팀 합의 필요
    @Column(nullable = false, length = 20)
    private String version;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // 유형별로 지금 내려줄 문서 한 건만 true. 지난 버전도 동의 근거로 남겨야 하므로 지우지 않고 끈다
    @Column(name = "is_active", nullable = false)
    private boolean active;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private ConsentDocument(
        ConsentType type,
        String version,
        String content,
        boolean active
    ) {
        this.type = type;
        this.version = version;
        this.content = content;
        this.active = active;
    }

}
