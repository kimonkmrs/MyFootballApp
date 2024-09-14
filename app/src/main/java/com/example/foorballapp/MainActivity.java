package com.example.foorballapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
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
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements AddMatchBottomSheetFragment.OnMatchAddedListener {
    private ImageView back, menu;
    private boolean isAdmin;
    private ApiService apiService;
    private RecyclerView recyclerView;
    private MatchAdapter matchAdapter;
    private TextView noMatchesTextView;
    private List<Match> matchList = new ArrayList<>();
    private Button roundButtonPlus;
    private Button roundButtonMinus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Retrieve the role information from the Intent
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);

        // Initialize views
        back = findViewById(R.id.left_icon);
        back.setOnClickListener(v -> onBackPressed());

        menu = findViewById(R.id.right_icon);
        menu.setOnClickListener(this::showPopupMenu);
        recyclerView = findViewById(R.id.recyclerViewMatches);
        roundButtonPlus = findViewById(R.id.roundButtonPlus);
        roundButtonMinus = findViewById(R.id.roundButtonMinus);
        noMatchesTextView = findViewById(R.id.noMatchesTextView);

        // Handle UI and functionality based on user role
        if (isAdmin) {
            Toast.makeText(this, "Welcome Admin!", Toast.LENGTH_SHORT).show();
            roundButtonPlus.setVisibility(View.VISIBLE);
            roundButtonMinus.setVisibility(View.VISIBLE);
            roundButtonPlus.setOnClickListener(v -> showAddMatchDialog());
            roundButtonMinus.setOnClickListener(v -> showRemoveMatchDialog());
        } else {
            Toast.makeText(this, "Welcome User!", Toast.LENGTH_SHORT).show();
            roundButtonPlus.setVisibility(View.GONE);
            roundButtonMinus.setVisibility(View.GONE);
        }

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

    @Override
    public void onMatchAdded() {
        fetchTodayMatches(); // Refresh the match list
    }




    private void showPopupMenu(View anchorView) {
        PopupMenu popupMenu = new PopupMenu(this, anchorView);
        popupMenu.getMenuInflater().inflate(R.menu.menus, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_item_layout1) {
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                return true;
            } else if (item.getItemId() == R.id.menu_item_standings) {
                Intent intent = new Intent(MainActivity.this, StandingsActivity.class);
                startActivity(intent);
                return true;

            }
            return false;
        });

        popupMenu.show();
    }

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
                        noMatchesTextView.setVisibility(View.GONE);

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
                        noMatchesTextView.setVisibility(View.VISIBLE); // No matches available
                    }
                } else {
                    Log.e("MainActivity", "Request failed: " + response.code());
                    noMatchesTextView.setVisibility(View.VISIBLE); // Show the "No match today" message
                }
            }

            @Override
            public void onFailure(Call<List<Match>> call, Throwable t) {
                Log.e("MainActivity", "Network error: " + t.getMessage());
                noMatchesTextView.setVisibility(View.VISIBLE); // Show the "No match today" message
                Toast.makeText(MainActivity.this, "Network error, please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
