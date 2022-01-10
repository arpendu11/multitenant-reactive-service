package com.stackabuse.multitenantreactiveservice.config.mongo.tenant;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.stackabuse.multitenantreactiveservice.config.mongo.MongoProperties;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoClientFactoryBean;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Collections;

@Configuration
@AllArgsConstructor
@EnableMongoRepositories(basePackages = {"com.stackabuse.multitenantnosqlservice.repository"})
public class TenantMongoConfig {

    private final MongoProperties mongoProperties;

    @Bean
    public MongoClient getMongoClient() {
        MongoCredential credential = MongoCredential.createCredential(
                mongoProperties.getUsername(),
                mongoProperties.getDataBaseName(),
                mongoProperties.getPassword().toCharArray());
        return MongoClients.create(MongoClientSettings.builder()
                .applyToClusterSettings(builder ->
                        builder.hosts(
                                Collections.singletonList(
                                        new ServerAddress(
                                                mongoProperties.getHost(),
                                                Integer.parseInt(mongoProperties.getPort())))))
                .credential(credential)
                .build());
    }

    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate(TenantReactiveMongoDatabaseFactory tenantReactiveMongoDatabaseFactory) {
        return new ReactiveMongoTemplate(tenantReactiveMongoDatabaseFactory);
    }

    @Bean
    public TenantReactiveMongoDatabaseFactory tenantReactiveMongoDatabaseFactory(MongoClient mongoClient) {
        return new TenantReactiveMongoDatabaseFactory(getMongoClient(), mongoProperties.getDataBaseName());
    }

    @Bean
    public ReactiveMongoClientFactoryBean mongoClient() {
        ReactiveMongoClientFactoryBean clientFactory = new ReactiveMongoClientFactoryBean();
        clientFactory.setConnectionString(mongoProperties.getUri());
        return clientFactory;
    }
}
