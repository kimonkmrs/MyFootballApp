package com.example.foorballapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        Call<Void> call = apiService.removePlayerFromMatch(playerId, matchId); // playerId as Path, matchId as Query

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("PlayerDetailsAdapter", "Player removed from match.");
                    Toast.makeText(context, "Player removed from match.", Toast.LENGTH_SHORT).show();
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


    public static class PlayerViewHolder extends RecyclerView.ViewHolder {
        TextView playerNameTextView;
        ImageView removeIcon; // Add this for the remove icon

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            playerNameTextView = itemView.findViewById(R.id.playerName);
            removeIcon = itemView.findViewById(R.id.removePlayerIcon); // Initialize the remove icon
        }
    }
}
