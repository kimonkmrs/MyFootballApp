package com.example.foorballapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder> {

    private List<Match> matchList;

    public MatchAdapter(List<Match> matchList) {
        this.matchList = matchList;
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match, parent, false);
        return new MatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        Match match = matchList.get(position);
        holder.matchId.setText(String.valueOf(match.getMatchID()));
        holder.team1Name.setText(match.getTeam1Name());
        holder.team2Name.setText(match.getTeam2Name());
        holder.team1Score.setText(String.valueOf(match.getScoreTeam1()));
        holder.team2Score.setText(String.valueOf(match.getScoreTeam2()));
        holder.matchTime.setText(match.getMatchDate());
    }

    @Override
    public int getItemCount() {
        return matchList.size();
    }

    public void setMatchList(List<Match> newMatchList) {
        this.matchList.clear();
        this.matchList.addAll(newMatchList);
        notifyDataSetChanged();
    }

    public static class MatchViewHolder extends RecyclerView.ViewHolder {
        TextView team1Name, team2Name, team1Score, team2Score, matchTime,matchId;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            matchId=itemView.findViewById(R.id.matchId);
            team1Name = itemView.findViewById(R.id.team1Name);
            team2Name = itemView.findViewById(R.id.team2Name);
            team1Score = itemView.findViewById(R.id.team1Score);
            team2Score = itemView.findViewById(R.id.team2Score);
            matchTime = itemView.findViewById(R.id.matchTime);
        }
    }
}
