package com.deepak.mongoreactive.controller;

import com.deepak.mongoreactive.models.User;
import com.deepak.mongoreactive.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/appointment")
public class AppointmentController {
    private final UserService userService;

    public AppointmentController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/activebyid/{userId}")
    public Mono<User> getUserWithActiveAppointmentsUsingUserId(@PathVariable String userId) {
        return this.userService.getUserWithActiveAppointmentsUsingUserId(userId);
    }

    @GetMapping("/activebyphoneNumber/{phoneNumber}")
    public Mono<User> getUserWithActiveAppointmentsUsingPhoneNumber(@PathVariable String phoneNumber) {
        return this.userService.getUserWithActiveAppointmentsUsingPhoneNumber(phoneNumber);
    }
}
