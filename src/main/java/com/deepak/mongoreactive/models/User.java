package com.deepak.mongoreactive.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Document
@Getter
@Setter
@ToString
@EqualsAndHashCode
@JsonFormat
@Builder
public class User {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private int age;
    private String email;
    private AppointmentType appointmentType;
    private AppointmentFor appointmentFor;
    private String appointmentForName;
    private String appointmentForAge;
    private Symptom symptom;
    private String otherSymptoms;
    private LocalDateTime time;
    private String doctorName;

}