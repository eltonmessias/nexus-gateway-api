package com.nexus.nexusgateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        long start   = System.currentTimeMillis();
        String path  = exchange.getRequest().getPath().value();
        String method = exchange.getRequest().getMethod().name();
        String traceId = exchange.getRequest().getHeaders().getFirst("X-Trace-Id");

        return chain.filter(exchange).doFinally(signal -> {
            long duration  = System.currentTimeMillis() - start;
            int statusCode = exchange.getResponse().getStatusCode() != null
                    ? exchange.getResponse().getStatusCode().value() : 0;

            log.info("method={} path={} status={} duration={}ms traceId={}",
                    method, path, statusCode, duration, traceId);
        });
    }

    @Override
    public int getOrder() { return -1; }
}
