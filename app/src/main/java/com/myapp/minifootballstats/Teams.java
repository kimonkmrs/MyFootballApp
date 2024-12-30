package com.myapp.minifootballstats;

import com.google.gson.annotations.SerializedName;

public class Teams {
    @SerializedName("TeamName")
    private String teamName;  // Assuming you have a name field

    @SerializedName("GroupName")
    private String groupName;

    @SerializedName("Wins")
    private int wins;

    @SerializedName("Draws")
    private int draws;

    @SerializedName("Losses")
    private int losses;

    @SerializedName("GF")
    private int goalsFor;

    @SerializedName("GA")
    private int goalsAgainst;

    @SerializedName("GD")
    private int goalDifference;

    @SerializedName("SquadSize")
    private int squadSize; // Replace rosterCount with squadSize


    // Constructor
    public Teams(String teamName, String groupName, int wins, int draws, int losses, int goalsFor, int goalsAgainst, int goalDifference, int squadSize) {
        this.teamName = teamName;
        this.groupName = groupName;
        this.wins = wins;
        this.draws = draws;
        this.losses = losses;
        this.goalsFor = goalsFor;
        this.goalsAgainst = goalsAgainst;
        this.goalDifference = goalDifference;
        this.squadSize = squadSize;
    }

    // Getters and Setters
    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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

    public int getRosterCount() {
        return squadSize;
    }

    public void setRosterCount(int squadSize) {
        this.squadSize = squadSize;
    }
}

