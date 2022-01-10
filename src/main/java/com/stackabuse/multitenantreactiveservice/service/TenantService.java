package com.stackabuse.multitenantreactiveservice.service;

import com.stackabuse.multitenantreactiveservice.dto.TenantRegistrationDTO;
import com.stackabuse.multitenantreactiveservice.entity.Tenant;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Tenant Registration and Namespace Management Service
 */
public interface TenantService {

    Mono<Boolean> isTenantExists(String tenantKey);

    /**
     * Tenant Registration Layer
     *
     * Description: This service will register Tenant in the global namespace,
     * create tenant based namespace and migrate required objects to the newly
     * created tenant namespace.
     */
    Mono<Tenant> registerTenant(TenantRegistrationDTO tenantRegistrationDTO) throws Exception;

    /**
     * Tenant Fetch Layer
     *
     * Description: This service will fetch all the tenants being registered along
     * with the activation and deactivation information.
     */
    Flux<Tenant> getAllTenants();

    /**
     * Tenant Deactivation Layer
     *
     * Description: This service will deactivate Tenant and restrict access to its
     * namespace contents.
     */
    Mono<Boolean> deactivateTenant(String tenantKey) throws Exception;

    /**
     * Tenant Activation Layer
     *
     * Description: This service will activate a deactivated tenant being registered and
     * regain all the access to its namespace.
     */
    Mono<Boolean> activateTenant(String tenantKey) throws Exception;

    /**
     * Tenant Fetch Layer
     *
     * Description: This service will fetch a tenant by tenant key that is being registered along
     * with the activation and deactivation information.
     */
    Tenant findTenant(String key);
}
