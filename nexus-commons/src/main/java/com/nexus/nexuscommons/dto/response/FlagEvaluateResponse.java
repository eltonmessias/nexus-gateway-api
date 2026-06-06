package com.nexus.nexuscommons.dto.response;

public record FlagEvaluateResponse(
        String flagKey,
        String value,
        String reason
) {

}
