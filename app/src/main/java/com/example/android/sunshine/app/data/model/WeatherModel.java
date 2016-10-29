package com.example.android.sunshine.app.data.model;

import com.example.android.sunshine.app.R;
import com.example.android.sunshine.app.data.WeatherContract;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by justinmitchell on 25/10/2016.
 */

public class WeatherModel {

    private final String LOG_TAG = WeatherModel.class.getSimpleName();

    private static final String DATE_FORMAT = "yyyyMMdd";

    // _id
    private int id;

    // location_id
    private float locationId;

    // dateTime
    private long dateTime;

    // weather_id
    private float weatherId;

    // short_desc
    private String description;

    // low
    private double low;

    // high
    private double high;

    // humidity
    private double humidity;

    // pressure
    private double pressure;

    // windSpeed
    private double windSpeed;

    // windDirection
    private double windDirection;

    public WeatherModel() {
        // Do nothing
    }

    public WeatherModel(int id, float locationId, int dateTime, float weatherId, String description, double low,
                        double high, double humidity, double pressure, double windSpeed, double windDirection) {
        this.id = id;
        this.locationId = locationId;
        this.dateTime = dateTime;
        this.weatherId = weatherId;
        this.description = description;
        this.low = low;
        this.high = high;
        this.humidity = humidity;
        this.pressure = pressure;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getLocationId() {
        return locationId;
    }

    public void setLocationId(float locationId) {
        this.locationId = locationId;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public float getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(float weatherId) {
        this.weatherId = weatherId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public double getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(double windDirection) {
        this.windDirection = windDirection;
    }

    public boolean isMetric(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_units_key),
                context.getString(R.string.pref_units_metric))
                .equals(context.getString(R.string.pref_units_metric));
    }

    public String formatDate() {
        return DateFormat.getDateInstance().format(new Date(dateTime));
    }

    public String getFormattedWind(Context context) {
        return null;
    }

    /**
     * Returns the formatted temperature in the users preferred measurement system. By default it
     * is in celcius (metric), but can be in farenheight (imperial)
     * @param temperature The temperature in celcius
     * @param context
     * @return
     */
    public String getFormattedTemperature(double temperature, Context context, boolean isMetric) {
        double temp;
        if (!isMetric) {
            temp = 9 * temperature / 5 + 32;
        } else {
            temp = temperature;
        }

        return context.getString(R.string.format_temperature, temp);
    }

    public String getFormattedMinTemperature(Context context, boolean isMetric) {
        return getFormattedTemperature(low, context, isMetric);
    }

    public String getFormattedMaxTemperature(Context context, boolean isMetric) {
        return getFormattedTemperature(high, context, isMetric);
    }

    /**
     * Helper method to convert the database representation of the dateTime into something to display
     * to users.  As classy and polished a user experience as "20140102" is, we can do better.
     *
     * The day string for forecast uses the following logic:
     * For today: "Today, June 8"
     * For tomorrow:  "Tomorrow"
     * For the next 5 days: "Wednesday" (just the day name)
     * For all days after that: "Mon Jun 8"
     *
     * @param context Context to use for resource localization
     * @return a user-friendly representation of the dateTime.
     */
    public String getFriendlyDayString(Context context) {
        long dateInMillis = (long) dateTime;
        Time time = new Time();
        time.setToNow();
        long currentTime = System.currentTimeMillis();
        int julianDay = Time.getJulianDay(dateInMillis, time.gmtoff);
        int currentJulianDay = Time.getJulianDay(currentTime, time.gmtoff);

        // If the dateTime we're building the String for is today's dateTime, the format
        // is "Today, June 24"
        if (julianDay == currentJulianDay) {
            String today = context.getString(R.string.today);
            int formatId = R.string.format_full_friendly_date;
            return String.format(
                    context.getString(formatId),
                    today,
                    getFormattedMonthDay(context)
            );
        } else if ( julianDay < currentJulianDay + 7 ) {
            // If the input dateTime is less than a week in the future, just return the day name.
            return getDayName(context);
        } else {
            // Otherwise, use the form "Mon Jun 3"
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(dateInMillis);
        }
    }

    /**
     * Given a day, returns just the name to use for that day.
     * E.g "today", "tomorrow", "wednesday".
     *
     * @param context Context to use for resource localization
     * @return
     */
    public String getDayName(Context context) {
        // If the dateTime is today, return the localized version of "Today" instead of the actual
        // day name.

        long dateInMillis = (long) dateTime;
        Time t = new Time();
        t.setToNow();
        int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
        int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);

        if (julianDay == currentJulianDay) {
            return context.getString(R.string.today);
        } else if ( julianDay == currentJulianDay +1 ) {
            return context.getString(R.string.tomorrow);
        } else {
            Time time = new Time();
            time.setToNow();
            // Otherwise, the format is just the day of the week (e.g "Wednesday".
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
            return dayFormat.format(dateInMillis);
        }
    }

