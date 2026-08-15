package com.nexters.death.result.client;

import java.math.BigDecimal;

public record WarningMessageRequest(
    String name,
    BigDecimal bodyPenalty,
    BigDecimal mindPenalty,
    BigDecimal attitudePenalty
) {
}
