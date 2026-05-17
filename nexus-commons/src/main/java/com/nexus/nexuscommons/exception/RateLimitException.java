package com.nexus.nexuscommons.exception;

public class RateLimitException extends NexusException{

    private final String clientId;
    private final int limitRpm;

    public RateLimitException(String clienteId, int limitRpm) {
        super("RATE_LIMIT_EXCEEDED",
                String.format("Client '%s' exceeded rate limit of %d rpm", clienteId, limitRpm));
        this.clientId = clienteId;
        this.limitRpm = limitRpm;
    }

    public String getClientId() { return clientId; }
    public int getLimitRpm() { return limitRpm; }
}
