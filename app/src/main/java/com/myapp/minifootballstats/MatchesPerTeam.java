package com.myapp.minifootballstats;

import com.google.gson.annotations.SerializedName;

public class MatchesPerTeam {
    @SerializedName("Game") // Ensure these match the API response keys
    private String game;

    @SerializedName("Result")
    private String result;

    @SerializedName("MatchDate")
    private String matchDate;

    public MatchesPerTeam(String game, String result, String matchDate) {
        this.game = game;
        this.result = result;
        this.matchDate = matchDate;
    }

    public String getGame() { return game; }
    public String getResult() { return result; }
    public String getMatchDate() { return matchDate; }
}

