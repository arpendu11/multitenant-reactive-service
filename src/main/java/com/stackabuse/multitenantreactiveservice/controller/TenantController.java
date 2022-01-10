package com.stackabuse.multitenantreactiveservice.controller;

import com.stackabuse.multitenantreactiveservice.dto.TenantRegistrationDTO;
import com.stackabuse.multitenantreactiveservice.entity.Tenant;
import com.stackabuse.multitenantreactiveservice.service.TenantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/tenants")
public class TenantController {

    private final TenantService tenantService;

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Tenant>> registerTenant(
            @Valid @RequestBody TenantRegistrationDTO tenantRegistrationDTO) throws Exception {
        log.info("Registering newly generated tenant..");
        return tenantService.registerTenant(tenantRegistrationDTO).map(ResponseEntity::ok);
    }

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<Tenant>>> getAllTenants() {
        log.info("Fetching all the registered tenants..");
        return tenantService.getAllTenants().collectList().map(ResponseEntity::ok);
    }

    @PutMapping(value = "/{tenantKey}/deactivate")
    public Mono<ResponseEntity<?>> deactivateTenant(@PathVariable(required = true) String tenantKey) throws Exception {
        log.info("Deactivating a given tenant..");
        return tenantService.deactivateTenant(tenantKey).map(ResponseEntity::ok);
    }

    @PutMapping(value = "/{tenantKey}/activate")
    public Mono<ResponseEntity<?>> activateTenant(@PathVariable String tenantKey) throws Exception {
        log.info("Activating a given tenant..");
        return tenantService.activateTenant(tenantKey).map(ResponseEntity::ok);
    }
}
