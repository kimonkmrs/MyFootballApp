package com.myapp.minifootballstats;

import com.google.gson.annotations.SerializedName;

public class PlayerRemoveRequest {
    @SerializedName("MatchId")
    private int matchId;

    public PlayerRemoveRequest(int matchId) {
        this.matchId = matchId;
    }

    public int getMatchId() {
        return matchId;
    }

    public void setMatchId(int matchId) {
        this.matchId = matchId;
    }
}

