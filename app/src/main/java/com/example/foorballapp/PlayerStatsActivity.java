package com.example.foorballapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foorballapp.api.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PlayerStatsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PlayerStatsAdapter playerStatsAdapter;
    private List<PlayerStats> playerStatsList = new ArrayList<>();
    private TextView noDataTextView;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_stats);

        recyclerView = findViewById(R.id.recyclerViewPlayerStats);
        noDataTextView = findViewById(R.id.noDataTextView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        playerStatsAdapter = new PlayerStatsAdapter(playerStatsList);
        recyclerView.setAdapter(playerStatsAdapter);

        Retrofit retrofit = NetworkClient.getRetrofitInstance();
        apiService = retrofit.create(ApiService.class);

        fetchPlayerStats();
    }

    private void fetchPlayerStats() {
        Call<List<PlayerStats>> call = apiService.getPlayerStats();  // Define this in ApiService
        call.enqueue(new Callback<List<PlayerStats>>() {
            @Override
            public void onResponse(Call<List<PlayerStats>> call, Response<List<PlayerStats>> response) {
                if (response.isSuccessful()) {
                    List<PlayerStats> stats = response.body();
                    if (stats != null && !stats.isEmpty()) {
                        playerStatsList.clear();
                        playerStatsList.addAll(stats);
                        playerStatsAdapter.notifyDataSetChanged();
                        noDataTextView.setVisibility(View.GONE);
                    } else {
                        noDataTextView.setVisibility(View.VISIBLE);
                    }
                } else {
                    Log.e("PlayerStatsActivity", "Request failed: " + response.code());
                    Toast.makeText(PlayerStatsActivity.this, "Failed to load player stats", Toast.LENGTH_SHORT).show();
                    noDataTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<PlayerStats>> call, Throwable t) {
                Log.e("PlayerStatsActivity", "Network error: " + t.getMessage());
                Toast.makeText(PlayerStatsActivity.this, "Network error, please try again later.", Toast.LENGTH_SHORT).show();
                noDataTextView.setVisibility(View.VISIBLE);
            }
        });
    }
}
