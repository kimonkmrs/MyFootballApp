package com.myapp.minifootballstats;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myapp.minifootballstats.api.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayerDetailsAdapter extends RecyclerView.Adapter<PlayerDetailsAdapter.PlayerViewHolder> {
    private List<Player> playerList;
    private int matchId;
    private ApiService apiService;
    private Context context;
    public interface ScoreUpdateListener {
        void onScoreUpdated();
    }

    public PlayerDetailsAdapter(Context context, List<Player> playerList, int matchId, ApiService apiService) {
        this.context = context;
        this.playerList = playerList;
        this.matchId = matchId;
        this.apiService = apiService;

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

        // Use SharedPreferences keys scoped by matchId
        SharedPreferences sharedPreferences = context.getSharedPreferences("PlayerStats", Context.MODE_PRIVATE);
        int goals = sharedPreferences.getInt(matchId + "_" + player.getPlayerID() + "_goals", 0);
        int yellowCards = sharedPreferences.getInt(matchId + "_" + player.getPlayerID() + "_yellowCards", 0);
        int redCards = sharedPreferences.getInt(matchId + "_" + player.getPlayerID() + "_redCards", 0);
        String playerPosition = sharedPreferences.getString(matchId + "_" + player.getPlayerID() + "_position", "");

        // Set loaded values into EditText fields
        holder.goalsEditText.setText(String.valueOf(goals));
        holder.yellowCardsEditText.setText(String.valueOf(yellowCards));
        holder.redCardsEditText.setText(String.valueOf(redCards));
        holder.playerPositionEditText.setText(playerPosition);

        // Save stats only when save button is clicked
        holder.saveButton.setOnClickListener(v -> {
            // Retrieve the current values from EditText fields
            int updatedGoals = Integer.parseInt(holder.goalsEditText.getText().toString());
            int updatedYellowCards = Integer.parseInt(holder.yellowCardsEditText.getText().toString());
            int updatedRedCards = Integer.parseInt(holder.redCardsEditText.getText().toString());
            String updatedPosition = holder.playerPositionEditText.getText().toString();

            // Save stats to SharedPreferences with matchId
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(matchId + "_" + player.getPlayerID() + "_goals", updatedGoals);
            editor.putInt(matchId + "_" + player.getPlayerID() + "_yellowCards", updatedYellowCards);
            editor.putInt(matchId + "_" + player.getPlayerID() + "_redCards", updatedRedCards);
            editor.putString(matchId + "_" + player.getPlayerID() + "_position", updatedPosition);
            editor.apply();

            // Optionally show confirmation
            Toast.makeText(context, "Player stats saved locally for this match.", Toast.LENGTH_SHORT).show();

            // Update player stats in the database
            updatePlayerStat(player.getPlayerID(), "goals", updatedGoals);
            updatePlayerStat(player.getPlayerID(), "yellowCards", updatedYellowCards);
            updatePlayerStat(player.getPlayerID(), "redCards", updatedRedCards);

            // Update player position in the database
            updatePlayerPosition(player.getPlayerID(),matchId, updatedPosition);

            // Update match scores after all player stats are updated
            updateMatchScores();
        });

        holder.removeIcon.setOnClickListener(view -> {
            removePlayer(player);
        });
    }



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
            playerList.remove(position);
            notifyItemRemoved(position); // Notify about the item removal
        }

        if (matchId != -1) {
            Log.d("PlayerDetailsAdapter", "Removing player ID: " + player.getPlayerID() + " from match ID: " + matchId);
            removePlayerFromMatch(player.getPlayerID());
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
        // First reset the player's stats to 0
        resetPlayerStats(playerId);
        Call<Void> call = apiService.removePlayerFromMatch(playerId, matchId); // playerId as Path, matchId as Query

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("PlayerDetailsAdapter", "Player removed from match.");
                    Toast.makeText(context, "Player removed from match.", Toast.LENGTH_SHORT).show();
                    //removePlayer(player);
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
        TextView playerNameTextView,removePlayerText;
        ImageView removeIcon; // Add this for the remove icon
        EditText goalsEditText;
        EditText yellowCardsEditText;
        EditText redCardsEditText;
        EditText playerPositionEditText;
        Button saveButton; // Add save button

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            playerNameTextView = itemView.findViewById(R.id.playerName);
            removePlayerText=itemView.findViewById(R.id.removePlayerText);
            removeIcon = itemView.findViewById(R.id.removePlayerIcon); // Initialize the remove icon
            goalsEditText = itemView.findViewById(R.id.goals);
            yellowCardsEditText = itemView.findViewById(R.id.yellows);
            redCardsEditText = itemView.findViewById(R.id.reds);
            playerPositionEditText = itemView.findViewById(R.id.playerPosition);
            saveButton = itemView.findViewById(R.id.saveButton); // Initialize the save button
        }
    }
}
