package com.example.foorballapp;

import com.google.gson.annotations.SerializedName;

public class Match {

    @SerializedName("MatchID")
    private int matchID;

    @SerializedName("Team1Name")
    private String team1Name;

    @SerializedName("Team2Name")
    private String team2Name;

    @SerializedName("ScoreTeam1")
    private int scoreTeam1;

    @SerializedName("ScoreTeam2")
    private int scoreTeam2;

    @SerializedName("MatchDate")
    private String matchDate;

    // Constructor
    public Match(int matchID, String team1Name, String team2Name, int scoreTeam1, int scoreTeam2, String matchDate) {
        this.matchID = matchID;
        this.team1Name = team1Name;
        this.team2Name = team2Name;
        this.scoreTeam1 = scoreTeam1;
        this.scoreTeam2 = scoreTeam2;
        this.matchDate = matchDate;
    }

    // Getters and Setters
    public int getMatchID() { return matchID; }
    public void setMatchID(int matchID) { this.matchID = matchID; }
    public String getTeam1Name() { return team1Name; }
    public void setTeam1Name(String team1Name) { this.team1Name = team1Name; }
    public String getTeam2Name() { return team2Name; }
    public void setTeam2Name(String team2Name) { this.team2Name = team2Name; }
    public int getScoreTeam1() { return scoreTeam1; }
    public void setScoreTeam1(int scoreTeam1) { this.scoreTeam1 = scoreTeam1; }
    public int getScoreTeam2() { return scoreTeam2; }
    public void setScoreTeam2(int scoreTeam2) { this.scoreTeam2 = scoreTeam2; }
    public String getMatchDate() { return matchDate; }
    public void setMatchDate(String matchDate) { this.matchDate = matchDate; }
}
