package com.deepak.mongoreactive.models;

import java.util.List;

public class ErrorResponse {
    private String timestamp;
    private int status;
    private List<String> errors;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "timestamp='" + timestamp + '\'' +
                ", status=" + status +
                ", errors=" + errors +
                '}';
    }
}
