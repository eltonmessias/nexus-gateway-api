package com.nexus.nexusgateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class TracingFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String traceId = exchange.getRequest().getHeaders().getFirst("X-Trace-Id");

        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
        }

        final String finalTraceId = traceId;

        var mutateRequest = exchange.getRequest().mutate()
                .header("X-Trace-Id", finalTraceId)
                .build();

        exchange.getResponse().getHeaders().add("X-Trace-Id", finalTraceId);

        return chain.filter(exchange.mutate().request(mutateRequest).build());
    }

    @Override
    public int getOrder() {
        return -2;
    }
}
