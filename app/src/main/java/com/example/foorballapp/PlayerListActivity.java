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
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlayerListActivity extends AppCompatActivity {
    private Spinner spinnerTeam1, spinnerTeam2;
    private RecyclerView recyclerViewTeam1, recyclerViewTeam2;
    private PlayerDetailsAdapter adapterTeam1, adapterTeam2;
    private List<Player> playersTeam1 = new ArrayList<>();
    private List<Player> playersTeam2 = new ArrayList<>();
    private ApiService apiService;
    private int selectedMatchID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_list);

        // Retrofit setup
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // Get match ID from intent
        selectedMatchID = getIntent().getIntExtra("matchId", -1);
        int team1Id = getIntent().getIntExtra("team1Id", -1);
        int team2Id = getIntent().getIntExtra("team2Id", -1);
        String team1Name = getIntent().getStringExtra("team1Name");
        String team2Name = getIntent().getStringExtra("team2Name");

        // Initialize views
        spinnerTeam1 = findViewById(R.id.spinnerTeam1);
        spinnerTeam2 = findViewById(R.id.spinnerTeam2);
        recyclerViewTeam1 = findViewById(R.id.recyclerViewTeam1);
        recyclerViewTeam2 = findViewById(R.id.recyclerViewTeam2);

        recyclerViewTeam1.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTeam2.setLayoutManager(new LinearLayoutManager(this));

        adapterTeam1 = new PlayerDetailsAdapter(this, playersTeam1, selectedMatchID, apiService);
        adapterTeam2 = new PlayerDetailsAdapter(this, playersTeam2, selectedMatchID, apiService);

        recyclerViewTeam1.setAdapter(adapterTeam1);
        recyclerViewTeam2.setAdapter(adapterTeam2);

        // Set team names to TextViews
        ((TextView) findViewById(R.id.team1Label)).setText(team1Name != null ? team1Name : "Team 1");
        ((TextView) findViewById(R.id.team2Label)).setText(team2Name != null ? team2Name : "Team 2");

        // Fetch players for both teams
        if (team1Id != -1) {
            fetchPlayersForTeam(team1Id, spinnerTeam1, playersTeam1, adapterTeam1);
        }
        if (team2Id != -1) {
            fetchPlayersForTeam(team2Id, spinnerTeam2, playersTeam2, adapterTeam2);
        }
    }

    private void fetchPlayersForTeam(int teamId, Spinner spinner, List<Player> playerList, PlayerDetailsAdapter adapter) {
        Call<List<Player>> call = apiService.getPlayersByTeamId(teamId);
        call.enqueue(new Callback<List<Player>>() {
            @Override
            public void onResponse(Call<List<Player>> call, Response<List<Player>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Player> allPlayers = response.body();
                    List<String> playerNames = new ArrayList<>();

                    // Add placeholder option
                    playerNames.add("Select a player");

                    for (Player player : allPlayers) {
                        if (player.getTeamID() == teamId) {
                            playerNames.add(player.getPlayerName() != null ? player.getPlayerName() : "Unknown Player");
                        }
                    }

                    if (playerNames.size() == 1) {
                        playerNames.add("No players available");
                    }

                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(PlayerListActivity.this,
                            android.R.layout.simple_spinner_item, playerNames);
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(spinnerAdapter);

                    // Set the default selection to "Select a player"
                    spinner.setSelection(0);

                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String selectedPlayerName = (String) parent.getItemAtPosition(position);
                            if (!selectedPlayerName.equals("Select a player") && !selectedPlayerName.equals("No players available")) {
                                Player selectedPlayer = null;

                                for (Player player : allPlayers) {
                                    if (player.getPlayerName().equals(selectedPlayerName)) {
                                        selectedPlayer = player;
                                        break;
                                    }
                                }

                                if (selectedPlayer != null) {
                                    adapter.addPlayers(Collections.singletonList(selectedPlayer));
                                    adapter.notifyDataSetChanged();

                                    // Insert player into the match
                                    if (selectedMatchID != -1) {
                                        Log.d("PlayerListActivity", "Inserting player ID: " + selectedPlayer.getPlayerID() + " into match ID: " + selectedMatchID);
                                        insertPlayerToMatch(selectedPlayer.getPlayerID(), selectedMatchID);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // Handle case where nothing is selected if needed
                        }
                    });
                } else {
                    Log.e("PlayerListActivity", "Failed to fetch players: " + response.message());
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(PlayerListActivity.this,
                            android.R.layout.simple_spinner_item, Collections.singletonList("No players available"));
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(spinnerAdapter);
                    spinner.setSelection(0);
                }
            }

            @Override
            public void onFailure(Call<List<Player>> call, Throwable t) {
                Log.e("PlayerListActivity", "Network error: " + t.getMessage());
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(PlayerListActivity.this,
                        android.R.layout.simple_spinner_item, Collections.singletonList("No players available"));
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(spinnerAdapter);
                spinner.setSelection(0);
            }
        });
    }

    private void insertPlayerToMatch(int playerId, int matchId) {
        PlayerMatchRequest matchRequest = new PlayerMatchRequest(playerId, matchId);

        Call<Void> call = apiService.assignMatchToPlayer(matchRequest);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("PlayerListActivity", "Player successfully inserted into match.");
                    Toast.makeText(PlayerListActivity.this, "Player successfully inserted into match.", Toast.LENGTH_SHORT).show();

                } else {
                    Log.e("PlayerListActivity", "Failed to insert player into match: " + response.message());
                    Toast.makeText(PlayerListActivity.this, "Failed to insert player into match.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("PlayerListActivity", "Network error: " + t.getMessage());
                Toast.makeText(PlayerListActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    // Inside PlayerListActivity
    public interface OnScoreUpdatedListener {
        void onScoreUpdated();
    }


}
