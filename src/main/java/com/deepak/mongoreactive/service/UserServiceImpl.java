package com.deepak.mongoreactive.service;

import com.deepak.mongoreactive.exception.UserAlreadyExistsException;
import com.deepak.mongoreactive.exception.UserNotFoundException;
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
    private final ReactiveMongoTemplate template;

    public UserServiceImpl(UserRepository userRepository, ReactiveMongoTemplate template) {
        this.userRepository = userRepository;
        this.template = template;
    }

    @Override
    public Mono<User> getUserById(String id) {
        LOGGER.info("Attempting to retrieve user with ID: {}", id);
        return this.userRepository.findById(id)
                .doOnNext(user -> LOGGER.info("User retrieved successfully: {}", user))
                .doOnError(error -> LOGGER.error("Error occurred while fetching user: {}", error.getMessage()))
                .doOnCancel(() -> LOGGER.warn("User retrieval cancelled"))
                .doFinally(signalType -> LOGGER.debug("User retrieval completed with signal: {}", signalType));
    }

    @Override
    public Flux<User> getUsers() {
        LOGGER.info("Attempting to retrieve all users");
        return this.userRepository.findAll()
                .doOnNext(user -> LOGGER.info("User retrieved successfully: {}", user.getId()))
                .doOnError(error -> LOGGER.error("Error occurred while fetching users: {}", error.getMessage()))
                .doOnCancel(() -> LOGGER.warn("User retrieval cancelled"))
                .doFinally(signalType -> LOGGER.debug("User retrieval completed with signal: {}", signalType));
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
                                .forEach(appointment -> appointment.generateCustomAppointmentId(userPhoneNumber));
                    }

                    return userRepository.save(userDTO)
                            .doOnSuccess(user -> LOGGER.info("User saved successfully with ID: {}", user.getId()))
                            .doOnError(error -> LOGGER.error("Error occurred while saving user: {}", error.getMessage()))
                            .doOnCancel(() -> LOGGER.warn("User saving cancelled"))
                            .doFinally(signalType -> LOGGER.debug("User saving completed with signal: {}", signalType));
                }))
                .cast(User.class);
    }


    public Mono<User> updateUser(String id, User userDTO) {
        return this.userRepository.findById(id)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User with id " + id + " not found")))
                .flatMap(existingUser -> this.updateUserDetails(existingUser, userDTO))
                .flatMap(user -> this.updateAppointmentDetails(user, userDTO))
                .flatMap(this.userRepository::save)
                .doOnSuccess(user -> LOGGER.info("User with ID {} updated successfully", user.getId()))
                .doOnError(error -> LOGGER.error("Error occurred while updating user with ID {}: {}", id,
                        error.getMessage()))
                .doOnCancel(() -> LOGGER.warn("User updating cancelled for ID: {}", id))
                .doFinally(signalType -> LOGGER.debug("User update completed with signal: {}", signalType));
    }

    private Mono<User> updateUserDetails(User existingUser, User userDTO) {
        existingUser
                .setFirstName(userDTO.getFirstName() != null ? userDTO.getFirstName() : existingUser.getFirstName());
        existingUser.setLastName(userDTO.getLastName() != null ? userDTO.getLastName() : existingUser.getLastName());
        existingUser.setPhoneNumber(
                userDTO.getPhoneNumber() != null ? userDTO.getPhoneNumber() : existingUser.getPhoneNumber());
        existingUser.setAge(userDTO.getAge() != 0 ? userDTO.getAge() : existingUser.getAge());
        existingUser.setEmail(userDTO.getEmail() != null ? userDTO.getEmail() : existingUser.getEmail());

        return Mono.just(existingUser);
    }

    private Mono<User> updateAppointmentDetails(User user, User userDTO) {
        String userPhoneNumber = userDTO.getPhoneNumber();
        List<AppointmentDetails> existingDetails = user.getAppointmentDetails();
        List<AppointmentDetails> newDetails = userDTO.getAppointmentDetails();

        if (existingDetails.size() == newDetails.size()) {
            this.updateExistingAppointments(existingDetails, newDetails);
        } else {
            this.addNewAppointments(existingDetails, newDetails, userPhoneNumber);
        }

        return Mono.just(user);
    }

    private void updateExistingAppointments(List<AppointmentDetails> existingDetails,
                                            List<AppointmentDetails> newDetails) {
        for (int i = 0; i < existingDetails.size(); i++) {
            AppointmentDetails existing = existingDetails.get(i);
            AppointmentDetails newDetail = newDetails.get(i);

            // Update existing appointment details
            this.updateAppointment(existing, newDetail);
        }
    }

    private void addNewAppointments(List<AppointmentDetails> existingDetails, List<AppointmentDetails> newDetails,
                                    String phoneNumber) {
        for (AppointmentDetails newDetail : newDetails) {
            if (!existingDetails.contains(newDetail)) {
                if (newDetail.getAppointmentId() == null) {
                    newDetail.generateCustomAppointmentId(phoneNumber);
                }
                existingDetails.add(newDetail);
            }
        }
    }

    private void updateAppointment(AppointmentDetails existing, AppointmentDetails newDetail) {
        existing.setAppointmentType(newDetail.getAppointmentType());
        existing.setAppointmentFor(newDetail.getAppointmentFor());
        existing.setAppointmentForName(newDetail.getAppointmentForName());
        existing.setAppointmentForAge(newDetail.getAppointmentForAge());
        existing.setSymptom(newDetail.getSymptom());
        existing.setOtherSymptoms(newDetail.getOtherSymptoms());
        existing.setAppointmentDate(newDetail.getAppointmentDate());
        existing.setDoctorName(newDetail.getDoctorName());
        existing.setClinicId(newDetail.getClinicId());
    }

    @Override
    public Mono<Void> deleteUser(String id) {
        return this.userRepository.deleteById(id)
                .doOnSuccess(user -> LOGGER.info("User deleted successfully with ID: {}", id))
                .doOnError(error -> LOGGER.error("Error occurred while deleting user with ID {}: {}", id,
                        error.getMessage()))
                .doOnCancel(() -> LOGGER.warn("Deletion of user with ID {} cancelled", id))
                .doFinally(signalType -> LOGGER.debug("Deletion of user with ID {} completed with signal: {}", id,
                        signalType));
    }

    @Override
    public Mono<Long> deleteByName(String name) {
        return this.template.remove(query(where("name").is(name)), User.class).map(DeleteResult::getDeletedCount);
    }

    @Override
    public Mono<User> findByPhoneNumber(String phoneNumber) {
        return this.template.findOne(
                Query.query(Criteria.where("phoneNumber").is(phoneNumber)),
                User.class);

    }

    @Override
    public Mono<User> getUserWithActiveAppointmentsUsingUserId(String userId) {
        return this.userRepository.findById(userId)
                .map(user -> {
                    user.setAppointmentDetails(
                            user.getAppointmentDetails().stream()
                                    .filter(appt -> appt.isActive())
                                    .collect(Collectors.toList()));
                    return user;
                });
    }


    public Mono<User> getUserWithActiveAppointmentsUsingPhoneNumber(String phoneNumber) {
        return this.userRepository.findByPhoneNumber(phoneNumber)
                .map(user -> {
                    user.setAppointmentDetails(
                            user.getAppointmentDetails().stream()
                                    .filter(appt -> appt.isActive())
                                    .collect(Collectors.toList()));
                    return user;
                });
    }

}