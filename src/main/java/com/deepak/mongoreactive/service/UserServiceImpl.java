package com.deepak.mongoreactive.service;


import com.deepak.mongoreactive.controller.UserController;
import com.deepak.mongoreactive.exception.UserNotFoundException;
import com.deepak.mongoreactive.models.AppointmentDetails;
import com.deepak.mongoreactive.models.User;
import com.deepak.mongoreactive.repository.UserRepository;
import com.mongodb.client.result.DeleteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final UserRepository userRepository;
    private final ReactiveMongoTemplate template;

    public UserServiceImpl(UserRepository userRepository, ReactiveMongoTemplate template) {
        this.userRepository = userRepository;
        this.template = template;
    }

    @Override
    public Mono<User> getUserById(String id) {
        LOGGER.info("Attempting to retrieve user with ID: " + id);

        return this.userRepository.findById(id)
                .doOnNext(user -> LOGGER.info("User retrieved successfully: " + user.toString()))
                .doOnError(error -> LOGGER.error("Error occurred while fetching user: " + error.getMessage()))
                .doOnCancel(() -> LOGGER.warn("User retrieval cancelled"))
                .doFinally(signalType -> LOGGER.debug("User retrieval completed with signal: " + signalType));
    }

    @Override
    public Flux<User> getUsers() {
        LOGGER.info("Attempting to retrieve all users ");
        return userRepository.findAll()
                .doOnNext(user -> LOGGER.info("Users retrieved successfully"))
                .doOnError(error -> LOGGER.error("Error occurred while fetching user: " + error.getMessage()))
                .doOnCancel(() -> LOGGER.warn("Users retrieval cancelled"))
                .doFinally(signalType -> LOGGER.debug("User retrieval completed with signal: " + signalType));
    }

    @Override
    public Mono<User> saveUser(User userDTO) {
        return userRepository.save(userDTO)
                .doOnNext(user -> LOGGER.info("User saved successfully"))
                .doOnError(error -> LOGGER.error("Error occurred while saving user: " + error.getMessage()))
                .doOnCancel(() -> LOGGER.warn("Users saving cancelled"))
                .doFinally(signalType -> LOGGER.debug("User saving completed with signal: " + signalType));
    }

    public Mono<User> updateUser(String id, User userDTO) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User with id " + id + " not found")))
                .flatMap(existingUser -> {
                    existingUser.setFirstName(userDTO.getFirstName() != null ? userDTO.getFirstName() : existingUser.getFirstName());
                    existingUser.setLastName(userDTO.getLastName() != null ? userDTO.getLastName() : existingUser.getLastName());
                    existingUser.setPhoneNumber(userDTO.getPhoneNumber() != null ? userDTO.getPhoneNumber() : existingUser.getPhoneNumber());
                    existingUser.setAge(userDTO.getAge() != 0 ? userDTO.getAge() : existingUser.getAge());
                    existingUser.setEmail(userDTO.getEmail() != null ? userDTO.getEmail() : existingUser.getEmail());

                    List<AppointmentDetails> appointmentDetailsList = existingUser.getAppointmentDetails();
                    List<AppointmentDetails> dtoAppointmentDetailsList = userDTO.getAppointmentDetails();

                    if (appointmentDetailsList.size() == dtoAppointmentDetailsList.size()) {
                        int index = 0;
                        for (AppointmentDetails appointmentDetails : appointmentDetailsList) {
                            AppointmentDetails dtoAppointmentDetails = dtoAppointmentDetailsList.get(index);
                            // Set appointment details if not null
                            appointmentDetails.setAppointmentType(dtoAppointmentDetails.getAppointmentType());
                            appointmentDetails.setAppointmentFor(dtoAppointmentDetails.getAppointmentFor());
                            appointmentDetails.setAppointmentForName(dtoAppointmentDetails.getAppointmentForName());
                            appointmentDetails.setAppointmentForAge(dtoAppointmentDetails.getAppointmentForAge());
                            appointmentDetails.setSymptom(dtoAppointmentDetails.getSymptom());
                            appointmentDetails.setOtherSymptoms(dtoAppointmentDetails.getOtherSymptoms());
                            appointmentDetails.setAppointmentDate(dtoAppointmentDetails.getAppointmentDate());
                            appointmentDetails.setDoctorName(dtoAppointmentDetails.getDoctorName());
                            appointmentDetails.setClinicId(dtoAppointmentDetails.getClinicId());
                            index++;
                        }
                    } else {
                        // Add new appointment details
                        for (AppointmentDetails dtoAppointmentDetails : dtoAppointmentDetailsList) {
                            if (!appointmentDetailsList.contains(dtoAppointmentDetails)) {
                                appointmentDetailsList.add(dtoAppointmentDetails);
                            }
                        }
                    }
                    return userRepository.save(existingUser)
                            .doOnNext(user -> LOGGER.info("User updated successfully"))
                            .doOnError(error -> LOGGER.error("Error occurred while updating user: " + error.getMessage()))
                            .doOnCancel(() -> LOGGER.warn("User updating cancelled"))
                            .doFinally(signalType -> LOGGER.debug("User update completed with signal: " + signalType));
                });
    }


    @Override
    public Mono<Void> deleteUser(String id) {
        return userRepository.deleteById(id)
                .doOnNext(user -> LOGGER.info("User deleted successfully"))
                .doOnError(error -> LOGGER.error("Error occurred while deleting user: " + error.getMessage()))
                .doOnCancel(() -> LOGGER.warn("User delete cancelled"))
                .doFinally(signalType -> LOGGER.debug("User delete completed with signal: " + signalType));
    }

    @Override
    public Mono<Long> deleteByName(String name) {
        return template.remove(query(where("name").is(name)), User.class).map(DeleteResult::getDeletedCount);
    }
}