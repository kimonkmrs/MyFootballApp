package com.myapp.minifootballstats;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myapp.minifootballstats.api.ApiService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlayerListActivity extends AppCompatActivity {
    private ImageView back, menu;
    private long totalExtraTime = 0; // Accumulated extra time during pauses

    private TextView mainTimerText, extraTimeText;
    private Spinner spinnerTeam1, spinnerTeam2;
    private RecyclerView recyclerViewTeam1, recyclerViewTeam2;
    private PlayerDetailsAdapter adapterTeam1, adapterTeam2;
    private List<Player> playersTeam1 = new ArrayList<>();
    private List<Player> playersTeam2 = new ArrayList<>();
    private ApiService apiService;
    private int selectedMatchID;
    private Button buttonAddPlayerTeam1,btnStart, btnPause, btnStop;
    private Button buttonAddPlayerTeam2;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;
    private long startTime = 0;
    private boolean isRunning = false;

    private boolean isPaused = false;

    private Handler extraTimeHandler = new Handler();
    private Runnable extraTimeRunnable;
    private long extraStartTime = 0;
    private boolean isExtraRunning = false;
    private long extraTimeAccumulated = 0; // total accumulated extra pause time
    private long extraPauseStartTime = 0;  // when pause started





    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_list);
        // Initialize views
        back = findViewById(R.id.left_icon);
        back.setOnClickListener(v -> onBackPressed());

        menu = findViewById(R.id.right_icon);
        menu.setOnClickListener(this::showPopupMenu);


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
        buttonAddPlayerTeam1 = findViewById(R.id.roundButtonPlus1);
        buttonAddPlayerTeam2 = findViewById(R.id.roundButtonPlus2);
        mainTimerText = findViewById(R.id.mainTimerText);
        extraTimeText = findViewById(R.id.extraTimeText);
        btnStart = findViewById(R.id.btnStart);
        btnPause = findViewById(R.id.btnPause);
        btnStop = findViewById(R.id.btnStop);

        buttonAddPlayerTeam1.setOnClickListener(v -> showAddPlayerDialog(team1Id));  // Use team1Id from intent
        buttonAddPlayerTeam2.setOnClickListener(v -> showAddPlayerDialog(team2Id));  // Use team2Id from intent
        // Assuming TeamID 2 for example

        adapterTeam1 = new PlayerDetailsAdapter(this, playersTeam1, selectedMatchID, apiService);
        adapterTeam2 = new PlayerDetailsAdapter(this, playersTeam2, selectedMatchID, apiService);

        recyclerViewTeam1.setAdapter(adapterTeam1);
        recyclerViewTeam2.setAdapter(adapterTeam2);

        // Start Button Logic
        btnStart.setOnClickListener(view -> startTimer());
        btnPause.setOnClickListener(view -> pauseOrUnpauseTimer());
        btnStop.setOnClickListener(view -> stopTimer());

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
                                    adapter.notifyItemInserted(adapter.getItemCount() - 1);


                                    // Insert player into the match
                                    if (selectedMatchID != -1) {
                                        Log.d("PlayerListActivity", "Inserting player ID: " + selectedPlayer.getPlayerID() + " into match ID: " + selectedMatchID);
                                        insertPlayerToMatch(selectedPlayer.getPlayerID(), selectedMatchID);
                                    }
                                    // Reset the spinner back to "Select a player"
                                    spinner.setSelection(0);
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
    public void showAddPlayerDialog(int teamID) {
        // Pass a listener to refresh the player list after a player is added
        AddPlayerDialogFragment dialog = new AddPlayerDialogFragment(teamID, new AddPlayerDialogFragment.OnPlayerAddedListener() {
            @Override
            public void onPlayerAdded() {
                // Refresh the player list for the correct team
                if (teamID == getIntent().getIntExtra("team1Id", -1)) {
                    fetchPlayersForTeam(teamID, spinnerTeam1, playersTeam1, adapterTeam1);
                } else if (teamID == getIntent().getIntExtra("team2Id", -1)) {
                    fetchPlayersForTeam(teamID, spinnerTeam2, playersTeam2, adapterTeam2);
                }
            }
        });
        dialog.show(getSupportFragmentManager(), "AddPlayerDialogFragment");
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
    private void showPopupMenu(View anchorView) {
        PopupMenu popupMenu = new PopupMenu(this, anchorView);
        popupMenu.getMenuInflater().inflate(R.menu.menu_players_activity, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.logout) {
                SessionManager.logout(this);  // This clears session data and redirects to Login
                return true;
            } else if (item.getItemId() == R.id.main_menu) {
                Intent intent = new Intent(PlayerListActivity.this, MainActivity.class);
                startActivity(intent);
                return true;

            } else if (item.getItemId() == R.id.menu_item_players) {
                Intent intent = new Intent(PlayerListActivity.this, PlayerStatsActivity.class);
                startActivity(intent);
                return true;

            }else if (item.getItemId() == R.id.menu_item_standings) {
                Intent intent = new Intent(PlayerListActivity.this, StandingsActivity.class);
                startActivity(intent);
                return true;

            }
            return false;
        });

        popupMenu.show();
    }
