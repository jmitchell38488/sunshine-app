package com.example.android.sunshine.app.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.format.Time;
import android.util.Log;

import com.example.android.sunshine.app.BuildConfig;
import com.example.android.sunshine.app.MainActivity;
import com.example.android.sunshine.app.R;
import com.example.android.sunshine.app.data.WeatherContract;
import com.example.android.sunshine.app.data.model.CurrentConditionsModel;
import com.example.android.sunshine.app.data.model.LocationModel;
import com.example.android.sunshine.app.data.model.WeatherModel;
import com.example.android.sunshine.app.util.Utility;
import com.example.android.sunshine.app.util.WeatherDataParser;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SunshineSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = SunshineSyncAdapter.class.getSimpleName();

    private final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily";
    private final String CURRENT_CONDITIONS_BASE_URL = "http://api.openweathermap.org/data/2.5/weather";
    private final String QUERY_LAT = "lat";
    private final String QUERY_LON = "lon";
    private final String QUERY_PARAM = "q";
    private final String FORMAT_PARAM = "mode";
    private final String UNITS_PARAM = "units";
    private final String DAYS_PARAM = "cnt";
    private final String API_KEY = "APPID";

    public static final double SYNC_FLEXTIME = 0.333;
    private static final int WEATHER_NOTIFICATION_ID = 3004;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LOCATION_STATUS_OK,LOCATION_STATUS_SERVER_DOWN,LOCATION_STATUS_SERVER_INVALID,LOCATION_STATUS_UNKNOWN})
    public @interface LocationStatus {}

    public static final int LOCATION_STATUS_OK = 0;
    public static final int LOCATION_STATUS_SERVER_DOWN = 1;
    public static final int LOCATION_STATUS_SERVER_INVALID = 2;
    public static final int LOCATION_STATUS_UNKNOWN = 3;

    public SunshineSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "onPerformSync");
        long locationId = syncForecastData();

        // Store the location id in the SharedPreferences for later use
        SharedPreferences prefs = getContext().getSharedPreferences("com.example.android.sunshine.app", Context.MODE_PRIVATE);
        prefs.edit().putLong(
                getContext().getString(R.string.pref_location_last_used_status_key),
                locationId
        ).apply();

        syncCurrentData(locationId);

        Log.d(LOG_TAG, "Triggering notifyWeather");
        notifyWeather(locationId);
    }

    private long syncForecastData() {
        long locationId = 0;

        try {
            String[] params = new String[] {
                    Utility.getPreferredLocation(getContext()),
                    "json",
                    Utility.getUnitType(getContext()),
                    "14"
            };

            String forecastJsonStr = fetchForecastJson(params);

            if (forecastJsonStr == null || forecastJsonStr.isEmpty()) {
                throw new JSONException("Could not parse JSON content, no response data");
            }

            WeatherDataParser weatherParser = new WeatherDataParser(forecastJsonStr);

            // Convert the records to objects
            String locationSetting = Utility.usePreferredLocation(getContext()) ? params[0] : "";
            WeatherModel[] weatherItems = weatherParser.convertWeatherData();
            LocationModel locationItem = weatherParser.convertLocationData(locationSetting);

            // Save location to data store
            locationId = addLocation(locationItem);

            // Update weather models with location id
            for (WeatherModel item : weatherItems) {
                item.setLocationId(locationId);
            }

            // Save weather items to the data store
            addWeatherItems(weatherItems);

            // Remove expired items from the data store
            removeOldWeatherItems();
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return locationId;
    }

    private void syncCurrentData(long locationId) {
        try {
            String[] params = new String[] {
                    Utility.getPreferredLocation(getContext()),
                    "json",
                    Utility.getUnitType(getContext())
            };

            String currentJsonStr = fetchCurrentConditionsJson(params);

            if (currentJsonStr == null || currentJsonStr.isEmpty()) {
                throw new JSONException("Could not parse JSON content, no response data");
            }

            WeatherDataParser weatherParser = new WeatherDataParser(currentJsonStr);

            // Convert the records to objects
            CurrentConditionsModel currentModel = weatherParser.convertCurrentConditionsData();
            currentModel.setLocationId(locationId);

            // Save current conditions to the data store
            addCurrentCondition(currentModel);

            // Remove expired items from the data store
            //removeOldCurrentConditionsItems(locationId);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private void removeOldWeatherItems() {
        // Initialize the date/time objects
        Time dayTime = new Time();
        dayTime.setToNow();

        // we start at the day returned by local time. Otherwise this is a mess.
        int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

        // now we work exclusively in UTC
        dayTime = new Time();

        getContext().getContentResolver().delete(WeatherContract.WeatherEntry.CONTENT_URI,
                WeatherContract.WeatherEntry.COLUMN_DATE + " <= ?",
                new String[] {Long.toString(dayTime.setJulianDay(julianStartDay-1))});
    }

    private void removeOldCurrentConditionsItems(long locationId) {
        // Initialize the date/time objects
        Time dayTime = new Time();
        dayTime.setToNow();

        // we start at the day returned by local time. Otherwise this is a mess.
        int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

        // now we work exclusively in UTC
        dayTime = new Time();

        getContext().getContentResolver().delete(WeatherContract.CurrentConditionsEntry.CONTENT_URI,
                WeatherContract.CurrentConditionsEntry.COLUMN_DATE + " <= ? AND " +
                WeatherContract.CurrentConditionsEntry.COLUMN_LOC_KEY + " = ?",
                new String[] {
                        Long.toString(dayTime.setJulianDay(julianStartDay-1)),
                        Long.toString(locationId)
                });
    }

    private void notifyWeather(long locationId) {
        Context context = getContext();
        //checking the last update and notify if it' the first of the day
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        // Don't notify if the user has disabled notifications
        if (!Utility.userDisplayNotifications(context)) {
            return;
        }

        String lastNotificationKey = context.getString(R.string.pref_last_notification);
        long lastSync = prefs.getLong(lastNotificationKey, 0);

        // Get the notification time with the sync frequency minus 1 minute so that it will always notify
        long weatherNotificationDetail = (Long.parseLong(Utility.getSyncFrequency(context)) * 3600) - 60;

        if (System.currentTimeMillis() - lastSync < weatherNotificationDetail) {
            return;
        }

        Uri weatherUri = WeatherContract.WeatherEntry.buildWeatherLocationIdWithDate(locationId, System.currentTimeMillis());

        // we'll query our contentProvider, as always
        Cursor cursor = context.getContentResolver().query(weatherUri, WeatherContract.FORECAST_COLUMNS, null, null, null);

        if (cursor.moveToFirst()) {
            WeatherModel weatherModel = new WeatherModel();
            LocationModel locationModel = new LocationModel();
            CurrentConditionsModel currentModel = null;

            weatherModel.loadFromCursor(cursor);
            locationModel.loadFromCursor(cursor);

            Uri currentUri = WeatherContract.CurrentConditionsEntry.buildCurrentConditionsUri(weatherModel.getLocationId());
            Cursor conditionsCursor = context.getContentResolver().query(
                    currentUri,
                    WeatherContract.CurrentConditionsEntry.FORECAST_COLUMNS,
                    WeatherContract.CurrentConditionsEntry.COLUMN_LOC_KEY + " = ?",
                    new String[] {
                            Long.toString(weatherModel.getLocationId())
                    },
                    WeatherContract.CurrentConditionsEntry.COLUMN_DATE + " DESC"
            );

            if (conditionsCursor != null && conditionsCursor.moveToFirst()) {
                currentModel = new CurrentConditionsModel();
                currentModel.loadFromCursor(conditionsCursor);
            }

            int iconId = Utility.getIconResourceForWeatherCondition((int) weatherModel.getWeatherId());
            String title = context.getString(R.string.app_name);

            // Define the text of the forecast.
            String contentText = "";
            if (currentModel == null) {
                contentText = String.format(
                        context.getString(R.string.format_notification),
                        weatherModel.getDescription(),
                        weatherModel.getFormattedMaxTemperature(context, weatherModel.isMetric(context)),
                        weatherModel.getFormattedMinTemperature(context, weatherModel.isMetric(context)));
            } else {
                contentText = String.format(
                        context.getString(R.string.format_notification_current),
                        locationModel.getCityName(),
                        currentModel.getFormattedCurrentTemperature(context, currentModel.isMetric(context)),
                        currentModel.getDescription(),
                        weatherModel.getDescription(),
                        weatherModel.getFormattedMaxTemperature(context, weatherModel.isMetric(context)),
                        weatherModel.getFormattedMinTemperature(context, weatherModel.isMetric(context)));
            }

            Log.d(LOG_TAG, "Notification (" + contentText + ")");

            // NotificationCompatBuilder is a very convenient way to build backward-compatible
            // notifications.  Just throw in some data.
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(getContext())
                            .setSmallIcon(iconId)
                            .setContentTitle(title)
                            .setContentText(contentText);

            // Make something interesting happen when the user clicks on the notification.
            // In this case, opening the app is sufficient.
            Intent resultIntent = new Intent(context, MainActivity.class);

            // The stack builder object will contain an artificial back stack for the
            // started Activity.
            // This ensures that navigating backward from the Activity leads out of
            // your application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);

            NotificationManager mNotificationManager =
                    (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            // WEATHER_NOTIFICATION_ID allows you to update the notification later on.
            mNotificationManager.notify(WEATHER_NOTIFICATION_ID, mBuilder.build());


            //refreshing last sync
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong(lastNotificationKey, System.currentTimeMillis());
            editor.commit();
        }
    }

    /**
     * Helper method to fetch the raw JSON data from the Open Weather API
     * @param params
     * @return
     */
    protected String fetchForecastJson(String... params) {
        URL url;

        // Will contain the raw JSON response as a string.
        String jsonStr = null;
        Location location = null;

        try {
            boolean usePreferredLocation = Utility.usePreferredLocation(getContext());
            location = Utility.getLastBestLocation(getContext());

            Uri uriBuilder;
            if (usePreferredLocation || location == null) {
                uriBuilder = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(FORMAT_PARAM, params[1])
                        .appendQueryParameter(UNITS_PARAM, params[2])
                        .appendQueryParameter(DAYS_PARAM, params[3])
                        .appendQueryParameter(API_KEY, BuildConfig.OPEN_WEATHER_MAP_API_KEY)
                        .build();
            } else {
                uriBuilder = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_LAT, "" + location.getLatitude())
                        .appendQueryParameter(QUERY_LON, "" + location.getLongitude())
                        .appendQueryParameter(FORMAT_PARAM, params[1])
                        .appendQueryParameter(UNITS_PARAM, params[2])
                        .appendQueryParameter(DAYS_PARAM, params[3])
                        .appendQueryParameter(API_KEY, BuildConfig.OPEN_WEATHER_MAP_API_KEY)
                        .build();
            }

            url = new URL(uriBuilder.toString());

            jsonStr = fetchJsonFromUrl(url);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Could not fetch JSON from URL", e);
        }

        return jsonStr;
    }/**
     * Helper method to fetch the raw JSON data from the Open Weather API
     * @param params
     * @return
     */
    protected String fetchCurrentConditionsJson(String... params) {
        URL url;

        // Will contain the raw JSON response as a string.
        String jsonStr = null;
        Location location = null;

        try {
            boolean usePreferredLocation = Utility.usePreferredLocation(getContext());
            location = Utility.getLastBestLocation(getContext());

            Uri uriBuilder;
            if (usePreferredLocation || location == null) {
                uriBuilder = Uri.parse(CURRENT_CONDITIONS_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(FORMAT_PARAM, params[1])
                        .appendQueryParameter(UNITS_PARAM, params[2])
                        .appendQueryParameter(API_KEY, BuildConfig.OPEN_WEATHER_MAP_API_KEY)
                        .build();
            } else {
                uriBuilder = Uri.parse(CURRENT_CONDITIONS_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_LAT, "" + location.getLatitude())
                        .appendQueryParameter(QUERY_LON, "" + location.getLongitude())
                        .appendQueryParameter(FORMAT_PARAM, params[1])
                        .appendQueryParameter(UNITS_PARAM, params[2])
                        .appendQueryParameter(API_KEY, BuildConfig.OPEN_WEATHER_MAP_API_KEY)
                        .build();
            }

            url = new URL(uriBuilder.toString());

            jsonStr = fetchJsonFromUrl(url);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Could not fetch JSON from URL", e);
        }

        return jsonStr;
    }

    private String fetchJsonFromUrl(URL url) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String jsonPackage = null;

        try {
            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                jsonPackage = null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            // Stream was empty.  No point in parsing.
            if (buffer.length() == 0) {
                return null;
            }

            jsonPackage = buffer.toString();

            Log.d(LOG_TAG, "JSON Output: " + jsonPackage);
        } catch (SecurityException e) {
            Log.e(LOG_TAG, "Error: " + e.getMessage(), e);
            jsonPackage = null;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error: " + e.getMessage(), e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            jsonPackage = null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        return jsonPackage;
    }

    public long addLocation(LocationModel locationModel) {
        long locationId =  this.addLocation(
                locationModel.getLocationSetting(),
                locationModel.getCityName(),
                locationModel.getCoordLat(),
                locationModel.getCoordLon()
        );

        locationModel.setId(locationId);

        return locationId;
    }

    public long addLocation(String locationSetting, String cityName, double lat, double lon) {
        long locationId;

        Cursor locationCursor = getContext().getContentResolver().query(
                WeatherContract.LocationEntry.CONTENT_URI,
                new String[] {WeatherContract.LocationEntry._ID},
                WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ?",
                new String[]{locationSetting},
                null
        );

        // Check if there's a result by attempting to load the first result
        if (locationCursor.moveToFirst()) {
            int locationIdIndex = locationCursor.getColumnIndex(WeatherContract.LocationEntry._ID);
            locationId = locationCursor.getLong(locationIdIndex);

            // No result, insert then return insert_id
        } else {
            // Create content values array
            ContentValues locationValues = new ContentValues();

            // Load the data
            locationValues.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, cityName);
            locationValues.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, locationSetting);
            locationValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT, lat);
            locationValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LON, lon);

            // Insert the data
            Uri insertedUri = getContext().getContentResolver().insert(
                    WeatherContract.LocationEntry.CONTENT_URI,
                    locationValues
            );

            // The resulting URI contains the ID for the row.  Extract the locationId from the Uri.
            locationId = ContentUris.parseId(insertedUri);
        }

        // Close connection
        locationCursor.close();

        // Return!
        return locationId;
    }

    public int addWeatherItems(WeatherModel[] weatherItems) throws IOException {
        if (weatherItems == null || weatherItems.length == 0) {
            throw new IOException("Invalid weather items array, no items or invalid array");
        }

        ContentValues[] values = new ContentValues[weatherItems.length];

        // Convert model objects to ContentValues for bulkInsert
        for (int i = 0; i < weatherItems.length; i++) {
            values[i] = weatherItems[i].toContentValues();
        }

        int insertedRows = 0;
        if (values.length > 0) {
            insertedRows = getContext().getContentResolver().bulkInsert(
                    WeatherContract.WeatherEntry.CONTENT_URI,
                    values
            );
        }

        return insertedRows;
    }

    public int addCurrentCondition(CurrentConditionsModel currentModel) throws IOException {
        if (currentModel == null) {
            throw new IOException("Invalid CurrentConditionsModel");
        }

        ContentValues values = currentModel.toContentValues();

        int insertedRows = 0;
        Uri returnUri = getContext().getContentResolver().insert(
                WeatherContract.CurrentConditionsEntry.CONTENT_URI,
                values
        );

        if (returnUri == null) {
            return 0;
        }

        return 1;
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }


    private static void onAccountCreated(Account newAccount, Context context) {
        // Get the sync interval in hours, make sure to multiply by seconds (3600: 1 hour)
        int syncInterval = Integer.parseInt(Utility.getSyncFrequency(context)) * 3600;
        int syncFlex = (int)((double)syncInterval * SYNC_FLEXTIME);

        /*
         * Since we've created an account
         */
        SunshineSyncAdapter.configurePeriodicSync(context, syncInterval, syncFlex);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

}