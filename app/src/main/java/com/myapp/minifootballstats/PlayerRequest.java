package com.myapp.minifootballstats;

public class PlayerRequest {

    private int teamID;


    private String playerName;
    private String playerPosition;

    // Constructor without PlayerID
    public PlayerRequest(int teamID, String playerName,String playerPosition) {
        this.teamID = teamID;
        this.playerName = playerName;
        this.playerPosition=playerPosition;
    }

    // Getters
    public int getTeamID() {
        return teamID;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getPlayerPosition() {
        return playerPosition;
    }

    // Setters
    public void setTeamID(int teamID) {
        this.teamID = teamID;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setPlayerPosition(String playerPosition) {
        this.playerName = playerPosition;
    }
}
