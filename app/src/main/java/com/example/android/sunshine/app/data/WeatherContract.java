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
    public static final String PATH_HOURLY = "hourly";

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
            LocationEntry.TABLE_NAME + "." + LocationEntry.COLUMN_LOCATION_ID,
            LocationEntry.TABLE_NAME + "." + LocationEntry.COLUMN_CITY_NAME,
            LocationEntry.TABLE_NAME + "." + LocationEntry.COLUMN_COUNTRY_NAME,
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
    public static final int COL_LOCATION_LOCATION_ID = 11;
    public static final int COL_LOCATION_CITY_NAME = 12;
    public static final int COL_LOCATION_COUNTRY_NAME = 13;
    public static final int COL_LOCATION_COORD_LAT = 14;
    public static final int COL_LOCATION_COORD_LONG = 15;
    public static final int COL_CUR_TEMP = 16;

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

        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_LOCATION_ID = "location_id";
        public static final String COLUMN_CITY_NAME = "city_name";
        public static final String COLUMN_COUNTRY_NAME = "country_name";
        public static final String COLUMN_COORD_LAT = "coord_lat";
        public static final String COLUMN_COORD_LON = "coord_long";

        public static final String[] PROJECTION = {
                WeatherContract.LocationEntry.COLUMN_ID,
                WeatherContract.LocationEntry.COLUMN_LOCATION_ID,
                WeatherContract.LocationEntry.COLUMN_CITY_NAME,
                WeatherContract.LocationEntry.COLUMN_COUNTRY_NAME,
                WeatherContract.LocationEntry.COLUMN_COORD_LAT,
                WeatherContract.LocationEntry.COLUMN_COORD_LON
        };

        public static final int COL_LOCATION_ID = 0;
        public static final int COL_LOCATION_LOCATION_ID = 1;
        public static final int COL_LOCATION_CITY_NAME = 2;
        public static final int COL_LOCATION_COUNTRY_NAME = 3;
        public static final int COL_LOCATION_COORD_LAT = 4;
        public static final int COL_LOCATION_COORD_LONG = 5;

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

    public static final class HourlyForecastEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CURRENT).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HOURLY;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HOURLY;

        public static final String TABLE_NAME = "hourly";

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
                WeatherContract.HourlyForecastEntry.COLUMN_ID,
                WeatherContract.HourlyForecastEntry.COLUMN_LOC_KEY,
                WeatherContract.HourlyForecastEntry.COLUMN_WEATHER_ID,
                WeatherContract.HourlyForecastEntry.COLUMN_DATE,
                WeatherContract.HourlyForecastEntry.COLUMN_SHORT_DESC,
                WeatherContract.HourlyForecastEntry.COLUMN_CUR_TEMP,
                WeatherContract.HourlyForecastEntry.COLUMN_MIN_TEMP,
                WeatherContract.HourlyForecastEntry.COLUMN_MAX_TEMP,
                WeatherContract.HourlyForecastEntry.COLUMN_HUMIDITY,
                WeatherContract.HourlyForecastEntry.COLUMN_PRESSURE,
                WeatherContract.HourlyForecastEntry.COLUMN_WIND_SPEED,
                WeatherContract.HourlyForecastEntry.COLUMN_DEGREES
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

        public static Uri buildHourlyForecastUri(long forecastId) {
            return ContentUris.withAppendedId(CONTENT_URI, forecastId);
        }

        /**
         * Helper method to fetch the hourly weather conditions based on a location id.
         * @param locationId
         * @return
         */
        public static Uri buildHourlyForecastFromLocationIdUri(long locationId) {
            return CONTENT_URI.buildUpon()
                    .appendPath("location")
                    .appendPath(Long.toString(locationId))
                    .build();
        }

        public static Uri buildHourlyForecastFromLocationWithDateUri(long locationId, long dateTime) {
            return CONTENT_URI.buildUpon()
                    .appendPath("location")
                    .appendPath(Long.toString(locationId))
                    .appendPath(Long.toString(dateTime))
                    .build();
        }

        public static long getIdFromUri(Uri uri) {
            if (uri.getPathSegments().isEmpty()) {
                return 0l;
            }

            String id = uri.getPathSegments().get(1);

            try {
                Long l = Long.parseLong(id);
                return l;
            } catch (NumberFormatException nfe) {
                return 0l;
            }
        }

        public static long getLocationIdFromUri(Uri uri) {
            if (uri.getPathSegments().isEmpty() || uri.getPathSegments().size() < 3) {
                return 0l;
            }

            String id = uri.getPathSegments().get(3);

            try {
                Long l = Long.parseLong(id);
                return l;
            } catch (NumberFormatException nfe) {
                return 0l;
            }
        }

        public static long getDateFromUri(Uri uri) {
            if (uri.getPathSegments().isEmpty() || uri.getPathSegments().size() < 4) {
                return 0l;
            }

            String id = uri.getPathSegments().get(4);

            try {
                Long l = Long.parseLong(id);
                return l;
            } catch (NumberFormatException nfe) {
                return 0l;
            }
        }
    }

    /* Inner class that defines the table contents of the weather table */
    public static final class WeatherEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEATHER).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;

        public static final String TABLE_NAME = "weather";

        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_LOC_KEY = "location_id";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_WEATHER_ID = "weather_id";
        public static final String COLUMN_SHORT_DESC = "short_desc";
        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";
        public static final String COLUMN_HUMIDITY = "humidity";
        public static final String COLUMN_PRESSURE = "pressure";
        public static final String COLUMN_WIND_SPEED = "wind";
        public static final String COLUMN_DEGREES = "degrees";

        public static final int COL_ID = 0;
        public static final int COL_LOC_KEY = 1;
        public static final int COL_WEATHER_ID = 2;
        public static final int COL_DATE = 3;
        public static final int COL_SHORT_DESC = 4;
        public static final int COL_MIN_TEMP = 5;
        public static final int COL_MAX_TEMP = 6;
        public static final int COL_HUMIDITY = 7;
        public static final int COL_PRESSURE = 8;
        public static final int COL_WIND_SPEED = 9;
        public static final int COL_DEGREES = 10;

        public static Uri buildWeatherUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildWeatherLocationIdWithDate(long locationId, long timestamp) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(locationId))
                    .appendPath(Long.toString(timestamp))
                    .build();
        }

        public static Uri buildWeatherTodayWithLocationId(long locationId) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(locationId))
                    .appendPath("today")
                    .build();
        }

        public static long getLocationIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static long getDateFromUri(Uri uri) {
            String segment = uri.getPathSegments().get(2);

            try {
                Long date = Long.parseLong(segment);
                return date;
            } catch (NumberFormatException nfe) {
                return 0l;
            }
        }
    }

}
