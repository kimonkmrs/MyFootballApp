package com.myapp.minifootballstats;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MatchesPerTeamAdapter extends RecyclerView.Adapter<MatchesPerTeamAdapter.MatchViewHolder> {
    private List<MatchesPerTeam> matchList;
    private SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
    private SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

    public MatchesPerTeamAdapter(List<MatchesPerTeam> matchList) {
        this.matchList = matchList;
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.match_item_per_team, parent, false);
        return new MatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        MatchesPerTeam match = matchList.get(position);



        String[] teams = match.getGame().split("-");
        if (teams.length == 2) {
            holder.team1TextView.setText(teams[0].trim());
            holder.team2TextView.setText(teams[1].trim());
        } else {
            holder.team1TextView.setText("Team A");
            holder.team2TextView.setText("Team B");
        }
        holder.resultTextView.setText("Result: " + match.getResult());

        // Format and display the date
        String formattedDate = formatMatchDate(match.getMatchDate());
        holder.dateTextView.setText("Date: " + formattedDate);
    }

    private String formatMatchDate(String matchDate) {
        try {
            Date date = inputFormat.parse(matchDate);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return matchDate; // Return original if parsing fails
        }
    }

    @Override
    public int getItemCount() {
        return matchList.size();
    }

    public static class MatchViewHolder extends RecyclerView.ViewHolder {
        TextView team1TextView, team2TextView, resultTextView, dateTextView;


        public MatchViewHolder(View itemView) {
            super(itemView);
            team1TextView = itemView.findViewById(R.id.team1TextView);
            team2TextView = itemView.findViewById(R.id.team2TextView);
            resultTextView = itemView.findViewById(R.id.resultTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }
}
