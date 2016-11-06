package com.example.android.sunshine.app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.sunshine.app.data.WeatherContract.LocationEntry;
import com.example.android.sunshine.app.data.WeatherContract.WeatherEntry;
import com.example.android.sunshine.app.data.WeatherContract.CurrentConditionsEntry;
import com.example.android.sunshine.app.data.WeatherContract.HourlyForecastEntry;

public class WeatherDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 4;

    static final String DATABASE_NAME = "weather.db";

    public WeatherDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createWeatherEntryTable(sqLiteDatabase);
        createLocationEntryTable(sqLiteDatabase);
        createCurrentConditionsEntryTable(sqLiteDatabase);
        createHourlyConditionsEntryTable(sqLiteDatabase);
    }

    private void createWeatherEntryTable(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_WEATHER_TABLE = "CREATE TABLE " + WeatherEntry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                WeatherEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this weather data
                WeatherEntry.COLUMN_LOC_KEY + " INTEGER NOT NULL, " +
                WeatherEntry.COLUMN_DATE + " INTEGER NOT NULL, " +
                WeatherEntry.COLUMN_SHORT_DESC + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_WEATHER_ID + " INTEGER NOT NULL," +

                WeatherEntry.COLUMN_MIN_TEMP + " REAL NOT NULL, " +
                WeatherEntry.COLUMN_MAX_TEMP + " REAL NOT NULL, " +

                WeatherEntry.COLUMN_HUMIDITY + " REAL NOT NULL, " +
                WeatherEntry.COLUMN_PRESSURE + " REAL NOT NULL, " +
                WeatherEntry.COLUMN_WIND_SPEED + " REAL NOT NULL, " +
                WeatherEntry.COLUMN_DEGREES + " REAL NOT NULL, " +

                // Set up the location column as a foreign key to location table.
                " FOREIGN KEY (" + WeatherEntry.COLUMN_LOC_KEY + ") REFERENCES " +
                LocationEntry.TABLE_NAME + " (" + LocationEntry._ID + "), " +

                // To assure the application have just one weather entry per day
                // per location, it's created a UNIQUE constraint with REPLACE strategy
                " UNIQUE (" + WeatherEntry.COLUMN_DATE + ", " +
                WeatherEntry.COLUMN_LOC_KEY + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    private void createLocationEntryTable(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE " + LocationEntry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                LocationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this weather data
                LocationEntry.COLUMN_LOCATION_ID + " INTEGER NOT NULL, " +
                LocationEntry.COLUMN_CITY_NAME + " TEXT NOT NULL, " +
                LocationEntry.COLUMN_COUNTRY_NAME + " TEXT NOT NULL, " +
                LocationEntry.COLUMN_COORD_LAT + " REAL NOT NULL, " +
                LocationEntry.COLUMN_COORD_LON + " REAL NOT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_LOCATION_TABLE);
    }

    private void createCurrentConditionsEntryTable(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_CURRENT_TABLE = "CREATE TABLE " + CurrentConditionsEntry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                CurrentConditionsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this weather data
                CurrentConditionsEntry.COLUMN_LOC_KEY + " INTEGER NOT NULL, " +
                CurrentConditionsEntry.COLUMN_DATE + " INTEGER NOT NULL, " +
                CurrentConditionsEntry.COLUMN_SHORT_DESC + " TEXT NOT NULL, " +
                CurrentConditionsEntry.COLUMN_WEATHER_ID + " INTEGER NOT NULL," +

                CurrentConditionsEntry.COLUMN_CUR_TEMP + " REAL NOT NULL, " +
                CurrentConditionsEntry.COLUMN_MIN_TEMP + " REAL NOT NULL, " +
                CurrentConditionsEntry.COLUMN_MAX_TEMP + " REAL NOT NULL, " +

                CurrentConditionsEntry.COLUMN_HUMIDITY + " REAL NOT NULL, " +
                CurrentConditionsEntry.COLUMN_PRESSURE + " REAL NOT NULL, " +
                CurrentConditionsEntry.COLUMN_WIND_SPEED + " REAL NOT NULL, " +
                CurrentConditionsEntry.COLUMN_DEGREES + " REAL NOT NULL, " +

                // Set up the location column as a foreign key to location table.
                " FOREIGN KEY (" + CurrentConditionsEntry.COLUMN_LOC_KEY + ") REFERENCES " +
                LocationEntry.TABLE_NAME + " (" + LocationEntry._ID + "), " +

                // To assure the application have just one weather entry per day
                // per location, it's created a UNIQUE constraint with REPLACE strategy
                " UNIQUE (" + CurrentConditionsEntry.COLUMN_DATE + ", " +
                CurrentConditionsEntry.COLUMN_LOC_KEY + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_CURRENT_TABLE);
    }

    private void createHourlyConditionsEntryTable(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_HOURLY_TABLE = "CREATE TABLE " + HourlyForecastEntry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                HourlyForecastEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this weather data
                HourlyForecastEntry.COLUMN_LOC_KEY + " INTEGER NOT NULL, " +
                HourlyForecastEntry.COLUMN_DATE + " INTEGER NOT NULL, " +
                HourlyForecastEntry.COLUMN_SHORT_DESC + " TEXT NOT NULL, " +
                HourlyForecastEntry.COLUMN_WEATHER_ID + " INTEGER NOT NULL," +

                HourlyForecastEntry.COLUMN_CUR_TEMP + " REAL NOT NULL, " +
                HourlyForecastEntry.COLUMN_MIN_TEMP + " REAL NOT NULL, " +
                HourlyForecastEntry.COLUMN_MAX_TEMP + " REAL NOT NULL, " +

                HourlyForecastEntry.COLUMN_HUMIDITY + " REAL NOT NULL, " +
                HourlyForecastEntry.COLUMN_PRESSURE + " REAL NOT NULL, " +
                HourlyForecastEntry.COLUMN_WIND_SPEED + " REAL NOT NULL, " +
                HourlyForecastEntry.COLUMN_DEGREES + " REAL NOT NULL, " +

                // Set up the location column as a foreign key to location table.
                " FOREIGN KEY (" + HourlyForecastEntry.COLUMN_LOC_KEY + ") REFERENCES " +
                LocationEntry.TABLE_NAME + " (" + LocationEntry._ID + "), " +

                // To assure the application have just one weather entry per day
                // per location, it's created a UNIQUE constraint with REPLACE strategy
                " UNIQUE (" + HourlyForecastEntry.COLUMN_DATE + ", " +
                HourlyForecastEntry.COLUMN_LOC_KEY + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_HOURLY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LocationEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WeatherEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CurrentConditionsEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + HourlyForecastEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
