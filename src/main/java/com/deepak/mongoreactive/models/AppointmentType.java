package com.deepak.mongoreactive.models;

public enum AppointmentType {
    CHECKUP("Regular Checkup"),
    DENTAL("Dental Checkup"),
    VACCINATION("Vaccination"),
    CONSULTATION("Consultation"),
    FOLLOW_UP("Follow-up");

    private final String displayName;

    AppointmentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}