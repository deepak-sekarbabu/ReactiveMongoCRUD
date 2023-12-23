package com.deepak.mongoreactive.service;

import com.deepak.mongoreactive.models.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<User> getUserById(String id);

    Flux<User> getUsers();

    Mono<User> saveUser(User userDtoMono);

    Mono<User> updateUser(String id, User userMono);

    Mono<Void> deleteUser(String id);

    Mono<Long> deleteByName(String name);

    Mono<User> findByMobileNumber(String phoneNumber);
}