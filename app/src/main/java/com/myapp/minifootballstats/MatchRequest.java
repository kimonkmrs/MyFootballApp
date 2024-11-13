package com.myapp.minifootballstats;

public class MatchRequest {
    private int team1ID;
    private int team2ID;
    private int scoreTeam1;
    private int scoreTeam2;
    private String matchDate;

    public MatchRequest(int team1ID, int team2ID, int scoreTeam1, int scoreTeam2, String matchDate) {
        this.team1ID = team1ID;
        this.team2ID = team2ID;
        this.scoreTeam1 = scoreTeam1;
        this.scoreTeam2 = scoreTeam2;
        this.matchDate = matchDate;
    }

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
