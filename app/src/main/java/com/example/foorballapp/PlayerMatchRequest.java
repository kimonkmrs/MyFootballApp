package com.example.foorballapp;
public class PlayerMatchRequest {
    private int playerId;
    private int matchId;

    public PlayerMatchRequest(int playerId, int matchId) {
        this.playerId = playerId;
        this.matchId = matchId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getMatchId() {
        return matchId;
    }
}
