package com.example.android.sunshine.app.view;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.app.R;
import com.example.android.sunshine.app.data.WeatherContract;
import com.example.android.sunshine.app.data.model.CurrentConditionsModel;
import com.example.android.sunshine.app.data.model.LocationModel;
import com.example.android.sunshine.app.data.model.WeatherModel;
import com.example.android.sunshine.app.util.Preferences;
import com.example.android.sunshine.app.util.Utility;

import java.text.SimpleDateFormat;
import java.util.StringTokenizer;

/**
 * Created by justinmitchell on 30/10/2016.
 */

public class TodayViewHolder {

    private static String LOG_TAG = TodayViewHolder.class.getSimpleName();
    private long lastWeatherDateTime = 0;

    public final TextView syncView;
    public final TextView locationView;
    public final TextView dateView;
    public final ImageView iconView;
    public final TextView currentView;
    public final TextView highLowView;
    public final TextView weatherView;
    public final TextView humidityView;
    public final TextView pressureView;
    public final TextView windView;

    public TodayViewHolder(View view) {
        syncView = (TextView) view.findViewById(R.id.today_last_sync);
        dateView = (TextView) view.findViewById(R.id.today_date_textview);
        iconView = (ImageView) view.findViewById(R.id.today_icon);
        currentView = (TextView) view.findViewById(R.id.today_current_textview);
        highLowView = (TextView) view.findViewById(R.id.today_high_low_textview);
        weatherView = (TextView) view.findViewById(R.id.today_weather_textview);
        humidityView = (TextView) view.findViewById(R.id.today_humidity_value_textview);
        pressureView = (TextView) view.findViewById(R.id.today_pressure_value_textview);
        windView = (TextView) view.findViewById(R.id.today_wind_value_textview);

        if (view.findViewById(R.id.today_location_textview) != null) {
            locationView = (TextView) view.findViewById(R.id.today_location_textview);
        } else {
            locationView = null;
        }
    }

    public void reloadProperties(Cursor cursor, Context context) {
        if (!cursor.moveToFirst()) {
            return;
        }

        Log.d(LOG_TAG, "Redrawing FragmentToday.TodayViewHolder");

        WeatherModel weatherModel = new WeatherModel();
        weatherModel.loadFromCursor(cursor);

        LocationModel locationModel = new LocationModel();
        locationModel.loadFromCursor(cursor);

        Uri currentUri = WeatherContract.CurrentConditionsEntry.buildCurrentConditionsUri(weatherModel.getLocationId());
        Cursor cursorCurrent = context.getContentResolver().query(currentUri, WeatherContract.CurrentConditionsEntry.FORECAST_COLUMNS, null, null, null);

        if (!cursorCurrent.moveToFirst()) {
            Log.d(LOG_TAG, "Cannot load current conditions from db, count (" + cursorCurrent.getCount() + ")");
        }

        CurrentConditionsModel currentModel = new CurrentConditionsModel();
        currentModel.loadFromCursor(cursorCurrent);


        long weatherId = weatherModel.getWeatherId();
        boolean isMetric = Preferences.getUnitType(context).equals(context.getString(R.string.pref_units_metric));

        // Set icon
        iconView.setImageResource(Utility.getArtResourceForWeatherCondition((int) weatherId));

        // Set accessibility property
        iconView.setContentDescription(weatherModel.getDescription());

        // Set date
        lastWeatherDateTime = weatherModel.getDateTime();
        redrawDateTime();

        if (currentModel != null) {
            currentView.setText(currentModel.getFormattedCurrentTemperature(context, isMetric));
            String highLow = context.getString(R.string.format_highlow_temperature,
                    Utility.getConvertedTemperature(weatherModel.getHigh(), isMetric),
                    Utility.getConvertedTemperature(weatherModel.getLow(), isMetric));
            highLowView.setText(highLow);
        } else {
            currentView.setText(weatherModel.getFormattedMaxTemperature(context, isMetric));
            highLowView.setText(weatherModel.getFormattedMinTemperature(context, isMetric));
        }

        weatherView.setText(Utility.capitalize(currentModel.getDescription()));
        humidityView.setText(context.getString(R.string.format_humidity_today, currentModel.getHumidity()));
        pressureView.setText(context.getString(R.string.format_pressure_today, currentModel.getHumidity()));

        String windDirection = Utility.getWindCompassDirections(currentModel.getWindDirection());
        double windSpeed = Utility.getConvertedWindSpeed(currentModel.getWindSpeed(), isMetric);
        int windFormat = isMetric ? R.string.format_wind_kmh_today : R.string.format_wind_mph_today;

        windView.setText(context.getString(windFormat,
                windSpeed,
                windDirection));

        // Set last sync date
        long lastSync = Preferences.getLastSyncTime(context);
        if (lastSync > 0) {
            SimpleDateFormat sd = new SimpleDateFormat("d MMM HH:mm");
            String syncText = context.getString(R.string.format_last_sync, sd.format(lastSync));
            syncView.setText(syncText);
        } else {
            String syncText = context.getString(R.string.format_last_sync, "never");
            syncView.setText(syncText);
        }

        // Set location
        if (locationView != null) {
            locationView.setText(locationModel.getCityName());
        }

        cursorCurrent.close();
    }

    /**
     * Helper method to redraw the date time. This is called when the UI is updated but also
     * when the BroadcastReceiver is notified of the time update Intent.ACTION_TIME_TICK
     */
    public void redrawDateTime() {
        // Set date
        String formatDay = "EEE, d MMMM HH:mm";
        SimpleDateFormat dt = new SimpleDateFormat(formatDay);
        String formattedDate = dt.format(System.currentTimeMillis());

        dateView.setText(formattedDate);
    }

}