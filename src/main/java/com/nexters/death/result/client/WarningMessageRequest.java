package com.nexters.death.result.client;

import java.math.BigDecimal;

public record WarningMessageRequest(
    BigDecimal bodyPenalty,
    BigDecimal mindPenalty,
    BigDecimal attitudePenalty
) {
}
