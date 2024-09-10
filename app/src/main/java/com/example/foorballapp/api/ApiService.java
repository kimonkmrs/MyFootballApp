package com.example.foorballapp.api;

import com.example.foorballapp.DeleteMatchResponse;
import com.example.foorballapp.Group;
import com.example.foorballapp.InsertMatchResponse;
import com.example.foorballapp.Match;
import com.example.foorballapp.MatchRequest;
import com.example.foorballapp.Player;
import com.example.foorballapp.PlayerMatchRequest;
import com.example.foorballapp.PlayerRemoveRequest;
import com.example.foorballapp.Standing;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("matches/today") // Adjust endpoint as needed
    Call<List<Match>> getTodayMatches();
    @POST("matches/insert")
    Call<InsertMatchResponse> insertMatch(@Body MatchRequest matchRequest);
    @DELETE("matches/delete/{id}")
    Call<DeleteMatchResponse> deleteMatch(@Path("id") int matchID);

    @GET("playersByTeam") // Updated endpoint to fetch players by team ID
    Call<List<Player>> getPlayersByTeamId(@Query("teamId") int teamId);

    @POST("/players/{playerId}/assignMatch")
    Call<Void> assignMatchToPlayer(@Path("playerId") int playerId, @Body PlayerMatchRequest matchRequest);



    @POST("players/{playerId}/removeMatch")
    Call<Void> removeMatchFromPlayer(@Path("playerId") int playerId, @Body PlayerRemoveRequest removeRequest);



    @GET("/standings/general")
    Call<List<Standing>> getGeneralStandings(); // General Table

    @GET("/standings/group/{groupName}")
    Call<List<Standing>> getStandingsByGroup(@Path("groupName") String groupName); // Specific group

    @GET("/groups")
    Call<List<Group>> getGroups(); // Fetch team groups



}
