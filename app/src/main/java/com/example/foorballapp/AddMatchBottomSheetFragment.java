package com.example.foorballapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.foorballapp.api.ApiService;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddMatchBottomSheetFragment extends BottomSheetDialogFragment {
    public interface OnMatchAddedListener {
        void onMatchAdded();
    }

    private OnMatchAddedListener onMatchAddedListener;

    public void setOnMatchAddedListener(OnMatchAddedListener listener) {
        this.onMatchAddedListener = listener;
    }
    private EditText editTextTeam1ID, editTextTeam2ID, editTextScoreTeam1, editTextScoreTeam2, editTextMatchDate;
    private Button buttonSaveMatch;
    private ApiService apiService;

    // Greek locale
    private static final Locale GREEK_LOCALE = new Locale("el", "GR");
    private static final SimpleDateFormat INPUT_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat OUTPUT_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_match_bottom_sheet, container, false);

        // Initialize views
        editTextTeam1ID = view.findViewById(R.id.editTextTeam1ID);
        editTextTeam2ID = view.findViewById(R.id.editTextTeam2ID);
        editTextScoreTeam1 = view.findViewById(R.id.editTextScoreTeam1);
        editTextScoreTeam2 = view.findViewById(R.id.editTextScoreTeam2);
        editTextMatchDate = view.findViewById(R.id.editTextMatchDate);
        buttonSaveMatch = view.findViewById(R.id.buttonSaveMatch);


        // Set up Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // Handle save button click
        buttonSaveMatch.setOnClickListener(v -> insertMatch());

        return view;
    }

    private void insertMatch() {
        try {
            // Retrieve input values
            int team1ID = Integer.parseInt(editTextTeam1ID.getText().toString());
            int team2ID = Integer.parseInt(editTextTeam2ID.getText().toString());
            int scoreTeam1 = Integer.parseInt(editTextScoreTeam1.getText().toString());
            int scoreTeam2 = Integer.parseInt(editTextScoreTeam2.getText().toString());
            String matchDateInput = editTextMatchDate.getText().toString();

            // Validate and format the date
            String matchDateFormatted = validateAndFormatDate(matchDateInput);
            if (matchDateFormatted == null) {
                Toast.makeText(getContext(), "Invalid date format. Please use YYYY-MM-DD HH:MM:SS", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create MatchRequest object with matchDate
            MatchRequest matchRequest = new MatchRequest(team1ID, team2ID, scoreTeam1, scoreTeam2, matchDateFormatted);

            // Create MatchRequest object
            Call<InsertMatchResponse> call = apiService.insertMatch(matchRequest);
            call.enqueue(new Callback<InsertMatchResponse>() {
                @Override
                public void onResponse(Call<InsertMatchResponse> call, Response<InsertMatchResponse> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Match inserted successfully", Toast.LENGTH_SHORT).show();
                        dismiss(); // Close the dialog
                        if (onMatchAddedListener != null) {
                            onMatchAddedListener.onMatchAdded();
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
            Log.d("AddMatchFragment", "Input date: " + dateInput);
            Date date = INPUT_FORMAT.parse(dateInput);
            if (date != null) {
                String formattedDate = OUTPUT_FORMAT.format(date);
                Log.d("AddMatchFragment", "Formatted date: " + formattedDate);
                return formattedDate;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
