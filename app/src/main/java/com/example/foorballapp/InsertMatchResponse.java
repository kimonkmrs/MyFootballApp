package com.example.foorballapp;

import com.google.gson.annotations.SerializedName;

public class InsertMatchResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("matchId") // Ensure this matches the JSON field name
    private int matchId; // or Integer if it can be null

    // No-arg constructor
    public InsertMatchResponse() {
    }

    // Parameterized constructor
    public InsertMatchResponse(String status, String message, int matchId) {
        this.status = status;
        this.message = message;
        this.matchId = matchId;
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

    // Getter for matchId
    public int getMatchId() {
        return matchId;
    }

    // Setter for matchId
    public void setMatchId(int matchId) {
        this.matchId = matchId;
    }

    @Override
    public String toString() {
        return "InsertMatchResponse{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", matchId=" + matchId +
                '}';
    }
}
