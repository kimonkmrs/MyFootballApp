package com.myapp.minifootballstats;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myapp.minifootballstats.api.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayerDetailsAdapter extends RecyclerView.Adapter<PlayerDetailsAdapter.PlayerViewHolder> {
    private List<Player> playerList;
    private int matchId;
    private ApiService apiService;
    private Context context;
    private List<String> positionList = new ArrayList<>(); // Store positions for AutoComplete
    public interface ScoreUpdateListener {
        void onScoreUpdated();
    }


    public PlayerDetailsAdapter(Context context, List<Player> playerList, int matchId, ApiService apiService) {
        this.context = context;
        this.playerList = playerList;
        this.matchId = matchId;
        this.apiService = apiService;
        fetchPositions();

    }
    private void fetchPositions() {
        Call<List<PlayerStats>> call = apiService.getPosition();
        call.enqueue(new Callback<List<PlayerStats>>() {
            @Override
            public void onResponse(Call<List<PlayerStats>> call, Response<List<PlayerStats>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (PlayerStats stat : response.body()) {
                        positionList.add(stat.getPosition());
                    }
                } else {
                    Log.e("PlayerDetailsAdapter", "Failed to fetch positions. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<PlayerStats>> call, Throwable t) {
                Log.e("PlayerDetailsAdapter", "Network error: " + t.getMessage());
            }
        });
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player, parent, false);
        return new PlayerViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        Player player = playerList.get(position);

        holder.playerNameTextView.setText(player.getPlayerName());

        // Set labels
        holder.goalsLabel.setText("Goals");
        holder.yellowCardsLabel.setText("Yellow Cards");
        holder.redCardsLabel.setText("Red Cards");

        // Set icons
        holder.goalsIcon.setImageResource(R.drawable.baseline_sports_soccer_24);
        holder.yellowCardsIcon.setImageResource(R.drawable.baseline_yellow_card);
        holder.redCardsIcon.setImageResource(R.drawable.baseline_red_card);

        // SharedPreferences
        SharedPreferences sp = context.getSharedPreferences("PlayerStats", Context.MODE_PRIVATE);

        // ✅ Load saved values
        int goals = sp.getInt(matchId + "_" + player.getPlayerID() + "_goals", 0);
        int yellowCards = sp.getInt(matchId + "_" + player.getPlayerID() + "_yellowCards", 0);
        int redCards = sp.getInt(matchId + "_" + player.getPlayerID() + "_redCards", 0);
        String savedPosition = sp.getString(matchId + "_" + player.getPlayerID() + "_position", "");

        // ✅ Set data to views
        holder.goalsCount.setText(String.valueOf(goals));
        holder.yellowCardsCount.setText(String.valueOf(yellowCards));
        holder.redCardsCount.setText(String.valueOf(redCards));
        holder.positionAutoComplete.setText(savedPosition);

        // ✅ Set up AutoCompleteTextView adapter for positions
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, positionList);
        holder.positionAutoComplete.setAdapter(adapter);

        // Increment/Decrement buttons
        holder.goalsIncrement.setOnClickListener(v -> {
            int current = Integer.parseInt(holder.goalsCount.getText().toString());
            holder.goalsCount.setText(String.valueOf(current + 1));
        });
        holder.goalsDecrement.setOnClickListener(v -> {
            int current = Integer.parseInt(holder.goalsCount.getText().toString());
            if (current > 0) holder.goalsCount.setText(String.valueOf(current - 1));
        });

        holder.yellowCardsIncrement.setOnClickListener(v -> {
            int current = Integer.parseInt(holder.yellowCardsCount.getText().toString());
            holder.yellowCardsCount.setText(String.valueOf(current + 1));
        });
        holder.yellowCardsDecrement.setOnClickListener(v -> {
            int current = Integer.parseInt(holder.yellowCardsCount.getText().toString());
            if (current > 0) holder.yellowCardsCount.setText(String.valueOf(current - 1));
        });

        holder.redCardsIncrement.setOnClickListener(v -> {
            int current = Integer.parseInt(holder.redCardsCount.getText().toString());
            holder.redCardsCount.setText(String.valueOf(current + 1));
        });
        holder.redCardsDecrement.setOnClickListener(v -> {
            int current = Integer.parseInt(holder.redCardsCount.getText().toString());
            if (current > 0) holder.redCardsCount.setText(String.valueOf(current - 1));
        });

        // ✅ Save button logic
        holder.saveButton.setOnClickListener(v -> {
            int updatedGoals = Integer.parseInt(holder.goalsCount.getText().toString());
            int updatedYellow = Integer.parseInt(holder.yellowCardsCount.getText().toString());
            int updatedRed = Integer.parseInt(holder.redCardsCount.getText().toString());
            String updatedPosition = holder.positionAutoComplete.getText().toString();

            // Save to SharedPreferences
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt(matchId + "_" + player.getPlayerID() + "_goals", updatedGoals);
            editor.putInt(matchId + "_" + player.getPlayerID() + "_yellowCards", updatedYellow);
            editor.putInt(matchId + "_" + player.getPlayerID() + "_redCards", updatedRed);
            editor.putString(matchId + "_" + player.getPlayerID() + "_position", updatedPosition);
            editor.apply();

            // ✅ Save to database (your existing methods)
            updatePlayerStat(player.getPlayerID(), "goals", updatedGoals);
            updatePlayerStat(player.getPlayerID(), "yellowCards", updatedYellow);
            updatePlayerStat(player.getPlayerID(), "redCards", updatedRed);
            updatePlayerPosition(player.getPlayerID(), matchId, updatedPosition);

            // Optionally update match score logic
            updateMatchScores();

            Toast.makeText(context, "Player stats saved.", Toast.LENGTH_SHORT).show();
        });

        // Optional: remove logic if needed
        holder.removeIcon.setOnClickListener(v -> {
            // Same logic as save before removing
            int updatedGoals = Integer.parseInt(holder.goalsCount.getText().toString());
            int updatedYellow = Integer.parseInt(holder.yellowCardsCount.getText().toString());
            int updatedRed = Integer.parseInt(holder.redCardsCount.getText().toString());
            String updatedPosition = holder.positionAutoComplete.getText().toString();

            SharedPreferences.Editor editor = sp.edit();
            editor.putInt(matchId + "_" + player.getPlayerID() + "_goals", updatedGoals);
            editor.putInt(matchId + "_" + player.getPlayerID() + "_yellowCards", updatedYellow);
            editor.putInt(matchId + "_" + player.getPlayerID() + "_redCards", updatedRed);
            editor.putString(matchId + "_" + player.getPlayerID() + "_position", updatedPosition);
            editor.apply();

            updatePlayerStat(player.getPlayerID(), "goals", updatedGoals);
            updatePlayerStat(player.getPlayerID(), "yellowCards", updatedYellow);
            updatePlayerStat(player.getPlayerID(), "redCards", updatedRed);
            updatePlayerPosition(player.getPlayerID(), matchId, updatedPosition);

            removePlayer(player); // Implement this
        });
    }


