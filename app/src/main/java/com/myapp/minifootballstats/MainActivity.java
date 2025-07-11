package com.myapp.minifootballstats;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myapp.minifootballstats.api.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements AddMatchBottomSheetFragment.OnMatchAddedListener {
    private ImageView back, menu;
    private boolean isAdmin;
    private ApiService apiService;
    private RecyclerView recyclerView;
    private MatchAdapter matchAdapter;
//    private TextView noMatchesTextView;
    private List<Match> matchList = new ArrayList<>();
    private Button roundButtonPlus;
    private Button roundButtonMinus;
    private View emptyStateContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Retrieve the role information from the Intent first
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);

// Store isAdmin in SharedPreferences
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isAdmin", isAdmin); // Store the value
        editor.apply();




        // Initialize views
        back = findViewById(R.id.left_icon);
        back.setOnClickListener(v -> onBackPressed());

        menu = findViewById(R.id.right_icon);
        menu.setOnClickListener(this::showPopupMenu);
        recyclerView = findViewById(R.id.recyclerViewMatches);
        roundButtonPlus = findViewById(R.id.roundButtonPlus);
        roundButtonMinus = findViewById(R.id.roundButtonMinus);
//        noMatchesTextView = findViewById(R.id.noMatchesTextView);
        emptyStateContainer = findViewById(R.id.emptyStateContainer);


        // Handle UI and functionality based on user role
        /*if (isAdmin) {
            Toast.makeText(this, "Welcome Admin!", Toast.LENGTH_SHORT).show();
            roundButtonPlus.setVisibility(View.VISIBLE);
            roundButtonMinus.setVisibility(View.VISIBLE);
            roundButtonPlus.setOnClickListener(v -> showAddMatchDialog());
            roundButtonMinus.setOnClickListener(v -> showRemoveMatchDialog());
        } else {
            Toast.makeText(this, "Welcome User!", Toast.LENGTH_SHORT).show();
            roundButtonPlus.setVisibility(View.GONE);
            roundButtonMinus.setVisibility(View.GONE);
        }*/
        initializeUI();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        matchAdapter = new MatchAdapter(matchList, match -> {
            Intent intent = new Intent(MainActivity.this, PlayerListActivity.class);
            intent.putExtra("team1Id", match.getTeam1ID());
            intent.putExtra("team2Id", match.getTeam2ID());
            intent.putExtra("team1Name", match.getTeam1Name()); // Pass the team name
            intent.putExtra("team2Name", match.getTeam2Name()); // Pass the team name
            intent.putExtra("matchId", match.getMatchID()); // Pass match ID as well
            startActivity(intent);
        }, isAdmin);

        recyclerView.setAdapter(matchAdapter);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        fetchTodayMatches();
    }
    private void initializeUI() {
        TextView addMatchDescription = findViewById(R.id.addMatchDescription);
        TextView removeMatchDescription = findViewById(R.id.removeMatchDescription);

        if (isAdmin) {
            Toast.makeText(this, "Welcome Admin!", Toast.LENGTH_SHORT).show();
            roundButtonPlus.setVisibility(View.VISIBLE);
            roundButtonMinus.setVisibility(View.VISIBLE);
            addMatchDescription.setVisibility(View.VISIBLE);
            removeMatchDescription.setVisibility(View.VISIBLE);
            roundButtonPlus.setOnClickListener(v -> showAddMatchDialog());
            roundButtonMinus.setOnClickListener(v -> showRemoveMatchDialog());
        } else {
            Toast.makeText(this, "Welcome User!", Toast.LENGTH_SHORT).show();
            roundButtonPlus.setVisibility(View.GONE);
            roundButtonMinus.setVisibility(View.GONE);
            addMatchDescription.setVisibility(View.GONE);
            removeMatchDescription.setVisibility(View.GONE);
        }
    }


    @Override
    public void onMatchAdded() {
        fetchTodayMatches(); // Refresh the match list
    }
    // New method to remove a match from the list and notify the adapter
    public void removeMatchFromList(int matchID) {
        for (int i = 0; i < matchList.size(); i++) {
            if (matchList.get(i).getMatchID() == matchID) {
                matchList.remove(i);
                matchAdapter.notifyItemRemoved(i);  // Notify adapter to remove item from the RecyclerView
                break;
            }
        }

        // Check if the match list is empty after removal

//            noMatchesTextView.setVisibility(View.VISIBLE); // Show "no matches" message
        if (matchList.isEmpty()) {
            emptyStateContainer.setVisibility(View.VISIBLE); // Show animation
            recyclerView.setVisibility(View.GONE);
        }


    }





    private void showPopupMenu(View anchorView) {
        PopupMenu popupMenu = new PopupMenu(this, anchorView);
        popupMenu.getMenuInflater().inflate(R.menu.menus, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_item_layout1) {
                SessionManager.logout(this);  // This clears session data and redirects to Login
                return true;
            } else if (item.getItemId() == R.id.menu_item_standings) {
                Intent intent = new Intent(MainActivity.this, StandingsActivity.class);
                startActivity(intent);
                return true;
            } else if (item.getItemId() == R.id.menu_item_players) {
                Intent intent = new Intent(MainActivity.this, PlayerStatsActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });

        popupMenu.show();
    }
    /*private void logoutUser() {
        // Clear the SharedPreferences
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear(); // Clear all stored data
        editor.apply();

        // Reset any in-memory flags
        isAdmin = false;

        // Redirect to the login screen
        Intent intent = new Intent(MainActivity.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        // Finish the current activity
        finish();
    }*/


    private void showAddMatchDialog() {
        AddMatchBottomSheetFragment bottomSheetFragment = new AddMatchBottomSheetFragment();
        bottomSheetFragment.setOnMatchAddedListener(this);
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    private void showRemoveMatchDialog() {
        RemoveMatchBottomSheetFragment removeMatchFragment = new RemoveMatchBottomSheetFragment();
        removeMatchFragment.show(getSupportFragmentManager(), removeMatchFragment.getTag());
    }

    public void fetchTodayMatches() {
        Call<List<Match>> call = apiService.getTodayMatches();
        call.enqueue(new Callback<List<Match>>() {
            @Override
            public void onResponse(Call<List<Match>> call, Response<List<Match>> response) {
                if (response.isSuccessful()) {
                    List<Match> matches = response.body();
                    if (matches != null && !matches.isEmpty()) {
                        matchList.clear();
                        matchList.addAll(matches);
                        matchAdapter.notifyDataSetChanged();
                        emptyStateContainer.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
//                        noMatchesTextView.setVisibility(View.GONE);

                        // Log the response to check the data
                        for (Match match : matches) {
                            Log.d("MainActivity", "Match ID: " + match.getMatchID() +
                                    ", Team 1: " + match.getTeam1Name() +
                                    ", Team 2: " + match.getTeam2Name() +
                                    ", Score Team 1: " + match.getScoreTeam1() +
                                    ", Score Team 2: " + match.getScoreTeam2() +
                                    ", Match Date: " + match.getMatchDate());
                        }
                    } else {
//                        noMatchesTextView.setVisibility(View.VISIBLE); // No matches available
                        emptyStateContainer.setVisibility(View.VISIBLE); // Show "No matches today"
                        recyclerView.setVisibility(View.GONE);
                    }
                } else {
                    Log.e("MainActivity", "Request failed: " + response.code());
//                    noMatchesTextView.setVisibility(View.VISIBLE); // Show the "No match today" message
                    emptyStateContainer.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Match>> call, Throwable t) {
                Log.e("MainActivity", "Network error: " + t.getMessage());
//                noMatchesTextView.setVisibility(View.VISIBLE); // Show the "No match today" message
                emptyStateContainer.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                //Toast.makeText(MainActivity.this, "Network error, please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void refreshMatchList() {
        // Re-fetch the match list from the server
        fetchTodayMatches();

        // Notify the adapter to refresh the UI
        matchAdapter.notifyDataSetChanged();
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Fetch today's matches and refresh the UI when the activity is resumed
        fetchTodayMatches();
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // Update the Intent
        handleIntent(intent); // Handle any intent data or actions
        initializeUI();
    }

    private void handleIntent(Intent intent) {
        // Check if there's any specific action or data to handle
        boolean refreshData = intent.getBooleanExtra("refresh_data", false);
        if (refreshData) {
            refreshMatchList(); // Custom method to refresh match data
        }

    }



}
