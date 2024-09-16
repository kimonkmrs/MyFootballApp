package com.example.foorballapp;

import com.google.gson.annotations.SerializedName;

public class PlayerRequest {

    private int teamID;


    private String playerName;

    // Constructor without PlayerID
    public PlayerRequest(int teamID, String playerName) {
        this.teamID = teamID;
        this.playerName = playerName;
    }

    // Getters
    public int getTeamID() {
        return teamID;
    }

    public String getPlayerName() {
        return playerName;
    }

    // Setters
    public void setTeamID(int teamID) {
        this.teamID = teamID;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
