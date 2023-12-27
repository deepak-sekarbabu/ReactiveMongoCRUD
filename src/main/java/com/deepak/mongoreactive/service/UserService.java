package com.deepak.mongoreactive.service;

import com.deepak.mongoreactive.models.AppointmentDetails;
import com.deepak.mongoreactive.models.User;
import org.springframework.format.annotation.DateTimeFormat;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

public interface UserService {
    Mono<User> getUserById(String id);

    Flux<User> getUsers(int limit, int offset);

    Flux<User> getAppointmentsByDate(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date);

    Flux<User> getAppointmentsByDateAndIsActive(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, boolean active);

    Mono<User> saveUser(Mono<User> userDtoMono);

    Mono<User> updateUserInformation(String id, Mono<User> userMono);

    Mono<Void> deleteUser(String id);

    Mono<Long> deleteByName(String name);

    Mono<User> findByPhoneNumber(String phoneNumber);

    Mono<String> findUserIdByPhoneNumber(String phoneNumber);

    Mono<User> getUserWithActiveAppointmentsByUserId(String userId);

    Mono<User> getUserWithActiveAppointmentsByPhoneNumber(String phoneNumber);

    Mono<User> cancelAppointmentByPhoneNumber(String phoneNumber, List<String> appointment);

    Mono<User> cancelAppointmentByUserId(String phoneNumber, List<String> appointment);

    Mono<User> createAppointmentByUserId(String userId, List<AppointmentDetails> appointmentDetails);

    Mono<List<AppointmentDetails>> createAppointmentsByUserId(String userId, List<AppointmentDetails> appointmentDetails);

    Mono<List<AppointmentDetails>> updateAppointmentsByUserId(String userId, List<AppointmentDetails> appointmentDetails);

}