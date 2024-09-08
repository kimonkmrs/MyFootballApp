package com.example.foorballapp;

public class PlayerRemoveRequest {
    private int matchId;

    public PlayerRemoveRequest(int matchId) {
        this.matchId = matchId;
    }

    public int getMatchId() {
        return matchId;
    }

    public void setMatchId(int matchId) {
        this.matchId = matchId;
    }
}
