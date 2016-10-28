package com.example.android.sunshine.app.data.model;

import com.example.android.sunshine.app.R;

import android.content.Context;
import android.content.SharedPreferences;
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
    private int locationId;

    // date
    private long date;

    // weather_id
    private int weatherId;

    // short_desc
    private String shortDesc;

    // min
    private double min;

    // max
    private double max;

    // humidity
    private double humidity;

    // pressure
    private double pressure;

    // wind
    private double wind;

    // degrees
    private double degrees;

    public WeatherModel() {
        // Do nothing
    }

    public WeatherModel(int id, int locationId, int date, int weatherId, String shortDesc, double min,
                        double max, double humidity, double pressure, double wind, double degrees) {
        this.id = id;
        this.locationId = locationId;
        this.date = date;
        this.weatherId = weatherId;
        this.shortDesc = shortDesc;
        this.min = min;
        this.max = max;
        this.humidity = humidity;
        this.pressure = pressure;
        this.wind = wind;
        this.degrees = degrees;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(int weatherId) {
        this.weatherId = weatherId;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
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

    public double getWind() {
        return wind;
    }

    public void setWind(double wind) {
        this.wind = wind;
    }

    public double getDegrees() {
        return degrees;
    }

    public void setDegrees(double degrees) {
        this.degrees = degrees;
    }

    public boolean isMetric(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_units_key),
                context.getString(R.string.pref_units_metric))
                .equals(context.getString(R.string.pref_units_metric));
    }

    public String formatDate(long dateInMilliseconds) {
        Date date = new Date(dateInMilliseconds);
        return DateFormat.getDateInstance().format(date);
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
    public String getFormattedTemperature(double temperature, Context context) {
        // Data stored in Celsius by default.  If user prefers to see in Fahrenheit, convert
        // the values here.
        String suffix = "\u00B0";
        if (!isMetric(context)) {
            temperature = (temperature * 1.8) + 32;
        }

        // For presentation, assume the user doesn't care about tenths of a degree.
        return String.format(context.getString(R.string.format_temperature), temperature);
    }

    public String getFormattedMinTemperature(Context context) {
        return getFormattedTemperature(min, context);
    }

    public String getFormattedMaxTemperature(Context context) {
        return getFormattedTemperature(max, context);
    }

    /**
     * Helper method to convert the database representation of the date into something to display
     * to users.  As classy and polished a user experience as "20140102" is, we can do better.
     *
     * The day string for forecast uses the following logic:
     * For today: "Today, June 8"
     * For tomorrow:  "Tomorrow"
     * For the next 5 days: "Wednesday" (just the day name)
     * For all days after that: "Mon Jun 8"
     *
     * @param context Context to use for resource localization
     * @return a user-friendly representation of the date.
     */
    public String getFriendlyDayString(Context context) {
        long dateInMillis = (long) date;
        Time time = new Time();
        time.setToNow();
        long currentTime = System.currentTimeMillis();
        int julianDay = Time.getJulianDay(dateInMillis, time.gmtoff);
        int currentJulianDay = Time.getJulianDay(currentTime, time.gmtoff);

        // If the date we're building the String for is today's date, the format
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
            // If the input date is less than a week in the future, just return the day name.
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
        // If the date is today, return the localized version of "Today" instead of the actual
        // day name.

        long dateInMillis = (long) date;
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
     * Converts db date format to the format "Month day", e.g "June 24".
     * @param context Context to use for resource localization
     * @return The day in the form of a string formatted "December 6"
     */
    public String getFormattedMonthDay(Context context) {
        long dateInMillis = (long) date;
        Time time = new Time();
        time.setToNow();

        SimpleDateFormat dbDateFormat = new SimpleDateFormat(DATE_FORMAT);
        SimpleDateFormat monthDayFormat = new SimpleDateFormat("MMMM dd");
        String monthDayString = monthDayFormat.format(dateInMillis);

        return monthDayString;
    }

    public String getFormattedString(String unitType, Context context) {
        String highAndLow = formatHighLows(unitType, context);
        return getFriendlyDayString(context) + " - " + shortDesc + " - " + formatHighLows(unitType, context);
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(String unitType, Context context) {
        double high = max, low = min;
        String unitStr = "c";

        if (unitType.equals(context.getString(R.string.pref_units_imperial))) {
            high = (max * 1.8) + 32;
            low = (min * 1.8) + 32;
            unitStr = "f";
        } else if (!unitType.equals(context.getString(R.string.pref_units_metric))) {
            Log.d(LOG_TAG, "Unit type not found: " + unitType);
            unitStr = "";
        }

        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        return roundedHigh + unitStr + "/" + roundedLow + unitStr;
    }
}
