package com.example.foorballapp;

import com.google.gson.annotations.SerializedName;

public class MatchRequest {

    @SerializedName("team1ID")
    private int team1ID;

    @SerializedName("team2ID")
    private int team2ID;

    @SerializedName("scoreTeam1")
    private int scoreTeam1;

    @SerializedName("scoreTeam2")
    private int scoreTeam2;

    @SerializedName("matchDate")
    private String matchDate;

    public MatchRequest(int team1ID, int team2ID, int scoreTeam1, int scoreTeam2, String matchDate) {
        this.team1ID = team1ID;
        this.team2ID = team2ID;
        this.scoreTeam1 = scoreTeam1;
        this.scoreTeam2 = scoreTeam2;
        this.matchDate = matchDate;
    }


    // Getters and Setters
    public int getTeam1ID() {
        return team1ID;
    }

    public void setTeam1ID(int team1ID) {
        this.team1ID = team1ID;
    }

    public int getTeam2ID() {
        return team2ID;
    }

    public void setTeam2ID(int team2ID) {
        this.team2ID = team2ID;
    }

    public int getScoreTeam1() {
        return scoreTeam1;
    }

    public void setScoreTeam1(int scoreTeam1) {
        this.scoreTeam1 = scoreTeam1;
    }

    public int getScoreTeam2() {
        return scoreTeam2;
    }

    public void setScoreTeam2(int scoreTeam2) {
        this.scoreTeam2 = scoreTeam2;
    }

    public String getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(String matchDate) {
        this.matchDate = matchDate;
    }
}
