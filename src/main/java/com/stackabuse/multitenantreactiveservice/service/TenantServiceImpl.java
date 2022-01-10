package com.stackabuse.multitenantreactiveservice.service;

import com.stackabuse.multitenantreactiveservice.dto.TenantRegistrationDTO;
import com.stackabuse.multitenantreactiveservice.entity.Tenant;
import com.stackabuse.multitenantreactiveservice.repository.TenantRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.MessageFormat;
import java.util.Objects;

@Slf4j
@Service
@AllArgsConstructor
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;

    private static final String VALID_SCHEMA_NAME_REGEXP = "^[a-z0-9]{1,8}$";

    @Override
    public Mono<Tenant> registerTenant(TenantRegistrationDTO tenantRegistrationDTO) throws Exception {

        log.info("Check if the Tenant already exists");
        String tenantKey = tenantRegistrationDTO.getKey();

        // The schema 'master' by default will be present and 'master' tenant creation is not allowed
        if (Objects.nonNull(findTenant(tenantKey)) || tenantKey.equals("master")) {
            throw new Exception(MessageFormat.format("Tenant with the key {0} already exists in the system", tenantKey));
        }

        log.info("Verify tenant key string to prevent SQL injection: {}", tenantKey);
        if (!tenantKey.matches(VALID_SCHEMA_NAME_REGEXP)) {
            throw new Exception(MessageFormat.format("Invalid schema name: {}",  tenantKey));
        }

        log.info("Convert Tenant DTO to Tenant data object: {}", tenantRegistrationDTO);
        Tenant newTenant = new Tenant();
        newTenant.setTenantId(tenantRegistrationDTO.getTenantId());
        newTenant.setKey(tenantRegistrationDTO.getKey());
        newTenant.setEnabled(tenantRegistrationDTO.getEnabled());
        newTenant.setCreatedBy("admin");
        newTenant.setCreatedOn(System.currentTimeMillis());
        newTenant.setLastUpdatedBy("admin");
        newTenant.setLastUpdatedOn(System.currentTimeMillis());

        log.info("Save tenant object to the global namespace: {}", newTenant);
        return tenantRepository.save(newTenant);
    }

    @Override
    public Mono<Boolean> isTenantExists(String tenantKey) {
        // check if the schema exists.
        return tenantRepository.findByKey(tenantKey).map(t -> !ObjectUtils.isEmpty(t));
    }

    @Override
    public Flux<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }

    @Override
    public Mono<Boolean> deactivateTenant(String tenantKey) throws Exception {
        log.info("Check if the Tenant already exists: {}", tenantKey);
        Tenant tenant = findTenant(tenantKey);
        if (Objects.isNull(tenant)) {
            throw new Exception(MessageFormat.format("Tenant with the key {0} does not exist in the system", tenantKey));
        }
        tenant.setEnabled(false);
        tenant.setCreatedBy("admin");
        tenant.setCreatedOn(System.currentTimeMillis());
        tenant.setLastUpdatedBy("admin");
        tenant.setLastUpdatedOn(System.currentTimeMillis());
        tenantRepository.save(tenant);
        return Mono.just(true);
    }

    @Override
    public Mono<Boolean> activateTenant(String tenantKey) throws Exception {
        log.info("Check if the Tenant already exists");
        Tenant tenant = findTenant(tenantKey);
        if (Objects.isNull(tenant)) {
            throw new Exception(MessageFormat.format("Tenant with the key {} doesn't exist in the system", tenantKey));
        }
        tenant.setEnabled(true);
        tenant.setCreatedBy("admin");
        tenant.setCreatedOn(System.currentTimeMillis());
        tenant.setLastUpdatedBy("admin");
        tenant.setLastUpdatedOn(System.currentTimeMillis());
        tenantRepository.save(tenant);
        return Mono.just(true);
    }

    @Override
    public Tenant findTenant(String key) {
        return tenantRepository.findByKey(key).share().block();
    }
}
