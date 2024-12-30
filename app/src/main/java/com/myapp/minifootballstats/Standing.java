package com.myapp.minifootballstats;

import com.google.gson.annotations.SerializedName;

public class Standing {

    @SerializedName("Position")
    private int position;

    @SerializedName("GroupID")
    private int groupID;  // Optional, if you need it

    @SerializedName("GroupName")
    private String groupName;  // Optional, if you need it

    @SerializedName("TeamName")
    private String teamName;

    @SerializedName("MP")
    private int matchesPlayed; // Adjusted field name to match SQL query

    @SerializedName("Wins")
    private int wins;

    @SerializedName("Draws")
    private int draws;

    @SerializedName("Losses")
    private int losses;

    @SerializedName("GF")
    private int goalsFor; // Adjusted field name to match SQL query

    @SerializedName("GA")
    private int goalsAgainst; // Adjusted field name to match SQL query

    @SerializedName("GD")
    private int goalDifference; // Adjusted field name to match SQL query

    @SerializedName("Points")
    private int points;
    @SerializedName("rosterCount")
    private int rosterCount;

    // Constructor
    public Standing(int position, String teamName, int matchesPlayed, int wins, int draws, int losses, int goalsFor,
                    int goalsAgainst, int goalDifference, int points, int groupID, String groupName,int rosterCount) {
        this.position = position;
        this.teamName = teamName;
        this.matchesPlayed = matchesPlayed;
        this.wins = wins;
        this.draws = draws;
        this.losses = losses;
        this.goalsFor = goalsFor;
        this.goalsAgainst = goalsAgainst;
        this.goalDifference = goalDifference;
        this.points = points;
        this.groupID = groupID; // Initialize groupID if needed
        this.groupName = groupName; // Initialize groupName if needed
        this.rosterCount=rosterCount;
    }

    // Getters and Setters
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public int getMatchesPlayed() {
        return matchesPlayed;
    }

    public void setMatchesPlayed(int matchesPlayed) {
        this.matchesPlayed = matchesPlayed;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getDraws() {
        return draws;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getGoalsFor() {
        return goalsFor;
    }

    public void setGoalsFor(int goalsFor) {
        this.goalsFor = goalsFor;
    }

    public int getGoalsAgainst() {
        return goalsAgainst;
    }

    public void setGoalsAgainst(int goalsAgainst) {
        this.goalsAgainst = goalsAgainst;
    }

    public int getGoalDifference() {
        return goalDifference;
    }

    public void setGoalDifference(int goalDifference) {
        this.goalDifference = goalDifference;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getRosterCount() {
        return rosterCount;
    }

    public void setRosterCount(int rosterCount) {
        this.rosterCount = rosterCount;
    }
}