//    private void startTimer() {
//        TimerUtils.startTimer(this);
//        startHandlerLoop();
//        btnStart.setEnabled(false);
//        btnPause.setEnabled(true);
//        btnStop.setEnabled(true);
//        btnPause.setText("Pause");
//    }
private void startTimer() {
    startTime = System.currentTimeMillis();
    TimerUtils.saveStartTime(this, selectedMatchID, startTime);
    TimerUtils.setTimerPaused(this, selectedMatchID, false);
    TimerUtils.saveElapsedTime(this, selectedMatchID, 0);
    startHandlerLoop();

    btnStart.setEnabled(false);
    btnPause.setEnabled(true);
    btnStop.setEnabled(true);
    btnPause.setText("Pause");
    isRunning = true;
}
    private void pauseOrUnpauseTimer() {
        if (!isPaused) {
            // Pause clicked — main timer keeps running normally
            // Start extra timer counting
            extraPauseStartTime = System.currentTimeMillis();
            startExtraTime();
            isPaused = true;
            btnPause.setText("Unpause");
        } else {
            // Unpause clicked — stop extra timer counting but keep accumulated
            long pauseDuration = System.currentTimeMillis() - extraPauseStartTime;
            extraTimeAccumulated += pauseDuration;
            stopExtraTime();
            isPaused = false;
            btnPause.setText("Pause");
        }
    }







    private void startHandlerLoop() {
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                long start = TimerUtils.getStartTime(PlayerListActivity.this, selectedMatchID); // Fix: add selectedMatchID
                long elapsed = System.currentTimeMillis() - start;
                TimerUtils.saveElapsedTime(PlayerListActivity.this, selectedMatchID, elapsed); // Fix: add selectedMatchID

                int seconds = (int) (elapsed / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                mainTimerText.setText(String.format("%02d:%02d", minutes, seconds));
                timerHandler.postDelayed(this, 1000);
            }
        };
        timerHandler.post(timerRunnable);
        isRunning = true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (TimerUtils.isTimerRunning(this, selectedMatchID)) {
            boolean wasPaused = TimerUtils.isTimerPaused(this, selectedMatchID);
            long displayTimeMillis;

            SharedPreferences prefs = getSharedPreferences("TimerPrefs", MODE_PRIVATE);
            extraTimeAccumulated = prefs.getLong("extraTimeAccumulated", 0);
            extraPauseStartTime = prefs.getLong("extraPauseStartTime", 0);
            isPaused = prefs.getBoolean("isPaused", false);

            if (wasPaused) {
                displayTimeMillis = TimerUtils.getElapsedTime(this, selectedMatchID);
                updateTimerDisplay(displayTimeMillis);
                btnPause.setText("Unpause");

                // Update extra time display manually
                long extraElapsed = extraTimeAccumulated;
                if (isPaused) {
                    extraElapsed += System.currentTimeMillis() - extraPauseStartTime;
                }
                updateExtraTimeUI(extraElapsed);

                // Optionally resume updates
                if (!isExtraRunning && isPaused) {
                    extraPauseStartTime = System.currentTimeMillis();
                    startExtraTime();
                }

            } else {
                long savedStartTime = TimerUtils.getStartTime(this, selectedMatchID);
                startTime = savedStartTime;
                displayTimeMillis = System.currentTimeMillis() - savedStartTime;

                startHandlerLoop();
                isRunning = true;

                if (isPaused && !isExtraRunning) {
                    startExtraTime();
                }
            }

            btnStart.setEnabled(false);
            btnPause.setEnabled(true);
            btnStop.setEnabled(true);
        } else {
            // Not running: Still update the extra time display based on saved value
            long extraElapsed = extraTimeAccumulated;
            if (isPaused && extraPauseStartTime > 0) {
                extraElapsed += System.currentTimeMillis() - extraPauseStartTime;
            }
            updateExtraTimeUI(extraElapsed);
        }
        // --- Always update extra time display ---
        SharedPreferences prefs = getSharedPreferences("TimerPrefs", MODE_PRIVATE);
        extraTimeAccumulated = prefs.getLong("extraTimeAccumulated", 0);
        extraPauseStartTime = prefs.getLong("extraPauseStartTime", 0);
        isPaused = prefs.getBoolean("isPaused", false);

