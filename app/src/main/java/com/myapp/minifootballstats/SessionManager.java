package com.myapp.minifootballstats;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class SessionManager {

    public static void logout(Context context) {
        // Clear the SharedPreferences
        SharedPreferences preferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear(); // Clear all stored data
        editor.apply();

        // Redirect to the login screen
        Intent intent = new Intent(context, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

        // Optional: If calling from an activity, finish it
        if (context instanceof PlayerStatsActivity) {
            ((PlayerStatsActivity) context).finish();
        } else if (context instanceof MainActivity) {
            ((MainActivity) context).finish();
        } else if (context instanceof StandingsActivity) {
            ((StandingsActivity) context).finish();
        } else if (context instanceof PlayerListActivity) {
            ((PlayerListActivity) context).finish();
        }
    }
}