//    @Override
//    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
//        Player player = playerList.get(position);
//        holder.playerNameTextView.setText(player.getPlayerName());
//        // ✅ Set up the AutoCompleteTextView with the fetched positions
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, positionList);
//        holder.playerPositionAutoComplete.setAdapter(adapter);
//
//        // ✅ Set the selected position from SharedPreferences
//        SharedPreferences sharedPreferences = context.getSharedPreferences("PlayerStats", Context.MODE_PRIVATE);
//        String savedPosition = sharedPreferences.getString(matchId + "_" + player.getPlayerID() + "_position", "");
//        holder.playerPositionAutoComplete.setText(savedPosition);
//
//        // Use SharedPreferences keys scoped by matchId
//        //SharedPreferences sharedPreferences = context.getSharedPreferences("PlayerStats", Context.MODE_PRIVATE);
//        int goals = sharedPreferences.getInt(matchId + "_" + player.getPlayerID() + "_goals", 0);
//        int yellowCards = sharedPreferences.getInt(matchId + "_" + player.getPlayerID() + "_yellowCards", 0);
//        int redCards = sharedPreferences.getInt(matchId + "_" + player.getPlayerID() + "_redCards", 0);
//        String playerPosition = sharedPreferences.getString(matchId + "_" + player.getPlayerID() + "_position", "");
//
//        // Set loaded values into EditText fields
//        holder.goalsEditText.setText(String.valueOf(goals));
//        holder.yellowCardsEditText.setText(String.valueOf(yellowCards));
//        holder.redCardsEditText.setText(String.valueOf(redCards));
//        //holder.playerPositionEditText.setText(playerPosition);
//
//        // Save stats only when save button is clicked
//        holder.saveButton.setOnClickListener(v -> {
//            // Retrieve the current values from EditText fields
//            int updatedGoals = Integer.parseInt(holder.goalsEditText.getText().toString());
//            int updatedYellowCards = Integer.parseInt(holder.yellowCardsEditText.getText().toString());
//            int updatedRedCards = Integer.parseInt(holder.redCardsEditText.getText().toString());
//
//            // ✅ Retrieve the selected position from AutoCompleteTextView
//            String updatedPosition = holder.playerPositionAutoComplete.getText().toString();
//
//            // Save stats to SharedPreferences with matchId
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putInt(matchId + "_" + player.getPlayerID() + "_goals", updatedGoals);
//            editor.putInt(matchId + "_" + player.getPlayerID() + "_yellowCards", updatedYellowCards);
//            editor.putInt(matchId + "_" + player.getPlayerID() + "_redCards", updatedRedCards);
//
//            // ✅ Save the updated position
//            editor.putString(matchId + "_" + player.getPlayerID() + "_position", updatedPosition);
//            editor.apply();
//
//            // Optionally show confirmation
//            Toast.makeText(context, "Player stats saved locally for this match.", Toast.LENGTH_SHORT).show();
//
//            // Update player stats in the database
//            updatePlayerStat(player.getPlayerID(), "goals", updatedGoals);
//            updatePlayerStat(player.getPlayerID(), "yellowCards", updatedYellowCards);
//            updatePlayerStat(player.getPlayerID(), "redCards", updatedRedCards);
//
//            // ✅ Update player position in the database
//            updatePlayerPosition(player.getPlayerID(), matchId, updatedPosition);
//
//            // Update match scores after all player stats are updated
//            updateMatchScores();
//        });
//
//        holder.removeIcon.setOnClickListener(view -> {
//            // Automatically save the current stats before removing the player
//            int updatedGoals = Integer.parseInt(holder.goalsEditText.getText().toString());
//            int updatedYellowCards = Integer.parseInt(holder.yellowCardsEditText.getText().toString());
//            int updatedRedCards = Integer.parseInt(holder.redCardsEditText.getText().toString());
//
//            // ✅ Retrieve the selected position from AutoCompleteTextView
//            String updatedPosition = holder.playerPositionAutoComplete.getText().toString();
//
//            // Save stats to SharedPreferences with matchId
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putInt(matchId + "_" + player.getPlayerID() + "_goals", updatedGoals);
//            editor.putInt(matchId + "_" + player.getPlayerID() + "_yellowCards", updatedYellowCards);
//            editor.putInt(matchId + "_" + player.getPlayerID() + "_redCards", updatedRedCards);
//
//            // ✅ Save the updated position
//            editor.putString(matchId + "_" + player.getPlayerID() + "_position", updatedPosition);
//            editor.apply();
//
//            // Update stats in the database
//            updatePlayerStat(player.getPlayerID(), "goals", updatedGoals);
//            updatePlayerStat(player.getPlayerID(), "yellowCards", updatedYellowCards);
//            updatePlayerStat(player.getPlayerID(), "redCards", updatedRedCards);
//
//            // ✅ Update player position in the database
//            updatePlayerPosition(player.getPlayerID(), matchId, updatedPosition);
//
//            // Then remove the player
//            removePlayer(player);
//        });
//
//
//    }



    @Override
    public int getItemCount() {
        return playerList.size();
    }

    public void addPlayers(List<Player> newPlayers) {
        int initialSize = playerList.size(); // Get the current size of the list
        playerList.addAll(newPlayers); // Add the new players
        notifyItemRangeInserted(initialSize, newPlayers.size()); // Notify the adapter
    }

    public void removePlayer(Player player) {
        int position = playerList.indexOf(player);
        if (position != -1) {
            if (position != -1) {
                // Reset the player's stats to zero before removal
                resetPlayerStats(player.getPlayerID());

                // Update match scores after resetting the stats
                updateMatchScores();

                // Then proceed to remove the player
                playerList.remove(position);
                notifyItemRemoved(position); // Notify about the item removal

                // Log and call the API to remove the player from the match
                Log.d("PlayerDetailsAdapter", "Removing player ID: " + player.getPlayerID() + " from match ID: " + matchId);
                removePlayerFromMatch(player.getPlayerID());
            }
        }
    }

    public void updatePlayerPosition(int playerId,int matchId, String position) {
        Call<Void> call = apiService.updatePlayerPosition(playerId,matchId, position);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("PlayerDetailsAdapter", "Player position updated successfully.");
                    Toast.makeText(context, "Player position updated.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("PlayerDetailsAdapter", "Failed to update player position. Code: " + response.code());
                    Toast.makeText(context, "Failed to update player position.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("PlayerDetailsAdapter", "Network error: " + t.getMessage());
                Toast.makeText(context, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void removePlayerFromMatch(int playerId) {
        // Call the API to remove the player from the match
        Call<Void> call = apiService.removePlayerFromMatch(playerId, matchId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("PlayerDetailsAdapter", "Player removed from match.");
                    Toast.makeText(context, "Player removed from match.", Toast.LENGTH_SHORT).show();

                    // Update match scores again after the player is fully removed
                    updateMatchScores();
                } else {
                    Log.e("PlayerDetailsAdapter", "Failed to remove player from match: " + response.message());
                    Toast.makeText(context, "Failed to remove player from match.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("PlayerDetailsAdapter", "Network error: " + t.getMessage());
                Toast.makeText(context, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void resetPlayerStats(int playerId) {
        updatePlayerStat(playerId, "goals", 0);
        updatePlayerStat(playerId, "yellowCards", 0);
        updatePlayerStat(playerId, "redCards", 0);
    }

    public void updatePlayerStat(int playerId, String statType, int value) {
        if (value < 0) {
            Log.e("PlayerDetailsAdapter", "Invalid stat value: " + value);
            return;
        }

        // Include matchId in the API call
        Call<Void> call = apiService.updatePlayerStat(playerId, statType, matchId, value);

        Log.d("PlayerDetailsAdapter", "Updating player stat: playerId=" + playerId + ", matchId=" + matchId + ", statType=" + statType + ", value=" + value);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("PlayerDetailsAdapter", "Player stats updated successfully for matchId=" + matchId);
                    Toast.makeText(context, "Player stats updated.", Toast.LENGTH_SHORT).show();
                    updateMatchScores();
                } else {
                    Log.e("PlayerDetailsAdapter", "Failed to update player stats. Code: " + response.code() + " - Message: " + response.message());
                    Toast.makeText(context, "Failed to update player stats. Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("PlayerDetailsAdapter", "Network error: " + t.getMessage());
                Toast.makeText(context, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Method to call the API for score update and notify listener
    public void updateMatchScores() {
        Call<Void> call = apiService.updateScoresFromPlayers(matchId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("PlayerDetailsAdapter", "Match scores updated successfully.");
                    Toast.makeText(context, "Match scores updated.", Toast.LENGTH_SHORT).show();

                    // Immediately update the UI in MainActivity
                    if (context instanceof MainActivity) {
                        ((MainActivity) context).refreshMatchList(); // Custom method in MainActivity
                    }
                } else {
                    Log.e("PlayerDetailsAdapter", "Failed to update match scores. Code: " + response.code() + " - Message: " + response.message());
                    Toast.makeText(context, "Failed to update match scores. Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("PlayerDetailsAdapter", "Network error: " + t.getMessage());
                Toast.makeText(context, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }



    public static class PlayerViewHolder extends RecyclerView.ViewHolder {
        TextView playerNameTextView, removePlayerText;
        ImageView removeIcon;
        AutoCompleteTextView positionAutoComplete;
        Button saveButton;

        // Goals
        View goalsCounter;
        TextView goalsLabel;
        ImageView goalsIcon;
        ImageButton goalsIncrement, goalsDecrement;
        TextView goalsCount;

        // Yellow Cards
        View yellowCardsCounter;
        TextView yellowCardsLabel;
        ImageView yellowCardsIcon;
        ImageButton yellowCardsIncrement, yellowCardsDecrement;
        TextView yellowCardsCount;

        // Red Cards
        View redCardsCounter;
        TextView redCardsLabel;
        ImageView redCardsIcon;
        ImageButton redCardsIncrement, redCardsDecrement;
        TextView redCardsCount;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);

            playerNameTextView = itemView.findViewById(R.id.player_name);
            positionAutoComplete = itemView.findViewById(R.id.position_search);
            saveButton = itemView.findViewById(R.id.save_button);
            removeIcon = itemView.findViewById(R.id.removeIcon);
            removePlayerText = itemView.findViewById(R.id.removePlayerText); // if used

            // Goals
            goalsCounter = itemView.findViewById(R.id.goals_counter);
            goalsLabel = goalsCounter.findViewById(R.id.stat_label);
            goalsIcon = goalsCounter.findViewById(R.id.stat_icon);
            goalsIncrement = goalsCounter.findViewById(R.id.stat_increase);
            goalsDecrement = goalsCounter.findViewById(R.id.stat_decrease);
            goalsCount = goalsCounter.findViewById(R.id.stat_value);

            // Yellow Cards
            yellowCardsCounter = itemView.findViewById(R.id.yellow_cards_counter);
            yellowCardsLabel = yellowCardsCounter.findViewById(R.id.stat_label);
            yellowCardsIcon = yellowCardsCounter.findViewById(R.id.stat_icon);
            yellowCardsIncrement = yellowCardsCounter.findViewById(R.id.stat_increase);
            yellowCardsDecrement = yellowCardsCounter.findViewById(R.id.stat_decrease);
            yellowCardsCount = yellowCardsCounter.findViewById(R.id.stat_value);

            // Red Cards
            redCardsCounter = itemView.findViewById(R.id.red_cards_counter);
            redCardsLabel = redCardsCounter.findViewById(R.id.stat_label);
            redCardsIcon = redCardsCounter.findViewById(R.id.stat_icon);
            redCardsIncrement = redCardsCounter.findViewById(R.id.stat_increase);
            redCardsDecrement = redCardsCounter.findViewById(R.id.stat_decrease);
            redCardsCount = redCardsCounter.findViewById(R.id.stat_value);
        }
    }

}
