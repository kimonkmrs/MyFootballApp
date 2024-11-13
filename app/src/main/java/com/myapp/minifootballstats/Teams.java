package com.myapp.minifootballstats;

import com.google.gson.annotations.SerializedName;

public class Teams {
    @SerializedName("TeamName")
    private String teamName;  // Assuming you have a name field




    public Teams( String teamName) {

        this.teamName = teamName;
    }

    public String getTeamName() { return teamName; }
    public void setTeamName(String groupName) { this.teamName = groupName; }
}

