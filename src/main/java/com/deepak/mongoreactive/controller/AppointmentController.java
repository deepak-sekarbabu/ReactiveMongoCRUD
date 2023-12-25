package com.deepak.mongoreactive.controller;

import com.deepak.mongoreactive.models.User;
import com.deepak.mongoreactive.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/appointments")
@Tag(name = "Appointments", description = "Handles CRUD operations for Appointments")
public class AppointmentController {

    private final UserService userService;

    public AppointmentController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/active/user/{userId}")
    @Operation(summary = "GetUserWithActiveAppointmentsByUserId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User with appointment information retrieved"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "User does not exist")
    })
    public Mono<User> getUserWithActiveAppointmentsByUserId(@PathVariable String userId) {
        return this.userService.getUserWithActiveAppointmentsByUserId(userId);
    }

    @GetMapping("/active/phone/{phoneNumber}")
    @Operation(summary = "GetUserWithActiveAppointmentsByPhoneNumber")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User with appointment information retrieved"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "User does not exist")
    })
    public Mono<User> getUserWithActiveAppointmentsByPhoneNumber(@PathVariable String phoneNumber) {
        return this.userService.getUserWithActiveAppointmentsByPhoneNumber(phoneNumber);
    }

    @GetMapping("/byDate/{date}")
    @Operation(summary = "GetAppointmentsByDate")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointments retrieved"),
            @ApiResponse(responseCode = "400", description = "Invalid date format"),
            @ApiResponse(responseCode = "404", description = "No appointments found for given date")
    })
    public Flux<User> getAppointmentsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return this.userService.getAppointmentsByDate(date);
    }

    @GetMapping("/byDate/{date}/{active}")
    @Operation(summary = "GetAppointmentsByDate and is active/inactive")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointments retrieved"),
            @ApiResponse(responseCode = "400", description = "Invalid date format"),
            @ApiResponse(responseCode = "404", description = "No appointments found for given date")
    })
    public Flux<User> getAppointmentsByDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                            @PathVariable boolean active) {
        return this.userService.getAppointmentsByDateAndIsActive(date, active);
    }

    @PostMapping("/cancel/phone/{phoneNumber}")
    @Operation(summary = "CancelAppointmentByPhoneNumber")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointments cancelled"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "User does not exist")
    })
    public Mono<User> cancelAppointmentByPhoneNumber(@PathVariable String phoneNumber,
                                                     @RequestBody List<String> appointmentIds) {
        return this.userService.cancelAppointmentByPhoneNumber(phoneNumber, appointmentIds);
    }

    @PostMapping("/cancel/user/{userId}")
    @Operation(summary = "CancelAppointmentByUserId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointments cancelled"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "User does not exist")
    })
    public Mono<User> cancelAppointmentByUserId(@PathVariable String userId, @RequestBody List<String> appointmentIds) {
        return this.userService.cancelAppointmentByUserId(userId, appointmentIds);
    }

}
