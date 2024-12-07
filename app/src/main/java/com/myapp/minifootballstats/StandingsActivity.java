package com.myapp.minifootballstats;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
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

public class StandingsActivity extends AppCompatActivity {
    private ImageView back, menu;
    private RecyclerView recyclerView;
    private StandingsAdapter standingsAdapter;
    private List<Standing> standingsList = new ArrayList<>();
    private TextView noDataTextView;
    private ApiService apiService;
    private Spinner groupSpinner;
    private List<String> groupNames = new ArrayList<>();
    private String selectedGroup = "General Table";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standings);
        // Initialize views
        back = findViewById(R.id.left_icon);
        back.setOnClickListener(v -> onBackPressed());

        menu = findViewById(R.id.right_icon);
        menu.setOnClickListener(this::showPopupMenu);

        recyclerView = findViewById(R.id.recyclerViewStandings);
        noDataTextView = findViewById(R.id.noDataTextView);
        groupSpinner = findViewById(R.id.groupSpinner);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        standingsAdapter = new StandingsAdapter(standingsList);
        recyclerView.setAdapter(standingsAdapter);
        // Add grid divider decoration
        int dividerSize = getResources().getDimensionPixelSize(R.dimen.divider_size);
        recyclerView.addItemDecoration(new GridDividerDecoration(this, R.color.divider_color, dividerSize));


        Retrofit retrofit = NetworkClient.getRetrofitInstance();
        apiService = retrofit.create(ApiService.class);

        fetchStandings();
        fetchGroupNames();

        setupSpinnerListener();
    }

    private void fetchGroupNames() {
        Call<List<Group>> call = apiService.getGroups();
        call.enqueue(new Callback<List<Group>>() {
            @Override
            public void onResponse(Call<List<Group>> call, Response<List<Group>> response) {
                if (response.isSuccessful()) {
                    List<Group> groups = response.body();
                    if (groups != null && !groups.isEmpty()) {
                        groupNames.clear();
                        // Add only actual group names from the API
                        for (Group group : groups) {
                            Log.d("StandingsActivity", "Adding group: " + group.getGroupName());
                            groupNames.add(group.getGroupName());
                        }
                        populateSpinner();
                    }
                } else {
                    Log.e("StandingsActivity", "Group request failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Group>> call, Throwable t) {
                Log.e("StandingsActivity", "Failed to fetch group names: " + t.getMessage());
            }
        });
    }

    private void populateSpinner() {
        // Create a new list with "General Table" as the first default option
        List<String> spinnerOptions = new ArrayList<>();
        spinnerOptions.add("General Table"); // Add "General Table" as the default option
        spinnerOptions.addAll(groupNames);   // Add the actual group names

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupSpinner.setAdapter(adapter);

        // Set "General Table" as the default selection (position 0)
        groupSpinner.setSelection(0);
    }

    private void setupSpinnerListener() {
        groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Set the selectedGroup based on the position selected
                if (position == 0) {
                    selectedGroup = "General Table"; // Default option
                } else if (position > 0 && position <= groupNames.size()) {
                    selectedGroup = groupNames.get(position - 1); // Adjust index due to "General Table"
                } else {
                    Log.e("StandingsActivity", "Invalid position selected: " + position);
                    Toast.makeText(StandingsActivity.this, "Invalid selection", Toast.LENGTH_SHORT).show();
                }
                fetchStandings(); // Fetch standings based on the selected group
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Default action if needed, "General Table" will be selected by default
            }
        });
    }


    private void fetchStandings() {
        Call<List<Standing>> call;
        if (selectedGroup.equals("General Table")) {
            call = apiService.getGeneralStandings();
        } else {
            call = apiService.getStandingsByGroup(selectedGroup);
        }

        call.enqueue(new Callback<List<Standing>>() {
            @Override
            public void onResponse(Call<List<Standing>> call, Response<List<Standing>> response) {
                if (response.isSuccessful()) {
                    List<Standing> standings = response.body();
                    if (standings != null && !standings.isEmpty()) {
                        standingsList.clear();
                        standingsList.addAll(standings);
                        standingsAdapter.notifyDataSetChanged();
                        noDataTextView.setVisibility(View.GONE);
                    } else {
                        noDataTextView.setVisibility(View.VISIBLE);
                    }
                } else {
                    Log.e("StandingsActivity", "Request failed: " + response.code());
                    Toast.makeText(StandingsActivity.this, "Failed to load standings", Toast.LENGTH_SHORT).show();
                    noDataTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<Standing>> call, Throwable t) {
                Log.e("StandingsActivity", "Network error: " + t.getMessage());
                Toast.makeText(StandingsActivity.this, "Network error, please try again later.", Toast.LENGTH_SHORT).show();
                noDataTextView.setVisibility(View.VISIBLE);
            }
        });
    }
    private void showPopupMenu(View anchorView) {
        PopupMenu popupMenu = new PopupMenu(this, anchorView);
        popupMenu.getMenuInflater().inflate(R.menu.menu_standings, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.logout) {
                SessionManager.logout(this);  // This clears session data and redirects to Login
                return true;
            } else if (item.getItemId() == R.id.main_menu) {
                Intent intent = new Intent(StandingsActivity.this, MainActivity.class);
                intent.putExtra("refresh_data", true);
                startActivity(intent);
                return true;

            } else if (item.getItemId() == R.id.menu_item_players) {
                Intent intent = new Intent(StandingsActivity.this, PlayerStatsActivity.class);
                startActivity(intent);
                return true;

            }
            return false;
        });

        popupMenu.show();
    }
}
