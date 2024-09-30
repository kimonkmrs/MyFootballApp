package com.example.foorballapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlayerStatsAdapter extends RecyclerView.Adapter<PlayerStatsAdapter.ViewHolder> {

    private List<PlayerStats> playerStatsList;

    public PlayerStatsAdapter(List<PlayerStats> playerStatsList) {
        this.playerStatsList = playerStatsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player_stat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlayerStats playerStat = playerStatsList.get(position);
        holder.playerNameTextView.setText(playerStat.getPlayerName());
        holder.positionTextView.setText(playerStat.getPosition());
        holder.teamNameTextView.setText(playerStat.getTeamName());
        holder.mpTextView.setText(String.valueOf(playerStat.getMatchesPlayed()));
        holder.winsTextView.setText(String.valueOf(playerStat.getWins()));
        holder.drawsTextView.setText(String.valueOf(playerStat.getDraws()));
        holder.lossesTextView.setText(String.valueOf(playerStat.getLosses()));
        holder.gfTextView.setText(String.valueOf(playerStat.getGoalsFor()));
        holder.gaTextView.setText(String.valueOf(playerStat.getGoalsAgainst()));

    }

    @Override
    public int getItemCount() {
        return playerStatsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView playerNameTextView, positionTextView, teamNameTextView, mpTextView;
        TextView winsTextView, drawsTextView, lossesTextView, gfTextView, gaTextView, gdTextView, pointsTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            playerNameTextView = itemView.findViewById(R.id.playerNameTextView);
            positionTextView = itemView.findViewById(R.id.positionTextView);
            teamNameTextView = itemView.findViewById(R.id.teamNameTextView);
            mpTextView = itemView.findViewById(R.id.mpTextView);
            winsTextView = itemView.findViewById(R.id.winsTextView);
            drawsTextView = itemView.findViewById(R.id.drawsTextView);
            lossesTextView = itemView.findViewById(R.id.lossesTextView);
            gfTextView = itemView.findViewById(R.id.gfTextView);
            gaTextView = itemView.findViewById(R.id.gaTextView);

        }
    }
}
