package com.nexus.nexusgateway.filter;

import com.nexus.nexusgateway.ratelimit.RateLimiter;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class RateLimitFilter extends AbstractGatewayFilterFactory<RateLimitFilter.Config> {
    private final RateLimiter rateLimiter;

    public RateLimitFilter(RateLimiter rateLimiter) {
        super(Config.class);
        this.rateLimiter = rateLimiter;
    }
    @Override
    public GatewayFilter apply(RateLimitFilter.Config config) {
        return (exchange, chain) -> {
            String clientId = exchange.getRequest()
                    .getHeaders()
                    .getFirst("X-Client-Id");

            if (clientId == null) clientId = exchange.getRequest().getRemoteAddress()
                    .getAddress().getHostAddress();

            final String finalClientId = clientId;

            return rateLimiter.isAllowed(finalClientId, config.getLimit())
                    .flatMap(result -> {
                        // Adiciona headers de rate limit na resposta
                        exchange.getResponse().getHeaders()
                                .add("X-RateLimit-Limit", String.valueOf(result.limit()));
                        exchange.getResponse().getHeaders()
                                .add("X-RateLimit-Remaining", String.valueOf(result.remaining()));

                        if (!result.allowed()) {
                            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                            return exchange.getResponse().setComplete();
                        }

                        return chain.filter(exchange);
                    });
        };
    }

    public static class Config {
        private int limit  = 100;
        private int window = 60;

        public int getLimit()          { return limit; }
        public void setLimit(int l)    { this.limit = l; }
        public int getWindow()         { return window; }
        public void setWindow(int w)   { this.window = w; }
    }
}
