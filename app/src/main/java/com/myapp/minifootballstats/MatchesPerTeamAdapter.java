package com.myapp.minifootballstats;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myapp.minifootballstats.MatchesPerTeam;
import com.myapp.minifootballstats.R;

import java.util.List;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MatchesPerTeamAdapter extends RecyclerView.Adapter<MatchesPerTeamAdapter.MatchViewHolder> {
    private List<MatchesPerTeam> matchList;



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
        holder.dateTextView.setText("Date: " + match.getMatchDate());
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
