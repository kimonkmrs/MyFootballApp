package com.example.foorballapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
    private Spinner teamSpinner;
    private List<String> teamNames = new ArrayList<>();  // Holds team names for the spinner
    private String selectedTeam = "All Teams";  // Default selection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_stats);

        recyclerView = findViewById(R.id.recyclerViewPlayerStats);
        noDataTextView = findViewById(R.id.noDataTextView);
        teamSpinner = findViewById(R.id.team_spinner);  // Find Spinner view

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        playerStatsAdapter = new PlayerStatsAdapter(playerStatsList);
        recyclerView.setAdapter(playerStatsAdapter);

        Retrofit retrofit = NetworkClient.getRetrofitInstance();
        apiService = retrofit.create(ApiService.class);

        fetchTeamNames();  // Load team names and populate the spinner


        fetchPlayerStats();  // Fetch player stats initially (for "All Teams")
        setupSpinnerListener();  // Set listener for team selection
    }

    // Fetch team names to populate the spinner
    private void fetchTeamNames() {
        Call<List<Teams>> call = apiService.getTeamNames();  // Adjust this to match your API
        call.enqueue(new Callback<List<Teams>>() {
            @Override
            public void onResponse(Call<List<Teams>> call, Response<List<Teams>> response) {
                if (response.isSuccessful()) {
                    List<Teams> teams = response.body();
                    if (teams != null && !teams.isEmpty()) {
                        teamNames.clear();
                        // Add only actual team names from the API
                        for (Teams team : teams) {
                            Log.d("PlayerStatsActivity", "Adding team: " + team.getTeamName());
                            teamNames.add(team.getTeamName());
                        }
                        populateSpinner();
                    }
                } else {
                    Log.e("PlayerStatsActivity", "Failed to load teams: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Teams>> call, Throwable t) {
                Log.e("PlayerStatsActivity", "Failed to fetch team names: " + t.getMessage());
            }
        });
    }

    // Populate the spinner with team names
    private void populateSpinner() {
        List<String> spinnerOptions = new ArrayList<>();
        spinnerOptions.add("All Teams");  // Add "All Teams" as the default option
        spinnerOptions.addAll(teamNames);  // Add the actual team names

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teamSpinner.setAdapter(adapter);

        // Set "All Teams" as the default selection
        teamSpinner.setSelection(0);
    }

    // Setup listener for spinner item selection
    private void setupSpinnerListener() {
        teamSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Set the selected team based on the spinner selection
                if (position == 0) {
                    selectedTeam = "All Teams";  // Default option
                } else if (position > 0 && position <= teamNames.size()) {
                    selectedTeam = teamNames.get(position - 1);  // Adjust index due to "All Teams"
                } else {
                    Log.e("PlayerStatsActivity", "Invalid position selected: " + position);
                    Toast.makeText(PlayerStatsActivity.this, "Invalid selection", Toast.LENGTH_SHORT).show();
                }
                fetchPlayerStats();  // Fetch player stats based on the selected team
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Default action if needed, "All Teams" will be selected by default
            }
        });
    }

    // Fetch player stats based on the selected team
    private void fetchPlayerStats() {
        Call<List<PlayerStats>> call;
        if (selectedTeam.equals("All Teams")) {
            call = apiService.getPlayerStats();  // Get stats for all teams
        } else {
            call = apiService.getPlayerStatsByTeams(selectedTeam);  // Filter stats by selected team
        }

        call.enqueue(new Callback<List<PlayerStats>>() {

            @Override
            public void onResponse(Call<List<PlayerStats>> call, Response<List<PlayerStats>> response) {
                if (response.isSuccessful()) {
                    List<PlayerStats> stats = response.body();
                    if (stats != null && !stats.isEmpty()) {
                        // If player stats are found, display them in the RecyclerView
                        playerStatsList.clear();
                        playerStatsList.addAll(stats);
                        playerStatsAdapter.notifyDataSetChanged();
                        noDataTextView.setVisibility(View.GONE);
                        //noDataTextView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        // If no player stats are found, display the "No Data" message
                        noDataTextView.setText("No players found for " + selectedTeam);
                        noDataTextView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);  // Hide RecyclerView if no data
                    }
                } else {
                    Log.e("PlayerStatsActivity", "Failed to load player stats: " + response.code());
                    Toast.makeText(PlayerStatsActivity.this, "Failed to load player stats", Toast.LENGTH_SHORT).show();
                    noDataTextView.setText("Failed to load data. Please try again.");
                    noDataTextView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);  // Hide RecyclerView in case of failure
                }


        }

            @Override
            public void onFailure(Call<List<PlayerStats>> call, Throwable t) {
                Log.e("PlayerStatsActivity", "Network error: " + t.getMessage());
                Toast.makeText(PlayerStatsActivity.this, "Network error, please try again later.", Toast.LENGTH_SHORT).show();
                noDataTextView.setText("Network error. Please try again later.");
                noDataTextView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);  // Hide RecyclerView in case of failure
            }

        });
    }
}
