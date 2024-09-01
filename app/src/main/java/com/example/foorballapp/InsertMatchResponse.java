package com.example.foorballapp;

import com.google.gson.annotations.SerializedName;

public class InsertMatchResponse {
    @SerializedName("status")
    private String status;
    @SerializedName("message")
    private String message;
    // Constructor
    public InsertMatchResponse() {
    }

    public InsertMatchResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    // Getter for status
    public String getStatus() {
        return status;
    }

    // Setter for status
    public void setStatus(String status) {
        this.status = status;
    }

    // Getter for message
    public String getMessage() {
        return message;
    }

    // Setter for message
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "InsertMatchResponse{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
