package com.myapp.minifootballstats.api;

import com.myapp.minifootballstats.DeleteMatchResponse;
import com.myapp.minifootballstats.Group;
import com.myapp.minifootballstats.InsertMatchResponse;
import com.myapp.minifootballstats.Match;
import com.myapp.minifootballstats.MatchRequest;
import com.myapp.minifootballstats.Player;
import com.myapp.minifootballstats.PlayerMatchRequest;
import com.myapp.minifootballstats.PlayerRequest;
import com.myapp.minifootballstats.PlayerStats;
import com.myapp.minifootballstats.Standing;
import com.myapp.minifootballstats.Teams;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
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
    @POST("match/{matchId}/player/{playerId}/updateStat")
    Call<Void> updatePlayerStat(

            @Path("playerId") int playerId,     // Pass playerId as a path parameter
            @Field("statType") String statType, // Pass statType as a form field
            @Path("matchId") int matchId,       // Pass matchId as a path parameter
            @Field("value") int value           // Pass stat value as a form field
    );
    @FormUrlEncoded
    @POST("match/{matchId}/player/{playerId}/updatePosition")
    Call<Void> updatePlayerPosition(
            @Path("playerId") int playerId,     // Pass playerId as a path parameter
            @Path("matchId") int matchId,       // Pass matchId as a path parameter

            @Field("position") String position  // Keep position as a field
    );



    @POST("/players/add")
    Call<Void> addPlayer(@Body PlayerRequest playerRequest);

    @GET("/standings/general")
    Call<List<Standing>> getGeneralStandings(); // General Table

    @GET("/team-details")
    Call<Teams> getTeamDetails(@Query("teamName") String teamName);



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

    @GET("/positions")
    Call<List<PlayerStats>> getPosition();  // API to get team names





}
