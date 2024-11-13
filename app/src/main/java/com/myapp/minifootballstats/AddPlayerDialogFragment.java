package com.myapp.minifootballstats;

import android.os.Bundle;
import android.util.Log;  // Import for logging
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.myapp.minifootballstats.api.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddPlayerDialogFragment extends DialogFragment {
    private static final String TAG = "AddPlayerDialogFragment";  // Tag for logging

    private EditText editTextPlayerName,editTextPlayerPosition;
    private Button buttonAddPlayer;

    private ApiService apiService;
    private int teamID;
    // Add a listener interface for callbacks

    private OnPlayerAddedListener listener;
    public AddPlayerDialogFragment(int teamID, OnPlayerAddedListener listener) {
        this.teamID = teamID;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_player_dialog, container, false);

        editTextPlayerName = view.findViewById(R.id.editTextPlayerName);
        editTextPlayerPosition = view.findViewById(R.id.editTextPlayerPosition);
        buttonAddPlayer = view.findViewById(R.id.buttonAddPlayer);

        // Set up Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/") // Replace with your server URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        buttonAddPlayer.setOnClickListener(v -> addPlayer());

        return view;
    }

    private void addPlayer() {
        String playerName = editTextPlayerName.getText().toString().trim();
        String playerPosition = editTextPlayerPosition.getText().toString().trim();

        Log.d(TAG, "Attempting to add player: " + playerName + " with position: " + playerPosition);

        if (playerName.isEmpty() || playerPosition.isEmpty()) {
            Log.e(TAG, "Player name or position is empty.");
            Toast.makeText(getContext(), "Please enter both player name and position", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new PlayerRequest object using teamID, playerName, and playerPosition
        PlayerRequest newPlayerRequest = new PlayerRequest(teamID, playerName, playerPosition);

        Call<Void> call = apiService.addPlayer(newPlayerRequest);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d(TAG, "onResponse: Status code: " + response.code());
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Player added successfully", Toast.LENGTH_SHORT).show();
                    dismiss(); // Close the dialog
                    if (listener != null) {
                        listener.onPlayerAdded();
                    }
                } else {
                    Log.e(TAG, "Failed to add player. Response code: " + response.code() + ", message: " + response.message());
                    try {
                        Log.e(TAG, "Response body: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e(TAG, "Error while reading error body", e);
                    }
                    Toast.makeText(getContext(), "Failed to add player", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Request failed: " + t.getMessage(), t);
                Toast.makeText(getContext(), "Request failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public interface OnPlayerAddedListener {
        void onPlayerAdded();
    }
}
