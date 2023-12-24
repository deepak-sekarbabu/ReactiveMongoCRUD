package com.deepak.mongoreactive.service;

import com.deepak.mongoreactive.exception.UserAlreadyExistsException;
import com.deepak.mongoreactive.models.AppointmentDetails;
import com.deepak.mongoreactive.models.User;
import com.deepak.mongoreactive.repository.UserRepository;
import com.mongodb.client.result.DeleteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final UserServiceUtils userServiceUtils;
    private final ReactiveMongoTemplate template;

    public UserServiceImpl(UserRepository userRepository, UserServiceUtils userServiceUtils,
                           ReactiveMongoTemplate template) {
        this.userRepository = userRepository;
        this.userServiceUtils = userServiceUtils;
        this.template = template;
    }

    @Override
    public Mono<User> getUserById(String id) {
        LOGGER.info("Attempting to retrieve user with ID: {}", id);
        return this.userRepository.findById(id)
                .doOnNext(user -> LOGGER.info("User retrieved successfully: {}", user))
                .doOnError(error -> LOGGER.error("Error occurred while fetching user: {}",
                        error.getMessage()))
                .doOnCancel(() -> LOGGER.warn("User retrieval cancelled"))
                .doFinally(signalType -> LOGGER.debug("User retrieval completed with signal: {}",
                        signalType));
    }

    @Override
    public Flux<User> getUsers(int limit, int offset) {
        LOGGER.info("Attempting to retrieve users with limit {} and offset {}", limit, offset);

        return this.userRepository.findAll()
                .skip(offset)
                .take(limit)
                .doOnNext(user -> LOGGER.info("User retrieved successfully: {}", user.getId()))
                .doOnError(error -> LOGGER.error("Error occurred while fetching users: {}",
                        error.getMessage()))
                .doOnCancel(() -> LOGGER.warn("User retrieval cancelled"))
                .doFinally(signalType -> LOGGER.debug("User retrieval completed with signal: {}",
                        signalType));
    }

    public Mono<User> saveUser(User userDTO) {
        String userPhoneNumber = userDTO.getPhoneNumber();
        return this.userRepository.findByPhoneNumber(userPhoneNumber)
                .flatMap(existingUser -> Mono.error(new UserAlreadyExistsException(
                        "User with phone number " + userPhoneNumber + " already exists")))
                .switchIfEmpty(Mono.defer(() -> {

                    // Generate custom appointment IDs for each appointment in the user's details
                    if (userDTO.getAppointmentDetails() != null) {
                        userDTO.getAppointmentDetails()
                                .forEach(appointment -> appointment
                                        .generateCustomAppointmentId(
                                                userPhoneNumber,
                                                appointment.getAppointmentForName()));
                    }

                    return this.userRepository.save(userDTO)
                            .doOnSuccess(user -> LOGGER.info(
                                    "User saved successfully with ID: {}",
                                    user.getId()))
                            .doOnError(
                                    error -> LOGGER.error(
                                            "Error occurred while saving user: {}",
                                            error.getMessage()))
                            .doOnCancel(() -> LOGGER.warn("User saving cancelled"))
                            .doFinally(signalType -> LOGGER.debug(
                                    "User saving completed with signal: {}",
                                    signalType));
                }))
                .cast(User.class);
    }

    public Mono<User> updateUser(String id, User userDTO) {
        return this.userRepository.findByPhoneNumber(userDTO.getPhoneNumber())
                .flatMap(existingUser -> this.userServiceUtils.handleExistingUser(id, userDTO,
                        existingUser))
                .switchIfEmpty(this.userServiceUtils.updateUserAndSave(id, userDTO));
    }

    @Override
    public Mono<Void> deleteUser(String id) {
        return this.userRepository.deleteById(id)
                .doOnSuccess(user -> LOGGER.info("User deleted successfully with ID: {}", id))
                .doOnError(error -> LOGGER.error("Error occurred while deleting user with ID {}: {}",
                        id,
                        error.getMessage()))
                .doOnCancel(() -> LOGGER.warn("Deletion of user with ID {} cancelled", id))
                .doFinally(signalType -> LOGGER.debug(
                        "Deletion of user with ID {} completed with signal: {}", id,
                        signalType));
    }

    @Override
    public Mono<Long> deleteByName(String name) {
        return this.template.remove(query(where("name").is(name)), User.class)
                .map(DeleteResult::getDeletedCount);
    }

    @Override
    public Mono<User> findByPhoneNumber(String phoneNumber) {
        return this.template.findOne(
                Query.query(Criteria.where("phoneNumber").is(phoneNumber)),
                User.class);

    }

    @Override
    public Mono<User> getUserWithActiveAppointmentsByUserId(String userId) {
        return this.userRepository.findById(userId)
                .map(user -> {
                    user.setAppointmentDetails(
                            user.getAppointmentDetails().stream()
                                    .filter(AppointmentDetails::isActive)
                                    .collect(Collectors.toList()));
                    return user;
                });
    }

    public Mono<User> getUserWithActiveAppointmentsByPhoneNumber(String phoneNumber) {
        return this.userRepository.findByPhoneNumber(phoneNumber)
                .map(user -> {
                    user.setAppointmentDetails(
                            user.getAppointmentDetails().stream()
                                    .filter(AppointmentDetails::isActive)
                                    .collect(Collectors.toList()));
                    return user;
                });
    }

    public Mono<User> cancelAppointmentByPhoneNumber(String phoneNumber, List<String> appointmentIds) {
        return this.userRepository.findByPhoneNumber(phoneNumber)
                .flatMap(user -> {
                    user.getAppointmentDetails()
                            .stream()
                            .filter(appointmentDetails -> appointmentIds.contains(
                                    appointmentDetails.getAppointmentId()))
                            .forEach(appointmentDetails -> appointmentDetails
                                    .setActive(false));

                    return this.userRepository.save(user);
                });
    }

    @Override
    public Mono<User> cancelAppointmentByUserId(String userId, List<String> appointmentIds) {
        return this.userRepository.findById(userId)
                .flatMap(user -> {
                    user.getAppointmentDetails()
                            .stream()
                            .filter(appointmentDetails -> appointmentIds.contains(
                                    appointmentDetails.getAppointmentId()))
                            .forEach(appointmentDetails -> appointmentDetails
                                    .setActive(false));

                    return this.userRepository.save(user);
                });
    }

}