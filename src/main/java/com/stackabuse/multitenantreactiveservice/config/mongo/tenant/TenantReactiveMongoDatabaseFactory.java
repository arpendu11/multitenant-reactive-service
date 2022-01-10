package com.stackabuse.multitenantreactiveservice.config.mongo.tenant;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoDatabase;
import com.stackabuse.multitenantreactiveservice.util.TenantContext;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;
import reactor.core.publisher.Mono;

import java.util.Objects;

public final class TenantReactiveMongoDatabaseFactory extends SimpleReactiveMongoDatabaseFactory {

    private final String databaseName;

    public TenantReactiveMongoDatabaseFactory(MongoClient mongoClient, String databaseName) {
        super(mongoClient, databaseName);
        this.databaseName = databaseName;
    }

    @Override
    public Mono<MongoDatabase> getMongoDatabase() throws DataAccessException {
        return super.getMongoDatabase(getTenantId());
    }

    public String getTenantId() {
        String tenantId = TenantContext.getTenantId();
        if (Objects.nonNull(tenantId)) {
            return tenantId;
        } else {
            return databaseName;
        }
    }
}
