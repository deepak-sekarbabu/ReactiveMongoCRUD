package com.deepak.mongoreactive.models;

import lombok.Getter;

@Getter
public enum Symptom {
    FEVER("Fever"),
    HEADACHE("Headache"),
    COUGH("Cough"),
    NAUSEA("Nausea"),
    FATIGUE("Fatigue"),
    SORE_THROAT("Sore Throat"),
    SHORTNESS_OF_BREATH("Shortness of Breath"),
    BODY_ACHES("Body Aches"),
    LOSS_OF_TASTE_OR_SMELL("Loss of Taste or Smell"),
    RASH("Rash"),
    OTHER("Other");

    private final String displayName;

    Symptom(String displayName) {
        this.displayName = displayName;
    }

}