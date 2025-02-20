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
        holder.gameTextView.setText(match.getGame());
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
        TextView gameTextView, resultTextView, dateTextView;

        public MatchViewHolder(View itemView) {
            super(itemView);
            gameTextView = itemView.findViewById(R.id.gameTextView);
            resultTextView = itemView.findViewById(R.id.resultTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }
}