    /**
     * Converts db dateTime format to the format "Month day", e.g "June 24".
     * @param context Context to use for resource localization
     * @return The day in the form of a string formatted "December 6"
     */
    public String getFormattedMonthDay(Context context) {
        long dateInMillis = (long) dateTime;
        Time time = new Time();
        time.setToNow();

        //SimpleDateFormat dbDateFormat = new SimpleDateFormat(DATE_FORMAT);
        SimpleDateFormat monthDayFormat = new SimpleDateFormat("MMMM dd");
        String monthDayString = monthDayFormat.format(dateInMillis);

        return monthDayString;
    }

    public String getFormattedString(String unitType, Context context) {
        return getFriendlyDayString(context) + " - " + description + " - " + formatHighLows(unitType, context);
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(String unitType, Context context) {
        double high = this.high, low = this.low;
        String unitStr = "c";

        if (unitType.equals(context.getString(R.string.pref_units_imperial))) {
            high = (this.high * 1.8) + 32;
            low = (this.low * 1.8) + 32;
            unitStr = "f";
        } else if (!unitType.equals(context.getString(R.string.pref_units_metric))) {
            Log.d(LOG_TAG, "Unit type not found: " + unitType);
            unitStr = "";
        }

        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        return roundedHigh + unitStr + "/" + roundedLow + unitStr;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();

        values.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, locationId);
        values.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, weatherId);
        values.put(WeatherContract.WeatherEntry.COLUMN_DATE, dateTime);
        values.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, humidity);
        values.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, pressure);
        values.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, windSpeed);
        values.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, windDirection);
        values.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, high);
        values.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, low);
        values.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC, description);

        return values;
    }

    public void loadFromContentValues(ContentValues contentValues) {
        locationId = contentValues.getAsLong(WeatherContract.WeatherEntry.COLUMN_LOC_KEY);
        weatherId = contentValues.getAsLong(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID);
        dateTime = contentValues.getAsLong(WeatherContract.WeatherEntry.COLUMN_DATE);
        humidity = contentValues.getAsDouble(WeatherContract.WeatherEntry.COLUMN_HUMIDITY);
        pressure = contentValues.getAsDouble(WeatherContract.WeatherEntry.COLUMN_PRESSURE);
        windSpeed = contentValues.getAsDouble(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED);
        windDirection = contentValues.getAsInteger(WeatherContract.WeatherEntry.COLUMN_DEGREES);
        high = contentValues.getAsDouble(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP);
        low = contentValues.getAsDouble(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP);
        description = contentValues.getAsString(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC);
    }

    public void loadFromCursor(Cursor cursor) {
        int _locationId = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_LOC_KEY);
        int _weatherId = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID);
        int _dateTime = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE);
        int _humidity = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_HUMIDITY);
        int _pressure = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_PRESSURE);
        int _windSpeed = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED);
        int _windDirection = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DEGREES);
        int _high = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP);
        int _low = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP);
        int _description = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC);

        locationId = cursor.getLong(_locationId);
        weatherId = cursor.getLong(_weatherId);
        dateTime = cursor.getLong(_dateTime);
        humidity = cursor.getDouble(_humidity);
        pressure = cursor.getDouble(_pressure);
        windSpeed = cursor.getDouble(_windSpeed);
        windDirection = cursor.getInt(_windDirection);
        high = cursor.getDouble(_high);
        low = cursor.getDouble(_low);
        description = cursor.getString(_description);
    }
}
