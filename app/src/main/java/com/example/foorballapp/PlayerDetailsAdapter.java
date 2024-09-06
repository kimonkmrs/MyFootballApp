package com.example.foorballapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foorballapp.api.ApiService;

import org.json.JSONException;
import org.json.JSONObject;

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
        holder.itemView.setOnClickListener(v -> removePlayer(player));
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
        playerList.remove(player);
        notifyDataSetChanged(); // Notify adapter about data change
        if (matchId != -1) {
            removePlayerMatchId(player.getPlayerID());
        }
    }


    private void removePlayerMatchId(int playerId) {
        // Create PlayerRemoveRequest object to pass in the API call
        PlayerRemoveRequest removeRequest = new PlayerRemoveRequest(matchId);

        Call<Void> call = apiService.removeMatchFromPlayer(playerId, removeRequest);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("PlayerDetailsAdapter", "Player removed from match.");
                } else {
                    Log.e("PlayerDetailsAdapter", "Failed to remove player from match: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("PlayerDetailsAdapter", "Network error: " + t.getMessage());
            }
        });
    }


    public static class PlayerViewHolder extends RecyclerView.ViewHolder {
        TextView playerNameTextView;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            playerNameTextView = itemView.findViewById(R.id.playerName);
        }
    }
}

