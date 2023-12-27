package com.deepak.mongoreactive.service;

import com.deepak.mongoreactive.exception.models.AppointmnetNotFoundException;
import com.deepak.mongoreactive.models.AppointmentDetails;
import com.deepak.mongoreactive.models.User;
import com.deepak.mongoreactive.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppointmentServiceImpl.class);

    private final UserRepository userRepository;
    private final ReactiveMongoTemplate template;

    public AppointmentServiceImpl(UserRepository userRepository,
                                  ReactiveMongoTemplate template) {
        this.userRepository = userRepository;
        this.template = template;
    }

    @Override
    public Flux<User> getAppointmentsByDate(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return this.userRepository.findByAppointmentDetailsAppointmentDateBetween(startOfDay, endOfDay)
                .doOnNext(user -> LOGGER.info("User with appointment on {} retrieved: {}",
                        date, user))
                .doOnError(error -> LOGGER.error("Error getting users with appointment on {}: {}", date,
                        error.getMessage()))
                .doOnCancel(() -> LOGGER.warn("Get users by appointment date {} cancelled", date))
                .doFinally(signalType -> LOGGER.debug(
                        "Get users by appointment date {} completed with signal: {}", date,
                        signalType));
    }

    @Override
    public Flux<User> getAppointmentsByDateAndIsActive(LocalDate date, boolean active) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        return this.userRepository.findByAppointmentDetailsAppointmentDateBetween(startOfDay, endOfDay)
                .flatMap(user -> {
                    List<AppointmentDetails> activeAppointments = user.getAppointmentDetails()
                            .stream()
                            .filter(appointmentDetails -> appointmentDetails
                                    .isActive() == active) // Filtering active
                            // appointments
                            .toList();

                    if (!activeAppointments.isEmpty()) {
                        user.setAppointmentDetails(activeAppointments); // Update user's active
                        // appointments
                        return Mono.just(user);
                    } else {
                        return Mono.empty(); // If no active appointments found, return empty
                    }
                })
                .doOnNext(user -> LOGGER.info("Active appointments for user on {} retrieved: {}",
                        date, user))
                .doOnError(error -> LOGGER.error("Error getting active appointments on {}: {}",
                        date, error.getMessage()))
                .doOnCancel(() -> LOGGER.warn("Get active appointments on {} cancelled", date))
                .doFinally(signalType -> LOGGER.debug(
                        "Get active appointments on {} completed with signal: {}",
                        date, signalType));
    }

    @Override
    public Mono<User> getUserWithActiveAppointmentsByUserId(String userId) {
        return this.userRepository.findById(userId)
                .map(user -> {
                    user.setAppointmentDetails(
                            user.getAppointmentDetails().stream()
                                    .filter(AppointmentDetails::isActive)
                                    .toList());
                    return user;
                })
                .doOnNext(user -> LOGGER.debug("Retrieved user with active appointments by ID {}: {}",
                        userId, user));
    }

    @Override
    public Mono<User> getUserWithActiveAppointmentsByPhoneNumber(String phoneNumber) {
        return this.userRepository.findByPhoneNumber(phoneNumber)
                .map(user -> {
                    user.setAppointmentDetails(
                            user.getAppointmentDetails().stream()
                                    .filter(AppointmentDetails::isActive)
                                    .toList());
                    return user;
                })
                .doOnNext(user -> LOGGER.debug(
                        "Retrieved user with active appointments by phone number {}: {}",
                        phoneNumber, user));
    }

    @Override
    public Mono<User> cancelAppointmentByPhoneNumber(String phoneNumber, List<String> appointmentIds) {
        return this.userRepository.findByPhoneNumber(phoneNumber)
                .flatMap(user -> {
                    user.getAppointmentDetails()
                            .stream()
                            .filter(appointmentDetails -> appointmentIds.contains(
                                    appointmentDetails.getAppointmentId()))
                            .forEach(appointmentDetails -> appointmentDetails
                                    .setActive(false));

                    return this.userRepository.save(user)
                            .doOnSuccess(savedUser -> LOGGER.info(
                                    "Cancelled appointments for user with phone number {}: {}",
                                    phoneNumber, savedUser))
                            .doOnError(error -> LOGGER.error(
                                    "Error occurred while cancelling appointments for user with phone number {}: {}",
                                    phoneNumber, error.getMessage()));
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

                    return this.userRepository.save(user)
                            .doOnSuccess(savedUser -> LOGGER.info(
                                    "Cancelled appointments for user with ID {}: {}",
                                    userId, savedUser))
                            .doOnError(error -> LOGGER.error(
                                    "Error occurred while cancelling appointments for user with ID {}: {}",
                                    userId, error.getMessage()));
                });
    }

    @Override
    public Mono<List<AppointmentDetails>> createAppointmentsByUserId(String userId, List<AppointmentDetails> appointmentDetails) {
        return this.userRepository.findById(userId)
                .flatMap(user -> {
                    String phoneNumber = user.getPhoneNumber();

                    // Generate custom appointment IDs for each appointment detail
                    appointmentDetails.forEach(
                            ad -> ad.generateCustomAppointmentId(phoneNumber, ad.getAppointmentForName())
                    );

                    // Add new appointment details to the user
                    user.getAppointmentDetails().addAll(appointmentDetails);

                    // Save the user with updated appointment details
                    return this.userRepository.save(user)
                            .doOnSuccess(savedUser -> LOGGER.info(
                                    "Added appointments for user with ID {}: {}", userId, savedUser))
                            .doOnError(error -> LOGGER.error(
                                    "Error occurred while adding appointments for user with ID {}: {}",
                                    userId, error.getMessage()))
                            .thenReturn(appointmentDetails); // Return the newly created appointment details
                });
    }

    @Override
    public Mono<List<AppointmentDetails>> updateAppointmentsByUserId(String userId, List<AppointmentDetails> appointmentDetails) {
        return this.userRepository.findById(userId)
                .flatMap(user -> {
                    List<AppointmentDetails> existingAppointments = user.getAppointmentDetails();

                    // Iterate through the passed appointmentDetails to update or throw an error if not found
                    for (AppointmentDetails newAppointment : appointmentDetails) {
                        boolean found = false;

                        for (AppointmentDetails existingAppointment : existingAppointments) {
                            if (newAppointment.getAppointmentId().equals(existingAppointment.getAppointmentId())) {
                                // Update existing appointment with new details
                                existingAppointment.setAppointmentType(newAppointment.getAppointmentType());
                                existingAppointment.setAppointmentFor(newAppointment.getAppointmentFor());
                                existingAppointment.setAppointmentForName(newAppointment.getAppointmentForName());
                                existingAppointment.setAppointmentForAge(newAppointment.getAppointmentForAge());
                                existingAppointment.setSymptom(newAppointment.getSymptom());
                                existingAppointment.setOtherSymptoms(newAppointment.getOtherSymptoms());
                                existingAppointment.setAppointmentDate(newAppointment.getAppointmentDate());
                                existingAppointment.setDoctorName(newAppointment.getDoctorName());
                                existingAppointment.setClinicId(newAppointment.getClinicId());
                                existingAppointment.setActive(newAppointment.isActive());
                                found = true;
                                break; // Stop further searching once found
                            }
                        }

                        // If not found, throw an error for the missing appointment ID
                        if (!found) {
                            return Mono.error(new AppointmnetNotFoundException("Appointment ID not found: " + newAppointment.getAppointmentId()));
                        }
                    }

                    // Save the user with updated appointment details
                    return this.userRepository.save(user)
                            .doOnSuccess(savedUser -> LOGGER.info(
                                    "Updated appointments for user with ID {}: {}", userId, savedUser))
                            .doOnError(error -> LOGGER.error(
                                    "Error occurred while updating appointments for user with ID {}: {}",
                                    userId, error.getMessage()))
                            .thenReturn(existingAppointments); // Return the updated appointment details
                });
    }


}