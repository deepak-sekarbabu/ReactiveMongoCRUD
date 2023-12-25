package com.deepak.mongoreactive.service;

import com.deepak.mongoreactive.exception.CannotUpdatePhoneNumberException;
import com.deepak.mongoreactive.exception.UserNotFoundException;
import com.deepak.mongoreactive.models.AppointmentDetails;
import com.deepak.mongoreactive.models.User;
import com.deepak.mongoreactive.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class UserServiceUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceUtils.class);

    private final UserRepository userRepository;

    public UserServiceUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    Mono<User> handleExistingUser(String id, User userDTO, User existingUser) {
        if (!existingUser.getId().equals(id)) {
            return Mono.error(new CannotUpdatePhoneNumberException("Cannot update phone number used by another User"));
        } else {
            return updateUserAndSave(id, userDTO);
        }
    }

    Mono<User> updateUserAndSave(String id, User userDTO) {
        return this.userRepository.findById(id)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User with id " + id + " not found")))
                .flatMap(existingUser -> updateUserDetails(existingUser, userDTO))
                .flatMap(user -> updateAppointmentDetails(user, userDTO))
                .flatMap(this.userRepository::save)
                .doOnSuccess(user -> LOGGER.info("User with ID {} updated successfully", user.getId()))
                .doOnError(error -> LOGGER.error("Error occurred while updating user with ID {}: {}", id, error.getMessage()))
                .doOnCancel(() -> LOGGER.warn("User updating cancelled for ID: {}", id))
                .doFinally(signalType -> LOGGER.debug("User update completed with signal: {}", signalType));
    }

    private Mono<User> updateUserDetails(User existingUser, User userDTO) {
        existingUser
                .setFirstName(userDTO.getFirstName() != null ? userDTO.getFirstName() : existingUser.getFirstName());
        existingUser.setLastName(userDTO.getLastName() != null ? userDTO.getLastName() : existingUser.getLastName());
        existingUser.setPhoneNumber(
                userDTO.getPhoneNumber() != null ? userDTO.getPhoneNumber() : existingUser.getPhoneNumber());
        existingUser.setDateOfBirth(userDTO.getDateOfBirth() != null ? userDTO.getDateOfBirth() : existingUser.getDateOfBirth());
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
                    newDetail.generateCustomAppointmentId(phoneNumber, newDetail.getAppointmentForName());
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
        existing.setActive(newDetail.isActive());
    }
}
