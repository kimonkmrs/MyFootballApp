package com.example.foorballapp;

import android.content.Context;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foorballapp.api.ApiService;

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

        // Set the click listener for the remove icon
        holder.removeIcon.setOnClickListener(v -> {
            removePlayer(player);
        });
        // Add TextWatchers for goals, yellow cards, and red cards
        // Add TextWatchers for goals, yellow cards, and red cards
        holder.goalsEditText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                String goalsText = s.toString();
                int goals = goalsText.isEmpty() ? 0 : Integer.parseInt(goalsText);
                updatePlayerStat(player.getPlayerID(), "goals", goals);
            }
        });

        holder.yellowCardsEditText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String yellowCardsText = s.toString();
                int yellowCards = yellowCardsText.isEmpty() ? 0 : Integer.parseInt(yellowCardsText);
                updatePlayerStat(player.getPlayerID(), "yellowCards", yellowCards);
            }
        });

        holder.redCardsEditText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String redCardsText = s.toString();
                int redCards = redCardsText.isEmpty() ? 0 : Integer.parseInt(redCardsText);
                updatePlayerStat(player.getPlayerID(), "redCards", redCards);
            }
        });
    }

    @Override
    public int getItemCount() {
        return playerList.size();
    }

    public void addPlayers(List<Player> newPlayers) {
        playerList.addAll(newPlayers);
        notifyDataSetChanged();
    }

    public void removePlayer(Player player) {
        int position = playerList.indexOf(player);
        if (position != -1) {
            playerList.remove(position);
            notifyItemRemoved(position); // Notify adapter about data change at a specific position
        }
        if (matchId != -1) {
            Log.d("PlayerDetailsAdapter", "Removing player ID: " + player.getPlayerID() + " from match ID: " + matchId);
            removePlayerFromMatch(player.getPlayerID());
        }
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

        Call<Void> call = apiService.updatePlayerStat(playerId, statType, value);

        Log.d("PlayerDetailsAdapter", "Updating player stat: playerId=" + playerId + ", statType=" + statType + ", value=" + value);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("PlayerDetailsAdapter", "Player stats updated successfully.");
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
        TextView playerNameTextView;
        ImageView removeIcon; // Add this for the remove icon
        EditText goalsEditText;
        EditText yellowCardsEditText;
        EditText redCardsEditText;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            playerNameTextView = itemView.findViewById(R.id.playerName);
            removeIcon = itemView.findViewById(R.id.removePlayerIcon); // Initialize the remove icon
            goalsEditText = itemView.findViewById(R.id.goals);
            yellowCardsEditText = itemView.findViewById(R.id.yellows);
            redCardsEditText = itemView.findViewById(R.id.reds);
        }
    }
}
