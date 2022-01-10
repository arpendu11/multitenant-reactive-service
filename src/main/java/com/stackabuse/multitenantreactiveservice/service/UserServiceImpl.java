package com.stackabuse.multitenantreactiveservice.service;

import com.stackabuse.multitenantreactiveservice.dto.CreateUserDTO;
import com.stackabuse.multitenantreactiveservice.dto.UpdateUserDTO;
import com.stackabuse.multitenantreactiveservice.entity.User;
import com.stackabuse.multitenantreactiveservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Flux<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<User> getUser(String id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional
    public Mono<User> createUser(CreateUserDTO createUserDTO) {
        User user = new User();
        user.setUsername(createUserDTO.getUsername());
        user.setFirstName(createUserDTO.getFirstName());
        user.setLastName(createUserDTO.getLastName());
        user.setCreatedBy("admin");
        user.setCreatedOn(System.currentTimeMillis());
        user.setLastUpdatedBy("admin");
        user.setLastUpdatedOn(System.currentTimeMillis());
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public Mono<User> updateUser(String id, UpdateUserDTO updateUserDTO) throws Exception {
        User user = userRepository
                .findById(id)
                .share()
                .block();
        if (Objects.nonNull(user)) {
            user.setFirstName(updateUserDTO.getFirstName());
            user.setLastName(updateUserDTO.getLastName());
            return userRepository.save(user);
        }
        throw new Exception("User with id: " + id + " not found in the tenant");
    }

    @Override
    @Transactional
    public Mono<Boolean> deleteUserById(String id) throws Exception {
        User user = userRepository
                .findById(id)
                .share()
                .block();
        if (Objects.nonNull(user)) {
            userRepository.delete(user).subscribe();
            return Mono.just(Boolean.TRUE);
        }
        throw new Exception("User with id: " + id + " not found in the tenant");
    }
}
