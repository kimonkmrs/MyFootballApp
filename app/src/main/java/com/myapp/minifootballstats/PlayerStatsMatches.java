package com.myapp.minifootballstats;

import com.google.gson.annotations.SerializedName;

public class PlayerStatsMatches {



    @SerializedName("Position")
    private String position;



    // Constructor
    public PlayerStatsMatches(String position) {

        this.position = position;}


        // Getters and Setters


    public String getPositionMatches() {
        return position; // ✅ Now it correctly returns a String
    }


    public void setPositionMatches (String position){
            this.position = position;
        }
    }



