package com.myapp.minifootballstats;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.myapp.minifootballstats.api.ApiService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddMatchBottomSheetFragment extends BottomSheetDialogFragment {

    // Listener interface
    public interface OnMatchAddedListener {
        void onMatchAdded();
    }

    // Listener field
    private OnMatchAddedListener listener;

    // Method to set the listener
    public void setOnMatchAddedListener(OnMatchAddedListener listener) {
        this.listener = listener;
    }

    // Views and other fields
    private AutoCompleteTextView autoCompleteTeam1, autoCompleteTeam2;
    private EditText editTextScoreTeam1, editTextScoreTeam2, editTextMatchDate;
    private Button buttonSaveMatch;
    private ApiService apiService;
    private HashMap<String, Integer> teamMap = new HashMap<>();

    private static final Locale GREEK_LOCALE = new Locale("el", "GR");
    private static final SimpleDateFormat INPUT_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", GREEK_LOCALE);
    private static final SimpleDateFormat OUTPUT_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", GREEK_LOCALE);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_match_bottom_sheet, container, false);

        // Initialize views
        autoCompleteTeam1 = view.findViewById(R.id.autoCompleteTeam1);
        autoCompleteTeam2 = view.findViewById(R.id.autoCompleteTeam2);
        editTextScoreTeam1 = view.findViewById(R.id.editTextScoreTeam1);
        editTextScoreTeam2 = view.findViewById(R.id.editTextScoreTeam2);
        editTextMatchDate = view.findViewById(R.id.editTextMatchDate);
        buttonSaveMatch = view.findViewById(R.id.buttonSaveMatch);
        // Handle visibility of search icons
        autoCompleteTeam1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    view.findViewById(R.id.search1).setVisibility(View.GONE);
                } else {
                    view.findViewById(R.id.search1).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        autoCompleteTeam2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    view.findViewById(R.id.search2).setVisibility(View.GONE);
                } else {
                    view.findViewById(R.id.search2).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        autoCompleteTeam1.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && autoCompleteTeam1.getText().toString().isEmpty()) {
                view.findViewById(R.id.search1).setVisibility(View.VISIBLE);
            }
        });

        autoCompleteTeam2.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && autoCompleteTeam2.getText().toString().isEmpty()) {
                view.findViewById(R.id.search2).setVisibility(View.VISIBLE);
            }
        });



        // Set up Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // Fetch and populate teams
        fetchTeams();

        // Handle save button click
        buttonSaveMatch.setOnClickListener(v -> insertMatch());

        return view;
    }

    private void fetchTeams() {
        Call<List<Teams>> call = apiService.getTeamNames();
        call.enqueue(new Callback<List<Teams>>() {
            @Override
            public void onResponse(Call<List<Teams>> call, Response<List<Teams>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    populateAutoComplete(response.body());
                } else {
                    Toast.makeText(getContext(), "Failed to load teams", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Teams>> call, Throwable t) {
                Toast.makeText(getContext(), "Request failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateAutoComplete(List<Teams> teams) {
        ArrayList<String> teamNames = new ArrayList<>();
        for (Teams team : teams) {
            teamNames.add(team.getTeamName());
            teamMap.put(team.getTeamName(), team.getTeamID());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, teamNames);
        autoCompleteTeam1.setAdapter(adapter);
        autoCompleteTeam2.setAdapter(adapter);
    }

    private void insertMatch() {
        try {
            // Retrieve selected team names
            String selectedTeam1 = autoCompleteTeam1.getText().toString();
            String selectedTeam2 = autoCompleteTeam2.getText().toString();

            if (!teamMap.containsKey(selectedTeam1) || !teamMap.containsKey(selectedTeam2)) {
                Toast.makeText(getContext(), "Please select valid teams", Toast.LENGTH_SHORT).show();
                return;
            }

            // Retrieve corresponding team IDs
            int team1ID = teamMap.get(selectedTeam1);
            int team2ID = teamMap.get(selectedTeam2);

            int scoreTeam1 = Integer.parseInt(editTextScoreTeam1.getText().toString());
            int scoreTeam2 = Integer.parseInt(editTextScoreTeam2.getText().toString());
            String matchDateInput = editTextMatchDate.getText().toString();

            // Validate and format the date
            String matchDateFormatted = validateAndFormatDate(matchDateInput);
            if (matchDateFormatted == null) {
                Toast.makeText(getContext(), "Invalid date format. Please use YYYY-MM-DD HH:MM:SS", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create MatchRequest object
            MatchRequest matchRequest = new MatchRequest(team1ID, team2ID, scoreTeam1, scoreTeam2, matchDateFormatted);

            // Make the API call
            Call<InsertMatchResponse> call = apiService.insertMatch(matchRequest);
            call.enqueue(new Callback<InsertMatchResponse>() {
                @Override
                public void onResponse(Call<InsertMatchResponse> call, Response<InsertMatchResponse> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Match inserted successfully", Toast.LENGTH_SHORT).show();
                        dismiss(); // Close the dialog
                        if (listener != null) {
                            listener.onMatchAdded();
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to insert match. Status code: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<InsertMatchResponse> call, Throwable t) {
                    Toast.makeText(getContext(), "Request failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Please enter valid numbers", Toast.LENGTH_SHORT).show();
        }
    }

    private String validateAndFormatDate(String dateInput) {
        try {
            Date date = INPUT_FORMAT.parse(dateInput);
            if (date != null) {
                return OUTPUT_FORMAT.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
