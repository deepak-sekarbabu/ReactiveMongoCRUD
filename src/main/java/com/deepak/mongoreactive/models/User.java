package com.deepak.mongoreactive.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
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
@Schema(description = "User Information")
public class User {
    /**
     * The unique ID for the user
     */
    @Id
    @Hidden
    @Schema(description = "AutogenerateID")
    private String id;

    /**
     * The user's first name
     */
    @NotBlank(message = "First name is required")
    @Size(max = 120)
    @Schema(description = "First Name", example = "Deepak")
    private String firstName;
    @Size(max = 120)
    @Schema(description = "Last Name", example = "Sharma")
    private String lastName;

    /**
     * The user's 10-digit phone number
     */
    @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
    @NotBlank(message = "Phone number cannot be blank")
    @Schema(description = "Phone Number", example = "9876543210")
    private String phoneNumber;

    @Schema(description = "Age", example = "21")
    @Min(0)
    @Max(100)
    private int age;

    /**
     * The user's email address
     */
    @Email(message = "Email should be valid")
    @Schema(description = "Email", example = "test@test.com")
    private String email;
    @Schema(description = "AppointmentDetails in Array")
    private List<AppointmentDetails> appointmentDetails;


}