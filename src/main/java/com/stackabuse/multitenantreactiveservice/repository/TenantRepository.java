package com.stackabuse.multitenantreactiveservice.repository;

import com.stackabuse.multitenantreactiveservice.entity.Tenant;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface TenantRepository extends ReactiveMongoRepository<Tenant, String> {
    Mono<Tenant> findByKey(String tenantKey);
}
