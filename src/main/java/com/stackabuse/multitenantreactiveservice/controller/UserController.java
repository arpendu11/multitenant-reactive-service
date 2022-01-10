package com.stackabuse.multitenantreactiveservice.controller;

import com.stackabuse.multitenantreactiveservice.dto.CreateUserDTO;
import com.stackabuse.multitenantreactiveservice.dto.UpdateUserDTO;
import com.stackabuse.multitenantreactiveservice.entity.User;
import com.stackabuse.multitenantreactiveservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<User>>> getUsers() {
        return userService.getUsers().collectList().map(ResponseEntity::ok);
    }

    @GetMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<User>> getUser(@PathVariable("userId") String userId) {
        return userService.getUser(userId).map(ResponseEntity::ok);
    }

    @PostMapping(value = "/",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<User>> createUser(@Valid @RequestBody CreateUserDTO createUserDTO) {
        return userService.createUser(createUserDTO).map(ResponseEntity::ok);
    }

    @PutMapping(value = "/{userId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<User>> updateUser(@PathVariable String userId, @Valid @RequestBody UpdateUserDTO updateUserDTO)
            throws Exception {
        return userService.updateUser(userId, updateUserDTO).map(ResponseEntity::ok);
    }

    @DeleteMapping("/{userId}")
    public Mono<ResponseEntity<Boolean>> deleteUser(@PathVariable String userId) throws Exception {
        return userService.deleteUserById(userId).map(ResponseEntity::ok);
    }
}
