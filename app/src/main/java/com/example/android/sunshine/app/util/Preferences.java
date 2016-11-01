package com.example.android.sunshine.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.android.sunshine.app.R;

/**
 * Created by justinmitchell on 2/11/2016.
 */

public class Preferences {

    private static final String SP_DEFAULT = "default";
    private static final String SP_COUNT = "countpref";

    public static String getSyncFrequency(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SP_DEFAULT, Context.MODE_PRIVATE);
        return prefs.getString(context.getString(R.string.pref_sync_frequency_key),
                context.getString(R.string.pref_sync_frequency_default));
    }

    public static String getPreferredLocation(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SP_DEFAULT, Context.MODE_PRIVATE);
        return prefs.getString(context.getString(R.string.pref_location_key),
                context.getString(R.string.pref_location_default));
    }

    public static String getUnitType(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SP_DEFAULT, Context.MODE_PRIVATE);
        return prefs.getString(context.getString(R.string.pref_units_key),
                context.getString(R.string.pref_units_metric));
    }

    public static long getTimesRun(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SP_COUNT, Context.MODE_PRIVATE);
        long def = Long.parseLong(context.getString(R.string.pref_sync_times_run_default));
        long timesRun = prefs.getLong(context.getString(R.string.pref_sync_times_run_key), def);

        return timesRun;
    }

    public static long getLastSyncTime(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SP_COUNT, Context.MODE_PRIVATE);
        long def = Long.parseLong(context.getString(R.string.pref_last_sync_default));
        long lastSync = prefs.getLong(context.getString(R.string.pref_last_sync_key), def);

        return lastSync;
    }

    public static void incrementTimesRun(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SP_COUNT, Context.MODE_PRIVATE);
        SharedPreferences.Editor spe = prefs.edit();

        long timesRun = getTimesRun(context);
        timesRun++;
        spe.putLong(context.getString(R.string.pref_sync_times_run_key), timesRun);
        spe.commit();
    }

    public static void setLastSyncTimeNow(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SP_COUNT, Context.MODE_PRIVATE);
        SharedPreferences.Editor spe = prefs.edit();
        spe.putLong(context.getString(R.string.pref_last_sync_key), System.currentTimeMillis());
        spe.commit();
    }

    public static long getLastUsedLocation(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SP_DEFAULT, Context.MODE_PRIVATE);
        long def = Long.parseLong(context.getString(R.string.pref_last_used_location_default));
        long lastLocation = prefs.getLong(context.getString(R.string.pref_last_used_location_key), def);

        return lastLocation;
    }

    public static void setLastUsedLocation(Context context, long locationId) {
        SharedPreferences prefs = context.getSharedPreferences(SP_DEFAULT, Context.MODE_PRIVATE);
        SharedPreferences.Editor spe = prefs.edit();
        spe.putLong(context.getString(R.string.pref_last_sync_key), System.currentTimeMillis());
        spe.commit();
    }

    public static boolean userDisplayNotifications(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SP_DEFAULT, Context.MODE_PRIVATE);
        String displayNotificationsKey = context.getString(R.string.pref_enable_notifications_key);

        boolean displayNotifications = prefs.getBoolean(displayNotificationsKey,
                Boolean.parseBoolean(context.getString(R.string.pref_enable_notifications_default)));

        return displayNotifications == true;
    }

    public static boolean usePreferredLocation(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SP_DEFAULT, Context.MODE_PRIVATE);
        String usePreferredKey = context.getString(R.string.pref_enable_gps_location_key);

        boolean usePreferred = prefs.getBoolean(usePreferredKey,
                Boolean.parseBoolean(context.getString(R.string.pref_enable_gps_location_default)));

        return !usePreferred;
    }

}