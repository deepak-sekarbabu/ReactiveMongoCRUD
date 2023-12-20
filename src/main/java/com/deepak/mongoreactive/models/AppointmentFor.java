package com.deepak.mongoreactive.models;

import lombok.Getter;

@Getter
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