package com.example.android.sunshine.app.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

public class WeatherContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.sunshine.app";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.sunshine.app/weather/ is a valid path for
    // looking at weather data. content://com.example.android.sunshine.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    public static final String PATH_WEATHER = "weather";
    public static final String PATH_LOCATION = "location";
    public static final String PATH_CURRENT = "current";

    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    public static final String[] FORECAST_COLUMNS = {
            WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            WeatherEntry.TABLE_NAME + "." + WeatherEntry.COLUMN_LOC_KEY,
            WeatherEntry.TABLE_NAME + "." + WeatherEntry.COLUMN_WEATHER_ID,
            WeatherEntry.TABLE_NAME + "." + WeatherEntry.COLUMN_DATE,
            WeatherEntry.TABLE_NAME + "." + WeatherEntry.COLUMN_SHORT_DESC,
            WeatherEntry.TABLE_NAME + "." + WeatherEntry.COLUMN_MIN_TEMP,
            WeatherEntry.TABLE_NAME + "." + WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.TABLE_NAME + "." + WeatherEntry.COLUMN_HUMIDITY,
            WeatherEntry.TABLE_NAME + "." + WeatherEntry.COLUMN_PRESSURE,
            WeatherEntry.TABLE_NAME + "." + WeatherEntry.COLUMN_WIND_SPEED,
            WeatherEntry.TABLE_NAME + "." + WeatherEntry.COLUMN_DEGREES,
            LocationEntry.TABLE_NAME + "." + LocationEntry.COLUMN_LOCATION_SETTING,
            LocationEntry.TABLE_NAME + "." + LocationEntry.COLUMN_CITY_NAME,
            LocationEntry.TABLE_NAME + "." + LocationEntry.COLUMN_COORD_LAT,
            LocationEntry.TABLE_NAME + "." + LocationEntry.COLUMN_COORD_LON,
            CurrentConditionsEntry.TABLE_NAME + "." + CurrentConditionsEntry.COLUMN_CUR_TEMP
    };

    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_LOC_KEY = 1;
    public static final int COL_WEATHER_WEATHER_ID = 2;
    public static final int COL_WEATHER_DATE = 3;
    public static final int COL_WEATHER_SHORT_DESC = 4;
    public static final int COL_WEATHER_MIN_TEMP = 5;
    public static final int COL_WEATHER_MAX_TEMP = 6;
    public static final int COL_WEATHER_HUMIDITY = 7;
    public static final int COL_WEATHER_PRESSURE = 8;
    public static final int COL_WEATHER_WIND_SPEED = 9;
    public static final int COL_WEATHER_DEGREES = 10;
    public static final int COL_LOCATION_SETTING = 11;
    public static final int COL_LOCATION_CITY_NAME = 12;
    public static final int COL_LOCATION_COORD_LAT = 13;
    public static final int COL_LOCATION_COORD_LONG = 14;
    public static final int COL_CUR_TEMP = 15;

    /*
        Inner class that defines the table contents of the location table
        Students: This is where you will add the strings.  (Similar to what has been
        done for WeatherEntry)
     */
    public static final class LocationEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        public static final String TABLE_NAME = "location";

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_LOCATION_SETTING = "location_setting";
        public static final String COLUMN_CITY_NAME = "city_name";
        public static final String COLUMN_COORD_LAT = "coord_lat";
        public static final String COLUMN_COORD_LON = "coord_long";

        public static final String[] PROJECTION = {
                WeatherContract.LocationEntry.COLUMN_ID,
                WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
                WeatherContract.LocationEntry.COLUMN_CITY_NAME,
                WeatherContract.LocationEntry.COLUMN_COORD_LAT,
                WeatherContract.LocationEntry.COLUMN_COORD_LON
        };

        public static final int COL_LOCATION_ID = 0;
        public static final int COL_LOCATION_SETTING = 1;
        public static final int COL_LOCATION_CITY_NAME = 2;
        public static final int COL_LOCATION_COORD_LAT = 3;
        public static final int COL_LOCATION_COORD_LONG = 4;

        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class CurrentConditionsEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CURRENT).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CURRENT;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CURRENT;

        public static final String TABLE_NAME = "current";

        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_LOC_KEY = "location_id";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_WEATHER_ID = "weather_id";
        public static final String COLUMN_SHORT_DESC = "short_desc";
        public static final String COLUMN_CUR_TEMP = "temp";
        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";
        public static final String COLUMN_HUMIDITY = "humidity";
        public static final String COLUMN_PRESSURE = "pressure";
        public static final String COLUMN_WIND_SPEED = "wind";
        public static final String COLUMN_DEGREES = "degrees";

        public static final String[] FORECAST_COLUMNS = {
                WeatherContract.CurrentConditionsEntry.COLUMN_ID,
                WeatherContract.CurrentConditionsEntry.COLUMN_LOC_KEY,
                WeatherContract.CurrentConditionsEntry.COLUMN_WEATHER_ID,
                WeatherContract.CurrentConditionsEntry.COLUMN_DATE,
                WeatherContract.CurrentConditionsEntry.COLUMN_SHORT_DESC,
                WeatherContract.CurrentConditionsEntry.COLUMN_CUR_TEMP,
                WeatherContract.CurrentConditionsEntry.COLUMN_MIN_TEMP,
                WeatherContract.CurrentConditionsEntry.COLUMN_MAX_TEMP,
                WeatherContract.CurrentConditionsEntry.COLUMN_HUMIDITY,
                WeatherContract.CurrentConditionsEntry.COLUMN_PRESSURE,
                WeatherContract.CurrentConditionsEntry.COLUMN_WIND_SPEED,
                WeatherContract.CurrentConditionsEntry.COLUMN_DEGREES
        };

        public static final int COL_ID = 0;
        public static final int COL_LOC_KEY = 1;
        public static final int COL_WEATHER_ID = 2;
        public static final int COL_DATE = 3;
        public static final int COL_SHORT_DESC = 4;
        public static final int COL_CUR_TEMP = 5;
        public static final int COL_MIN_TEMP = 6;
        public static final int COL_MAX_TEMP = 7;
        public static final int COL_HUMIDITY = 8;
        public static final int COL_PRESSURE = 9;
        public static final int COL_WIND_SPEED = 10;
        public static final int COL_DEGREES = 11;

        public static Uri buildCurrentConditionsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }

    /* Inner class that defines the table contents of the weather table */
    public static final class WeatherEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEATHER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;

        public static final String TABLE_NAME = "weather";

        // Column with the foreign key into the location table.
        public static final String COLUMN_LOC_KEY = "location_id";

        // Date, stored as long in milliseconds since the epoch
        public static final String COLUMN_DATE = "date";

        // Weather id as returned by API, to identify the icon to be used
        public static final String COLUMN_WEATHER_ID = "weather_id";

        // Short description and long description of the weather, as provided by API.
        // e.g "clear" vs "sky is clear".
        public static final String COLUMN_SHORT_DESC = "short_desc";

        // Min and max temperatures for the day (stored as floats)
        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";

        // Humidity is stored as a float representing percentage
        public static final String COLUMN_HUMIDITY = "humidity";

        // Humidity is stored as a float representing percentage
        public static final String COLUMN_PRESSURE = "pressure";

        // Windspeed is stored as a float representing windspeed  mph
        public static final String COLUMN_WIND_SPEED = "wind";

        // Degrees are meteorological degrees (e.g, 0 is north, 180 is south).  Stored as floats.
        public static final String COLUMN_DEGREES = "degrees";

        public static Uri buildWeatherUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildWeatherLocation(String locationSetting) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting).build();
        }

        public static Uri buildWeatherLocationWithStartDate(
                String locationSetting, long startDate) {
            long normalizedDate = normalizeDate(startDate);
            return CONTENT_URI.buildUpon().appendPath(locationSetting)
                    .appendQueryParameter(COLUMN_DATE, Long.toString(normalizedDate)).build();
        }

        public static Uri buildWeatherLocationWithDate(String locationSetting, long date) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting)
                    .appendPath(Long.toString(normalizeDate(date))).build();
        }

        public static String getLocationSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static long getDateFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }

        public static long getStartDateFromUri(Uri uri) {
            String dateString = uri.getQueryParameter(COLUMN_DATE);
            if (null != dateString && dateString.length() > 0)
                return Long.parseLong(dateString);
            else
                return 0;
        }
    }

}
