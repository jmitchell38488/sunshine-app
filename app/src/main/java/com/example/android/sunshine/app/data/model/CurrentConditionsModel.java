package com.example.android.sunshine.app.data.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.android.sunshine.app.data.WeatherContract;

import java.text.SimpleDateFormat;

/**
 * Created by justinmitchell on 25/10/2016.
 */

public class CurrentConditionsModel extends WeatherModel {

    private final String LOG_TAG = CurrentConditionsModel.class.getSimpleName();

    private double temp;

    public CurrentConditionsModel() {
        super();
    }

    public CurrentConditionsModel(int id, long locationId, int dateTime, long weatherId, String description, double temp,
                                  double low, double high, double humidity, double pressure, double windSpeed, double windDirection) {
        super(id, locationId, dateTime, weatherId, description, low, high, humidity, pressure, windSpeed, windDirection);
        this.temp = temp;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public String getFormattedCurrentTemperature(Context context, boolean isMetric) {
        return getFormattedTemperature(temp, context, isMetric);
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();

        values.put(WeatherContract.CurrentConditionsEntry.COLUMN_LOC_KEY, locationId);
        values.put(WeatherContract.CurrentConditionsEntry.COLUMN_WEATHER_ID, weatherId);
        values.put(WeatherContract.CurrentConditionsEntry.COLUMN_DATE, dateTime);
        values.put(WeatherContract.CurrentConditionsEntry.COLUMN_HUMIDITY, humidity);
        values.put(WeatherContract.CurrentConditionsEntry.COLUMN_PRESSURE, pressure);
        values.put(WeatherContract.CurrentConditionsEntry.COLUMN_WIND_SPEED, windSpeed);
        values.put(WeatherContract.CurrentConditionsEntry.COLUMN_DEGREES, windDirection);
        values.put(WeatherContract.CurrentConditionsEntry.COLUMN_CUR_TEMP, temp);
        values.put(WeatherContract.CurrentConditionsEntry.COLUMN_MAX_TEMP, high);
        values.put(WeatherContract.CurrentConditionsEntry.COLUMN_MIN_TEMP, low);
        values.put(WeatherContract.CurrentConditionsEntry.COLUMN_SHORT_DESC, description);

        return values;
    }

    public void loadFromContentValues(ContentValues contentValues) {
        locationId = contentValues.getAsLong(WeatherContract.CurrentConditionsEntry.COLUMN_LOC_KEY);
        weatherId = contentValues.getAsLong(WeatherContract.CurrentConditionsEntry.COLUMN_WEATHER_ID);
        dateTime = contentValues.getAsLong(WeatherContract.CurrentConditionsEntry.COLUMN_DATE);
        humidity = contentValues.getAsDouble(WeatherContract.CurrentConditionsEntry.COLUMN_HUMIDITY);
        pressure = contentValues.getAsDouble(WeatherContract.CurrentConditionsEntry.COLUMN_PRESSURE);
        windSpeed = contentValues.getAsDouble(WeatherContract.CurrentConditionsEntry.COLUMN_WIND_SPEED);
        windDirection = contentValues.getAsInteger(WeatherContract.CurrentConditionsEntry.COLUMN_DEGREES);
        temp = contentValues.getAsDouble(WeatherContract.CurrentConditionsEntry.COLUMN_CUR_TEMP);
        high = contentValues.getAsDouble(WeatherContract.CurrentConditionsEntry.COLUMN_MAX_TEMP);
        low = contentValues.getAsDouble(WeatherContract.CurrentConditionsEntry.COLUMN_MIN_TEMP);
        description = contentValues.getAsString(WeatherContract.CurrentConditionsEntry.COLUMN_SHORT_DESC);
    }

    public void loadFromCursor(Cursor cursor) {
        locationId = cursor.getLong(WeatherContract.CurrentConditionsEntry.COL_LOC_KEY);
        weatherId = cursor.getLong(WeatherContract.CurrentConditionsEntry.COL_WEATHER_ID);
        dateTime = cursor.getLong(WeatherContract.CurrentConditionsEntry.COL_DATE);
        humidity = cursor.getDouble(WeatherContract.CurrentConditionsEntry.COL_HUMIDITY);
        pressure = cursor.getDouble(WeatherContract.CurrentConditionsEntry.COL_PRESSURE);
        windSpeed = cursor.getDouble(WeatherContract.CurrentConditionsEntry.COL_WIND_SPEED);
        windDirection = cursor.getInt(WeatherContract.CurrentConditionsEntry.COL_DEGREES);
        temp = cursor.getDouble(WeatherContract.CurrentConditionsEntry.COL_CUR_TEMP);
        high = cursor.getDouble(WeatherContract.CurrentConditionsEntry.COL_MAX_TEMP);
        low = cursor.getDouble(WeatherContract.CurrentConditionsEntry.COL_MIN_TEMP);
        description = cursor.getString(WeatherContract.CurrentConditionsEntry.COL_SHORT_DESC);
    }
}
