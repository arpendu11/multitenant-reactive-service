package com.stackabuse.multitenantreactiveservice.service;

import com.stackabuse.multitenantreactiveservice.dto.CreateUserDTO;
import com.stackabuse.multitenantreactiveservice.dto.UpdateUserDTO;
import com.stackabuse.multitenantreactiveservice.entity.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {

    Flux<User> getUsers();

    Mono<User> getUser(String id);

    Mono<User> createUser(CreateUserDTO createUserDTO);

    Mono<User> updateUser(String id, UpdateUserDTO updateUserDTO) throws Exception;

    Mono<Boolean> deleteUserById(String id) throws Exception;
}
