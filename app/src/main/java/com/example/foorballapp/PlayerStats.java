package com.example.foorballapp;

import com.google.gson.annotations.SerializedName;

public class PlayerStats {

    @SerializedName("PlayerName")
    private String playerName;

    @SerializedName("Position")
    private String position;

    @SerializedName("TeamName")
    private String teamName;

    @SerializedName("MP")  // Total Matches Played
    private int matchesPlayed;

    @SerializedName("GF")  // Goals For
    private int goalsFor;

    @SerializedName("GA")  // Goals Against
    private int goalsAgainst;

    @SerializedName("Wins")
    private int wins;

    @SerializedName("Losses")
    private int losses;

    @SerializedName("Draws")
    private int draws;

    // Constructor
    public PlayerStats(String playerName, String position, String teamName, int matchesPlayed, int goalsFor, int goalsAgainst, int wins, int losses, int draws) {
        this.playerName = playerName;
        this.position = position;
        this.teamName = teamName;
        this.matchesPlayed = matchesPlayed;
        this.goalsFor = goalsFor;
        this.goalsAgainst = goalsAgainst;
        this.wins = wins;
        this.losses = losses;
        this.draws = draws;
    }

    // Getters and Setters
    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
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

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getDraws() {
        return draws;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }
}
