package com.example.foorballapp;

import com.google.gson.annotations.SerializedName;

public class Player {

    @SerializedName("PlayerID")
    private int playerID;

    @SerializedName("TeamID")
    private int teamID;

    @SerializedName("PlayerName")
    private String playerName;

    // Getters
    public int getPlayerID() {
        return playerID;
    }

    public int getTeamID() {
        return teamID;
    }

    public String getPlayerName() {
        return playerName;
    }

    // Optionally, you can add setters if needed
    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public void setTeamID(int teamID) {
        this.teamID = teamID;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
