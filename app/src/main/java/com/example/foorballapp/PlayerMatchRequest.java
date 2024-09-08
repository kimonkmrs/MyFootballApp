package com.example.foorballapp;


public class PlayerMatchRequest {
    private int matchId;

    public PlayerMatchRequest(int matchId) {
        this.matchId = matchId;
    }

    public int getMatchId() {
        return matchId;
    }

    public void setMatchId(int matchId) {
        this.matchId = matchId;
    }
}

