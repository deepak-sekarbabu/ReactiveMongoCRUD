package com.deepak.mongoreactive.repository;

import com.deepak.mongoreactive.models.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface UserRepository extends ReactiveMongoRepository<User, String> {


    Flux<User> findAllBy(Pageable pageable);

    Mono<User> findByPhoneNumber(String phoneNumber);

    Mono<User> findById(String id);

    Flux<User> findByAppointmentDetailsAppointmentDateBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);
}
