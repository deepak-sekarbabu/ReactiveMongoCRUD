package com.deepak.mongoreactive.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "user")
@Getter
@Setter
@ToString(exclude = {"firstName", "phoneNumber", "email"})
@EqualsAndHashCode
@JsonFormat
@Builder
public class User {
    /**
     * The unique ID for the user
     */
    @Id
    private String id;

    /**
     * The user's first name
     */
    @NotBlank(message = "First name is required")
    private String firstName;

    private String lastName;

    /**
     * The user's 10-digit phone number
     */
    @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
    @NotBlank(message = "Phone number cannot be blank")
    private String phoneNumber;

    private int age;

    /**
     * The user's email address
     */
    @Email(message = "Email should be valid")
    private String email;

    private List<AppointmentDetails> appointmentDetails;


}