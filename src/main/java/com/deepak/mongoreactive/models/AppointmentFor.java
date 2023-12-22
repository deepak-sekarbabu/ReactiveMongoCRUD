package com.deepak.mongoreactive.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(enumAsRef = true)
public enum AppointmentFor {
    SELF("Self"),
    SPOUSE("Spouse"),
    KIDS("Kids"),
    PARENTS("Parents"),
    OTHERS("Others");

    private final String displayName;

    AppointmentFor(String displayName) {
        this.displayName = displayName;
    }

}