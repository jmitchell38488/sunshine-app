package com.example.android.sunshine.app.data.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.android.sunshine.app.data.WeatherContract;

/**
 * Created by justinmitchell on 25/10/2016.
 */

public class HourlyModel extends WeatherModel {

    private final String LOG_TAG = HourlyModel.class.getSimpleName();

    private double temp;

    public HourlyModel() {
        super();
    }

    public HourlyModel(int id, long locationId, int dateTime, long weatherId, String description, double temp,
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

        values.put(WeatherContract.HourlyForecastEntry.COLUMN_LOC_KEY, locationId);
        values.put(WeatherContract.HourlyForecastEntry.COLUMN_WEATHER_ID, weatherId);
        values.put(WeatherContract.HourlyForecastEntry.COLUMN_DATE, dateTime);
        values.put(WeatherContract.HourlyForecastEntry.COLUMN_HUMIDITY, humidity);
        values.put(WeatherContract.HourlyForecastEntry.COLUMN_PRESSURE, pressure);
        values.put(WeatherContract.HourlyForecastEntry.COLUMN_WIND_SPEED, windSpeed);
        values.put(WeatherContract.HourlyForecastEntry.COLUMN_DEGREES, windDirection);
        values.put(WeatherContract.HourlyForecastEntry.COLUMN_CUR_TEMP, temp);
        values.put(WeatherContract.HourlyForecastEntry.COLUMN_MAX_TEMP, high);
        values.put(WeatherContract.HourlyForecastEntry.COLUMN_MIN_TEMP, low);
        values.put(WeatherContract.HourlyForecastEntry.COLUMN_SHORT_DESC, description);

        return values;
    }

    public void loadFromContentValues(ContentValues contentValues) {
        locationId = contentValues.getAsLong(WeatherContract.HourlyForecastEntry.COLUMN_LOC_KEY);
        weatherId = contentValues.getAsLong(WeatherContract.HourlyForecastEntry.COLUMN_WEATHER_ID);
        dateTime = contentValues.getAsLong(WeatherContract.HourlyForecastEntry.COLUMN_DATE);
        humidity = contentValues.getAsDouble(WeatherContract.HourlyForecastEntry.COLUMN_HUMIDITY);
        pressure = contentValues.getAsDouble(WeatherContract.HourlyForecastEntry.COLUMN_PRESSURE);
        windSpeed = contentValues.getAsDouble(WeatherContract.HourlyForecastEntry.COLUMN_WIND_SPEED);
        windDirection = contentValues.getAsInteger(WeatherContract.HourlyForecastEntry.COLUMN_DEGREES);
        temp = contentValues.getAsDouble(WeatherContract.HourlyForecastEntry.COLUMN_CUR_TEMP);
        high = contentValues.getAsDouble(WeatherContract.HourlyForecastEntry.COLUMN_MAX_TEMP);
        low = contentValues.getAsDouble(WeatherContract.HourlyForecastEntry.COLUMN_MIN_TEMP);
        description = contentValues.getAsString(WeatherContract.HourlyForecastEntry.COLUMN_SHORT_DESC);
    }

    public void loadFromCursor(Cursor cursor) {
        locationId = cursor.getLong(WeatherContract.HourlyForecastEntry.COL_LOC_KEY);
        weatherId = cursor.getLong(WeatherContract.HourlyForecastEntry.COL_WEATHER_ID);
        dateTime = cursor.getLong(WeatherContract.HourlyForecastEntry.COL_DATE);
        humidity = cursor.getDouble(WeatherContract.HourlyForecastEntry.COL_HUMIDITY);
        pressure = cursor.getDouble(WeatherContract.HourlyForecastEntry.COL_PRESSURE);
        windSpeed = cursor.getDouble(WeatherContract.HourlyForecastEntry.COL_WIND_SPEED);
        windDirection = cursor.getInt(WeatherContract.HourlyForecastEntry.COL_DEGREES);
        temp = cursor.getDouble(WeatherContract.HourlyForecastEntry.COL_CUR_TEMP);
        high = cursor.getDouble(WeatherContract.HourlyForecastEntry.COL_MAX_TEMP);
        low = cursor.getDouble(WeatherContract.HourlyForecastEntry.COL_MIN_TEMP);
        description = cursor.getString(WeatherContract.HourlyForecastEntry.COL_SHORT_DESC);
    }
}
