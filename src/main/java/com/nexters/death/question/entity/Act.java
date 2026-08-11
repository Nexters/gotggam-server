package com.nexters.death.question.entity;

import com.nexters.death.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
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

    @Column(name = "act_key", nullable = false, unique = true, length = 30)
    private String actKey;

    @Column(nullable = false, length = 30)
    private String label;

    public Act(String actKey, String label) {
        this.actKey = actKey;
        this.label = label;
    }

}
