package com.nexters.gotggam.question.entity;

import com.nexters.gotggam.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "act")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Act extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String code;

    @Column(nullable = false, length = 30)
    private String label;

    @Column(name = "display_order", nullable = false, unique = true)
    private Integer displayOrder;

    @Builder
    private Act(String code, String label, Integer displayOrder) {
        this.code = code;
        this.label = label;
        this.displayOrder = displayOrder;
    }

}
