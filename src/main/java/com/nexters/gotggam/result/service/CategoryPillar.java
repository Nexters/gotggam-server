package com.nexters.gotggam.result.service;

import com.nexters.gotggam.global.exception.BusinessException;
import com.nexters.gotggam.result.exception.ResultErrorCode;

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
