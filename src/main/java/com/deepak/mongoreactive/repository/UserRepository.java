package com.deepak.mongoreactive.repository;

import com.deepak.mongoreactive.models.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<User, String> {

    Mono<User> findByPhoneNumber(String phoneNumber);

    Mono<User> findById(String id);

}