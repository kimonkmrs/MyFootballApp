package com.example.foorballapp.api;

import com.example.foorballapp.DeleteMatchResponse;
import com.example.foorballapp.Group;
import com.example.foorballapp.InsertMatchResponse;
import com.example.foorballapp.Match;
import com.example.foorballapp.MatchRequest;
import com.example.foorballapp.Player;
import com.example.foorballapp.PlayerMatchRequest;
import com.example.foorballapp.PlayerRemoveRequest;
import com.example.foorballapp.PlayerRequest;
import com.example.foorballapp.PlayerStats;
import com.example.foorballapp.Standing;
import com.example.foorballapp.Teams;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
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

    @POST("players/assignMatch")
    Call<Void> assignMatchToPlayer(@Body PlayerMatchRequest matchRequest);
    @PATCH("/matches/{id}/updateScoresFromPlayers")
    Call<Void> updateScoresFromPlayers(@Path("id") int matchId);


    @DELETE("players/{playerId}/removeMatch")
    Call<Void> removePlayerFromMatch(@Path("playerId") int playerId, @Query("matchId") int matchId);
    @FormUrlEncoded
    @POST("updatePlayerStat")
    Call<Void> updatePlayerStat(
            @Field("playerId") int playerId,
            @Field("statType") String statType,  // "goals", "yellowCards", or "redCards"
            @Field("value") int value
    );
    @FormUrlEncoded
    @POST("match/{matchId}/player/{playerId}/updatePosition")
    Call<Void> updatePlayerPosition(
            @Path("playerId") int playerId,
            @Path("matchId") int matchId,
            @Field("position") String position
    );


    @POST("/players/add")
    Call<Void> addPlayer(@Body PlayerRequest playerRequest);

    @GET("/standings/general")
    Call<List<Standing>> getGeneralStandings(); // General Table
    @GET("/api/playerstats")  // Define your API endpoint
    Call<List<PlayerStats>> getPlayerStats();

    @GET("/standings/group/{groupName}")
    Call<List<Standing>> getStandingsByGroup(@Path("groupName") String groupName); // Specific group

    @GET("/groups")
    Call<List<Group>> getGroups(); // Fetch team groups

    @GET("/api/playerstats/byTeams/{teamName}")
    Call<List<PlayerStats>> getPlayerStatsByTeams(@Path("teamName") String teamName);


    @GET("/teams")
    Call<List<Teams>> getTeamNames();  // API to get team names



}
