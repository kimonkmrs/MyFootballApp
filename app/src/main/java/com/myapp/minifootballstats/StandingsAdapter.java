package com.myapp.minifootballstats;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StandingsAdapter extends RecyclerView.Adapter<StandingsAdapter.ViewHolder> {
    private List<Standing> standingsList;
    private OnItemClickListener onItemClickListener;
    // Define an interface for item click handling
    public interface OnItemClickListener {
        void onItemClick(Standing standing);
    }

    // Constructor to initialize standings list
    public StandingsAdapter(List<Standing> standingsList, OnItemClickListener onItemClickListener) {
        this.standingsList = standingsList;
        this.onItemClickListener = onItemClickListener;
    }

    // Method to update the standings dynamically
    public void updateStandings(List<Standing> newStandings) {
        standingsList.clear(); // Clear the old data
        standingsList.addAll(newStandings); // Add the new data
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item_standing layout for each item in the list
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_standing, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Bind the data to each view for each item in the list
        Standing standing = standingsList.get(position);
//        holder.positionTextView.setText(String.valueOf(standing.getPosition()));
        String displayName = standing.getPosition() + ". " + standing.getTeamName();
        holder.teamNameTextView.setText(displayName);
//        holder.teamNameTextView.setText(standing.getTeamName());
        holder.mpTextView.setText(String.valueOf(standing.getMatchesPlayed()));
        holder.winsTextView.setText(String.valueOf(standing.getWins()));
        holder.drawsTextView.setText(String.valueOf(standing.getDraws()));
        holder.lossesTextView.setText(String.valueOf(standing.getLosses()));
        holder.gfTextView.setText(String.valueOf(standing.getGoalsFor()));
        holder.gaTextView.setText(String.valueOf(standing.getGoalsAgainst()));
        holder.gdTextView.setText(String.valueOf(standing.getGoalDifference()));
        holder.pointsTextView.setText(String.valueOf(standing.getPoints()));
        // Set a click listener for the entire item view
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(standing));
    }

    @Override
    public int getItemCount() {
        return standingsList.size(); // Return the size of the standings list
    }

    // ViewHolder class to hold references to each view in the item layout
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView  teamNameTextView, mpTextView, winsTextView,
                drawsTextView, lossesTextView, gfTextView, gaTextView, gdTextView, pointsTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize the TextViews by finding them by ID from the item layout
//            positionTextView = itemView.findViewById(R.id.positionTextView);
            teamNameTextView = itemView.findViewById(R.id.teamNameTextView);
            mpTextView = itemView.findViewById(R.id.mpTextView);
            winsTextView = itemView.findViewById(R.id.winsTextView);
            drawsTextView = itemView.findViewById(R.id.drawsTextView);
            lossesTextView = itemView.findViewById(R.id.lossesTextView);
            gfTextView = itemView.findViewById(R.id.gfTextView);
            gaTextView = itemView.findViewById(R.id.gaTextView);
            gdTextView = itemView.findViewById(R.id.gdTextView);
            pointsTextView = itemView.findViewById(R.id.pointsTextView);
        }
    }
}
