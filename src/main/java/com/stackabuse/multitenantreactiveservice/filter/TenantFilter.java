package com.stackabuse.multitenantreactiveservice.filter;

import com.stackabuse.multitenantreactiveservice.service.TenantService;
import com.stackabuse.multitenantreactiveservice.util.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class TenantFilter implements WebFilter {

    private final String defaultTenant;
    private final TenantService tenantService;
    private static final String  TENANT_AUTH_HEADER = "X-TENANT-KEY";

    @Autowired
    public TenantFilter(
            @Value("${spring.data.mongodb.database:master}")
                    String defaultTenant,
            TenantService tenantService) {
        this.defaultTenant = defaultTenant;
        this.tenantService = tenantService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        List<String> tenantIdHeader = exchange.getRequest().getHeaders().getOrEmpty(TENANT_AUTH_HEADER);
        if (tenantIdHeader.isEmpty() || tenantIdHeader.get(0).equals(defaultTenant)) {
            TenantContext.setTenantId(defaultTenant);
            return chain.filter(exchange).doOnSuccessOrError((Void v, Throwable throwable) -> TenantContext.clear());
        } else if (Boolean.TRUE.equals(tenantService.isTenantExists(tenantIdHeader.get(0)).share().block())) {
            TenantContext.setTenantId(tenantIdHeader.get(0));
            return chain.filter(exchange).doOnSuccessOrError((Void v, Throwable throwable) -> TenantContext.clear());
        }

        // if tenant is not valid
        exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
        return exchange.getResponse().setComplete();
    }
}
