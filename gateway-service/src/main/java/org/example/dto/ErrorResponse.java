package org.example.dto;

public class ErrorResponse {
    private String message;
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public ErrorResponse(String message) {
        this.message = message;
    }
    public ErrorResponse() {
    }
}
