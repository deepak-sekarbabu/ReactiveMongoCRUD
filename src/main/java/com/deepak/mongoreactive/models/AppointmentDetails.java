package com.deepak.mongoreactive.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString(exclude = {"appointmentType", "appointmentFor", "appointmentForName", "appointmentForAge", "otherSymptoms", "symptom"})
@EqualsAndHashCode
@JsonFormat
@Builder
public class AppointmentDetails {

    @Hidden
    private String appointmentId;
    @Schema(description = "Appointment Type", example = "GENERAL_CHECKUP")
    private AppointmentType appointmentType;
    @Schema(description = "Appointment For", example = "SELF")
    private AppointmentFor appointmentFor;
    @Schema(description = "Appointment For Name", example = "Deepak Sharma")
    @Max(150)
    private String appointmentForName;
    @Schema(description = "Age of person seeking appointment", example = "25")
    private String appointmentForAge;
    @Schema(description = "Symptoms", example = "HEADACHE")
    private Symptom symptom;
    @Schema(description = "Other Symptoms", example = "Vomiting etc")
    @Max(250)
    private String otherSymptoms;
    @Schema(description = "Appointment Date", example = "2023-12-24T16:25:48.748Z")
    private LocalDateTime appointmentDate;
    @Schema(description = "Doctors Name", example = "Dr Dinesh Child Specialist")
    private String doctorName;
    @Schema(description = "Clinic Name", example = "Dr Dinesh Child Specialist Clinic")
    private String clinicId;
    @Hidden
    private boolean active;

    public void generateCustomAppointmentId(String userPhoneNumber, String appointmentForName) {
        // Generate a custom appointment ID using the user's phone number and current time
        this.appointmentId = userPhoneNumber + "_" + appointmentForName + "_" + LocalDateTime.now();
    }
}