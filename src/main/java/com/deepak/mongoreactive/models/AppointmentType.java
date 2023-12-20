package com.deepak.mongoreactive.models;

import lombok.Getter;

@Getter
public enum AppointmentType {
    GENERAL_CHECKUP("Regular Checkup"),
    DENTAL("Dental Checkup"),
    VACCINATION("Vaccination"),
    CONSULTATION("Consultation"),
    FOLLOW_UP("Follow-up");

    private final String displayName;

    AppointmentType(String displayName) {
        this.displayName = displayName;
    }

}