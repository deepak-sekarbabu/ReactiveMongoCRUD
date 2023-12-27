package com.deepak.mongoreactive.service;

import com.deepak.mongoreactive.exception.models.UserAlreadyExistsException;
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

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final ReactiveMongoTemplate template;

    public UserServiceImpl(UserRepository userRepository,
                           ReactiveMongoTemplate template) {
        this.userRepository = userRepository;
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

    public Mono<User> saveUser(Mono<User> userDTO) {
        return userDTO.flatMap(dto -> {
            String userPhoneNumber = dto.getPhoneNumber();
            return this.userRepository.findByPhoneNumber(userPhoneNumber)
                    .flatMap(existingUser -> Mono.error(new UserAlreadyExistsException(
                            "User with phone number " + userPhoneNumber
                                    + " already exists")))
                    .switchIfEmpty(Mono.defer(() -> {

                        // Generate custom appointment IDs for each appointment in the user's
                        // details
                        if (dto.getAppointmentDetails() != null) {
                            dto.getAppointmentDetails()
                                    .forEach(appointment -> appointment
                                            .generateCustomAppointmentId(
                                                    userPhoneNumber,
                                                    appointment.getAppointmentForName()));
                        }

                        return this.userRepository.save(dto)
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
        });
    }

    public Mono<User> updateUserInformation(String id, Mono<User> userDTOMono) {
        return userDTOMono.flatMap(userDTO -> this.userRepository.findById(id)
                .flatMap(existingUser -> {
                    existingUser.setFirstName(userDTO.getFirstName());
                    existingUser.setLastName(userDTO.getLastName());
                    existingUser.setDateOfBirth(userDTO.getDateOfBirth());
                    existingUser.setEmail(userDTO.getEmail());
                    existingUser.setPhoneNumber(userDTO.getPhoneNumber());

                    return this.userRepository.save(existingUser)
                            .doOnSuccess(user -> LOGGER.info(
                                    "User information updated successfully for user with ID: {}",
                                    id))
                            .doOnError(error -> LOGGER.error(
                                    "Error occurred while updating user information for user with ID {}: {}",
                                    id, error.getMessage()))
                            .thenReturn(existingUser.withoutAppointmentDetails()); // Return the updated user information without appointmentDetails
                }));
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
                .map(DeleteResult::getDeletedCount)
                .doOnSuccess(deletedCount -> LOGGER.info("{} user(s) deleted by name: {}", deletedCount,
                        name))
                .doOnError(error -> LOGGER.error("Error occurred while deleting user(s) by name {}: {}",
                        name, error.getMessage()));
    }

    @Override
    public Mono<User> findByPhoneNumber(String phoneNumber) {
        return this.template.findOne(
                        Query.query(Criteria.where("phoneNumber").is(phoneNumber)),
                        User.class)
                .doOnNext(user -> {
                    if (user != null) {
                        LOGGER.info("User found with phone number {}: {}", phoneNumber, user);
                    } else {
                        LOGGER.warn("No user found with phone number: {}", phoneNumber);
                    }
                });
    }

    @Override
    public Mono<String> findUserIdByPhoneNumber(String phoneNumber) {
        return this.userRepository.findByPhoneNumber(phoneNumber)
                .map(User::getId)
                .doOnNext(userId -> {
                    if (userId != null) {
                        LOGGER.info("User ID {} found for phone number {}", userId,
                                phoneNumber);
                    } else {
                        LOGGER.warn("No user ID found for phone number {}", phoneNumber);
                    }
                });
    }


}