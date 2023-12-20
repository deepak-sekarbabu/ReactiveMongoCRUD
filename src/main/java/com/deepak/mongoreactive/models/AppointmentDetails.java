package com.deepak.mongoreactive.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@JsonFormat
@Builder
public class AppointmentDetails {

    private AppointmentType appointmentType;
    private AppointmentFor appointmentFor;
    private String appointmentForName;
    private String appointmentForAge;
    private Symptom symptom;
    private String otherSymptoms;
    private LocalDateTime appointmentDate;
    private String doctorName;
}