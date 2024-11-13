package com.myapp.minifootballstats;

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
    @SerializedName("Team1ID")
    private int team1ID;  // Add this field

    @SerializedName("Team2ID")
    private int team2ID;  // Add this field

    // Constructor
    public Match(int matchID, String team1Name, String team2Name, int scoreTeam1, int scoreTeam2, String matchDate) {
        this.matchID = matchID;
        this.team1Name = team1Name;
        this.team2Name = team2Name;
        this.scoreTeam1 = scoreTeam1;
        this.scoreTeam2 = scoreTeam2;
        this.matchDate = matchDate;
        this.team1ID = team1ID;  // Initialize team1ID
        this.team2ID = team2ID;  // Initialize team2ID
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
    public int getTeam1ID() { return team1ID; }  // Getter for team1ID
    public void setTeam1ID(int team1ID) { this.team1ID = team1ID; }  // Setter for team1ID

    public int getTeam2ID() { return team2ID; }  // Getter for team2ID
    public void setTeam2ID(int team2ID) { this.team2ID = team2ID; }  // Setter for team2ID

}
