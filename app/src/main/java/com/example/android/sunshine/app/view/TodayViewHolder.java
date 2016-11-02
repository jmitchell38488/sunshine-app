package com.example.android.sunshine.app.view;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
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

/**
 * Created by justinmitchell on 30/10/2016.
 */

public class TodayViewHolder {

    public final ImageView iconView;
    public final TextView dayView;
    public final TextView dateView;
    public final TextView descriptionView;
    public final TextView highTempView;
    public final TextView lowTempView;
    public final TextView humidityView;
    public final TextView pressureView;
    public final TextView windView;
    public final TextView locationView;

    public TodayViewHolder(View view) {
        iconView = (ImageView) view.findViewById(R.id.detail_icon);
        dayView = (TextView) view.findViewById(R.id.detail_day_textview);
        dateView = (TextView) view.findViewById(R.id.detail_date_textview);
        descriptionView = (TextView) view.findViewById(R.id.detail_forecast_textview);
        highTempView = (TextView) view.findViewById(R.id.detail_high_textview);
        lowTempView = (TextView) view.findViewById(R.id.detail_low_textview);
        humidityView = (TextView) view.findViewById(R.id.detail_humidity_textview);
        pressureView = (TextView) view.findViewById(R.id.detail_pressure_textview);
        windView = (TextView) view.findViewById(R.id.detail_wind_textview);

        if (view.findViewById(R.id.detail_location_textview) != null) {
            locationView = (TextView) view.findViewById(R.id.detail_location_textview);
        } else {
            locationView = null;
        }
    }

    public void reloadProperties(Cursor cursor, Context context) {
        if (!cursor.moveToFirst()) {
            return;
        }

        WeatherModel weatherModel = new WeatherModel();
        weatherModel.loadFromCursor(cursor);

        LocationModel locationModel = new LocationModel();
        locationModel.loadFromCursor(cursor);

        Uri currentUri = WeatherContract.CurrentConditionsEntry.buildCurrentConditionsUri(locationModel.getId());
        Cursor cursorCurrent = context.getContentResolver().query(currentUri, WeatherContract.CurrentConditionsEntry.FORECAST_COLUMNS, null, null, null);

        CurrentConditionsModel currentModel = new CurrentConditionsModel();
        currentModel.loadFromCursor(cursorCurrent);

        cursorCurrent.close();

        long weatherId = weatherModel.getWeatherId();
        boolean isMetric = Preferences.getUnitType(context).equals(context.getString(R.string.pref_units_metric));

        // Set icon
        iconView.setImageResource(Utility.getArtResourceForWeatherCondition((int) weatherModel.getWeatherId()));

        // Set accessibility property
        iconView.setContentDescription(weatherModel.getDescription());

        // Set day
        dayView.setText(weatherModel.getDayName(context));

        // Set date
        dateView.setText(weatherModel.getFormattedMonthDay(context));

        // Set description
        descriptionView.setText(weatherModel.getDescription());

        // Set temperature high
        highTempView.setText(weatherModel.getFormattedMaxTemperature(context, isMetric));

        // Set temperature low
        lowTempView.setText(weatherModel.getFormattedMinTemperature(context, isMetric));

        // Set humidity
        humidityView.setText(weatherModel.getFormattedHumidity(context));

        // Set pressure
        pressureView.setText(weatherModel.getFormattedPressure(context));

        // Set wind details
        windView.setText(weatherModel.getFormattedWindDetails(context));

        // Set location
        if (locationView != null) {
            locationView.setText(locationModel.getCityName());
        }
    }

}