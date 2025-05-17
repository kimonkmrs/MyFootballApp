package com.myapp.minifootballstats;

import android.content.Context;
import android.content.SharedPreferences;

public class TimerUtils {
    private static final String PREFS_NAME = "GameTimers";

    private static String key(int matchId, String type) {
        return "match_" + matchId + "_" + type;
    }

    public static void saveStartTime(Context context, int matchId, long startTime) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putLong(key(matchId, "start_time"), startTime).apply();
    }

    public static long getStartTime(Context context, int matchId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getLong(key(matchId, "start_time"), 0);
    }

    public static void saveElapsedTime(Context context, int matchId, long elapsed) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putLong(key(matchId, "elapsed_time"), elapsed).apply();
    }

    public static long getElapsedTime(Context context, int matchId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getLong(key(matchId, "elapsed_time"), 0);
    }

    public static void setTimerPaused(Context context, int matchId, boolean paused) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(key(matchId, "paused"), paused).apply();
    }

    public static boolean isTimerPaused(Context context, int matchId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(key(matchId, "paused"), false);
    }

    public static void saveExtraTime(Context context, int matchId, long extraTime) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putLong(key(matchId, "extra_time"), extraTime).apply();
    }

    public static long getExtraTime(Context context, int matchId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getLong(key(matchId, "extra_time"), 0);
    }

    public static void clearTimerData(Context context, int matchId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .remove(key(matchId, "start_time"))
                .remove(key(matchId, "elapsed_time"))
                .remove(key(matchId, "paused"))
                .remove(key(matchId, "extra_time"))
                .apply();
    }
    // Check if the timer is running based on whether startTime is saved and timerPaused is false
    public static boolean isTimerRunning(Context context, int matchId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        long startTime = prefs.getLong(key(matchId, "start_time"), 0);
        boolean isPaused = prefs.getBoolean(key(matchId, "paused"), false);

        return startTime != 0 && !isPaused;
    }



    // Also, add this if you don't have it:

}