// Only add pause time if paused AND pauseStartTime is valid
        long extraElapsed = extraTimeAccumulated;
        if (isPaused && extraPauseStartTime > 0) {
            extraElapsed += System.currentTimeMillis() - extraPauseStartTime;
        }
        updateExtraTimeUI(extraElapsed);
    }


    private void updateTimerDisplay(long millis) {
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        mainTimerText.setText(String.format("%02d:%02d", minutes, seconds));
    }
    private void updateExtraTimeUI(long extraMillis) {
        int seconds = (int) (extraMillis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        extraTimeText.setText(String.format("%02d:%02d", minutes, seconds));
    }


    // In stopTimer(), reset extraTimeAccumulated to 0 and update UI:
    private void stopTimer() {
        timerHandler.removeCallbacks(timerRunnable);
        stopExtraTime();

        isRunning = false;
        isPaused = false;
        extraTimeAccumulated = 0;
        extraPauseStartTime = 0;

        mainTimerText.setText("00:00");
        extraTimeText.setText("00:00");

        btnStart.setEnabled(true);
        btnPause.setEnabled(false);
        btnStop.setEnabled(false);
        btnPause.setText("Pause");

        // Clear saved prefs
        SharedPreferences prefs = getSharedPreferences("TimerPrefs", MODE_PRIVATE);
        prefs.edit().clear().apply();

        TimerUtils.clearTimerData(this, selectedMatchID);
    }
    private void stopExtraTime() {
        extraTimeHandler.removeCallbacks(extraTimeRunnable);
        isExtraRunning = false;

    }
    private void startExtraTime() {
        extraTimeRunnable = new Runnable() {
            @Override
            public void run() {
                long elapsedExtra = extraTimeAccumulated;
                if (isPaused) {
                    // Add current extra paused duration while pause active
                    elapsedExtra += System.currentTimeMillis() - extraPauseStartTime;
                }
                int seconds = (int) (elapsedExtra / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                extraTimeText.setText(String.format("%02d:%02d", minutes, seconds));
                extraTimeHandler.postDelayed(this, 1000);
            }
        };
        extraTimeHandler.post(extraTimeRunnable);
        isExtraRunning = true;

    }

    public static void clearTimerData(Context context, int matchId) {
        SharedPreferences prefs = context.getSharedPreferences("TimerPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.remove("startTime_" + matchId);
        editor.remove("elapsedTime_" + matchId);
        editor.remove("timerPaused_" + matchId);
        editor.apply();
    }
    @Override
    protected void onPause() {
        super.onPause();
        // Save current extra pause time and pause info
        SharedPreferences prefs = getSharedPreferences("TimerPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // If currently paused, update accumulated with current ongoing pause duration
        if (isPaused) {
            long currentPauseDuration = System.currentTimeMillis() - extraPauseStartTime;
            extraTimeAccumulated += currentPauseDuration;
            extraPauseStartTime = System.currentTimeMillis(); // reset start to now
        }

        editor.putLong("extraTimeAccumulated", extraTimeAccumulated);
        editor.putBoolean("isPaused", isPaused);
        editor.putLong("extraPauseStartTime", extraPauseStartTime);
        editor.apply();
    }




}
