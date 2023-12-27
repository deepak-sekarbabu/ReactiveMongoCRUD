package com.deepak.mongoreactive.service;

import com.deepak.mongoreactive.models.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<User> getUserById(String id);

    Flux<User> getUsers(int limit, int offset);

    Mono<User> saveUser(Mono<User> userDtoMono);

    Mono<User> updateUserInformation(String id, Mono<User> userMono);

    Mono<Void> deleteUser(String id);

    Mono<Long> deleteByName(String name);

    Mono<User> findByPhoneNumber(String phoneNumber);

    Mono<String> findUserIdByPhoneNumber(String phoneNumber);


}