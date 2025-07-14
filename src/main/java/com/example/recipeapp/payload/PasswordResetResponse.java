package com.example.recipeapp.payload;

public class PasswordResetResponse {
    public enum Status {
        SUCCESS,
        USER_NOT_FOUND,
        USER_INACTIVE
    }

    private Status status;
    private String message;

    public PasswordResetResponse() {}

    public PasswordResetResponse(Status status, String message) {
        this.status = status;
        this.message = message;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

