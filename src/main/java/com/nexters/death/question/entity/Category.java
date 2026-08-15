package com.nexters.death.question.entity;

import com.nexters.death.global.entity.BaseEntity;
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
@Table(name = "category")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 관리자가 name(한글)을 바꿔도 매핑이 깨지지 않도록, 로직/API에서 참조하는 키.
    @Column(name = "category_key", nullable = false, unique = true, length = 30)
    private String categoryKey;

    @Column(nullable = false, unique = true, length = 30)
    private String name;

    @Column(length = 255)
    private String description;

    @Builder
    private Category(String categoryKey, String name, String description) {
        this.categoryKey = categoryKey;
        this.name = name;
        this.description = description;
    }

}
