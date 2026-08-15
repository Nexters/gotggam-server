package com.nexters.death.result.entity;

import com.nexters.death.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "result_character")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResultCharacter extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_id", nullable = false, unique = true)
    private Result result;

    @Column(name = "face_type", nullable = false)
    private Short faceType;

    @Column(name = "hair_type", nullable = false)
    private Short hairType;

    @Column(name = "eye_type", nullable = false)
    private Short eyeType;

    @Column(name = "nose_type", nullable = false)
    private Short noseType;

    @Column(name = "mouth_type", nullable = false)
    private Short mouthType;

    @Builder
    private ResultCharacter(
        Result result,
        Short faceType,
        Short hairType,
        Short eyeType,
        Short noseType,
        Short mouthType
    ) {
        this.result = result;
        this.faceType = faceType;
        this.hairType = hairType;
        this.eyeType = eyeType;
        this.noseType = noseType;
        this.mouthType = mouthType;
    }

}
