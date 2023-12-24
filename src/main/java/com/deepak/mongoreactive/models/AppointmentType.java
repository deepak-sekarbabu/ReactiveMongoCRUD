package com.deepak.mongoreactive.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(enumAsRef = true)
public enum AppointmentType {
    GENERAL_CHECKUP("General Checkup"),
    DENTAL("Dental Checkup"),
    VACCINATION("Vaccination"),
    CONSULTATION("Consultation"),
    FOLLOW_UP("Follow-up");

    private final String displayName;

    AppointmentType(String displayName) {
        this.displayName = displayName;
    }

}