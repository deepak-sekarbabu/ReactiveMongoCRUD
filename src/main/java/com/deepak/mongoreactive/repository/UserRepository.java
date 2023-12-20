package com.deepak.mongoreactive.repository;

import com.deepak.mongoreactive.models.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface UserRepository extends ReactiveMongoRepository<User, String> {
}