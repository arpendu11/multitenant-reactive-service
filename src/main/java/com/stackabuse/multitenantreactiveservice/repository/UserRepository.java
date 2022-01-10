package com.stackabuse.multitenantreactiveservice.repository;

import com.stackabuse.multitenantreactiveservice.entity.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface UserRepository extends ReactiveMongoRepository<User, String> {
}
