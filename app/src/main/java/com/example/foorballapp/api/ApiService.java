package com.example.foorballapp.api;

import com.example.foorballapp.DeleteMatchResponse;
import com.example.foorballapp.InsertMatchResponse;
import com.example.foorballapp.Match;
import com.example.foorballapp.MatchRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @GET("matches/today") // Adjust endpoint as needed
    Call<List<Match>> getTodayMatches();
    @POST("matches/insert")
    Call<InsertMatchResponse> insertMatch(@Body MatchRequest matchRequest);
    @DELETE("matches/delete/{id}")
    Call<DeleteMatchResponse> deleteMatch(@Path("id") int matchID);
}
