package org.h3kar360.config;

import org.h3kar360.filter.ProxyKeyAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.DispatchExceptionHandler;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {
    @Bean
    public SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) {
        ProxyKeyAuthFilter proxyKeyAuthFilter = new ProxyKeyAuthFilter();

        return http.csrf(csrf -> csrf.disable())
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/api/v1/admin/**").permitAll()
                        .pathMatchers("/proxy/v1/gateway/**").authenticated()
                        .anyExchange().denyAll()
                )
                .addFilterBefore(((exchange, chain) -> {
                    if(exchange.getRequest().getURI().getPath().startsWith("/proxy/")) {
                        return proxyKeyAuthFilter.filter(exchange, chain);
                    }

                    return chain.filter(exchange);
                }), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}
