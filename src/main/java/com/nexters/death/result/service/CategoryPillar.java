package com.nexters.death.result.service;

import com.nexters.death.global.exception.BusinessException;
import com.nexters.death.result.exception.ResultErrorCode;

public enum CategoryPillar {

    BODY,
    MIND,
    ATTITUDE;

    public static CategoryPillar from(String categoryKey) {
        try {
            return CategoryPillar.valueOf(categoryKey);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ResultErrorCode.UNKNOWN_CATEGORY_PILLAR);
        }
    }
}
