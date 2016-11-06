package com.example.android.sunshine.app.data.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.example.android.sunshine.app.data.WeatherContract;
import com.example.android.sunshine.app.data.WeatherDbHelper;
import com.example.android.sunshine.app.util.Utility;

/**
 * Created by justinmitchell on 27/10/2016.
 */
public class WeatherSqlite implements IStorage {

    private WeatherDbHelper mOpenHelper;

    private static final SQLiteQueryBuilder sWeatherByLocationSettingQueryBuilder;
    private static final SQLiteQueryBuilder sHourlyByLocationIdQueryBuilder;

    static {
        sWeatherByLocationSettingQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        sWeatherByLocationSettingQueryBuilder.setTables(
                //weather INNER JOIN location ON weather.location_id = location._id
                WeatherContract.WeatherEntry.TABLE_NAME + " INNER JOIN " +
                        WeatherContract.LocationEntry.TABLE_NAME +
                        " ON " + WeatherContract.WeatherEntry.TABLE_NAME +
                        "." + WeatherContract.WeatherEntry.COLUMN_LOC_KEY +
                        " = " + WeatherContract.LocationEntry.TABLE_NAME +
                        "." + WeatherContract.LocationEntry._ID + " " +

                //INNER JOIN current ON current.location_id = location._id
               " INNER JOIN " +
                        WeatherContract.CurrentConditionsEntry.TABLE_NAME +
                        " ON " + WeatherContract.CurrentConditionsEntry.TABLE_NAME +
                        "." + WeatherContract.CurrentConditionsEntry.COLUMN_LOC_KEY +
                        " = " + WeatherContract.LocationEntry.TABLE_NAME +
                        "." + WeatherContract.LocationEntry._ID
        );

        sHourlyByLocationIdQueryBuilder = new SQLiteQueryBuilder();

        // hourly INNER JOIN location ON hourly.location_id = location._id
        sHourlyByLocationIdQueryBuilder.setTables(
                WeatherContract.HourlyForecastEntry.TABLE_NAME + " INNER JOIN " +
                        WeatherContract.LocationEntry.TABLE_NAME +
                        " ON " + WeatherContract.HourlyForecastEntry.TABLE_NAME +
                        "." + WeatherContract.HourlyForecastEntry.COLUMN_LOC_KEY +
                        " = " + WeatherContract.LocationEntry.TABLE_NAME +
                        "." + WeatherContract.LocationEntry._ID
        );
    }

    private static final String sWeatherByLocationIdWithStartDateSelection =
            WeatherContract.LocationEntry.TABLE_NAME +
                    "." + WeatherContract.LocationEntry.COLUMN_ID + " = ? AND " +
                    WeatherContract.WeatherEntry.TABLE_NAME +
                    "." + WeatherContract.WeatherEntry.COLUMN_DATE + " = ? ";

    private static final String sLocationIdSelection =
            WeatherContract.LocationEntry.TABLE_NAME +
                    "." + WeatherContract.LocationEntry.COLUMN_ID + " = ?";

    private static final String sCurrentConditionsSelection =
            WeatherContract.CurrentConditionsEntry.TABLE_NAME+
                    "." + WeatherContract.CurrentConditionsEntry.COLUMN_LOC_KEY + " = ? ";

    private static final String sHourlyIdSelection =
            WeatherContract.HourlyForecastEntry.TABLE_NAME +
                    "." + WeatherContract.HourlyForecastEntry.COLUMN_ID + " = ?";

    private static final String sHourlyByLocationIdWithDateSelection =
            WeatherContract.LocationEntry.TABLE_NAME +
                    "." + WeatherContract.LocationEntry.COLUMN_ID + " = ? AND " +
                    WeatherContract.HourlyForecastEntry.TABLE_NAME +
                    "." + WeatherContract.HourlyForecastEntry.COLUMN_DATE + " = ? ";

    public Cursor query(SQLiteQueryBuilder builder, String[] projectionIn, String selection, String[] selectionArgs) {
        return this.query(
                builder,
                projectionIn,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
    }

    public Cursor query(SQLiteQueryBuilder builder, String[] projectionIn, String selection, String[] selectionArgs, String groupBy, String having, String sortOrder) {
        return builder.query(
                mOpenHelper.getReadableDatabase(),
                projectionIn,
                selection,
                selectionArgs,
                groupBy,
                having,
                sortOrder
        );
    }

    public Cursor query(String tableName, String[] projectionIn, String selection, String[] selectionArgs, String groupBy, String having, String sortOrder) {
        return mOpenHelper.getReadableDatabase().query(
                tableName,
                projectionIn,
                selection,
                selectionArgs,
                groupBy,
                having,
                sortOrder
        );
    }

    public WeatherSqlite(Context context) {
        mOpenHelper = new WeatherDbHelper(context);
    }

    public StorageType getType() {
        return StorageType.SQLITE;
    }

    /**
     * Performs an insert operation on the table and returns the new insert id
     * @param tableName
     * @param contentValues
     * @return
     */
    public long insert(String tableName, ContentValues contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        if (tableName == null || tableName.isEmpty()) {
            throw new android.database.SQLException("Invalid or null table name");
        }

        return db.insert(tableName, null, contentValues);
    }

    /**
     * Performs a delete operation on the table and returns the no. of rows affected
     * @param tableName
     * @param selection
     * @param selectionArgs
     * @return
     */
    public int delete(String tableName, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        if (tableName == null || tableName.isEmpty()) {
            throw new android.database.SQLException("Invalid or null table name");
        }

        return db.delete(tableName, selection, selectionArgs);
    }

    public int update(String tableName, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        if (tableName == null || tableName.isEmpty()) {
            throw new android.database.SQLException("Invalid or null table name");
        }

        return db.update(tableName, values, selection, selectionArgs);
    }

    public int bulkInsert(String tableName, ContentValues[] contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        db.beginTransaction();
        int returnCount = 0;

        try {
            for (ContentValues recordSet : contentValues) {
                long id = db.insert(tableName, null, recordSet);

                if (id != -1) {
                    returnCount++;
                }
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return returnCount;
    }

    public Cursor getWeatherTodayByLocationId(Uri uri, String[] projection, String sortOrder) {
        long locationId = WeatherContract.WeatherEntry.getLocationIdFromUri(uri);
        long date = Utility.getMidnightTimeToday();

        String[] selectionArgs = new String[] {
                Long.toString(locationId),
                Long.toString(date)
        };

        String selection = sWeatherByLocationIdWithStartDateSelection;

        return this.query(
                sWeatherByLocationSettingQueryBuilder,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    public Cursor getWeatherByLocationId(Uri uri, String[] projection, String sortOrder) {
        long locationId = WeatherContract.WeatherEntry.getLocationIdFromUri(uri);

        String[] selectionArgs = new String[] {
                Long.toString(locationId),
        };

        String selection = sLocationIdSelection;

        return this.query(
                sWeatherByLocationSettingQueryBuilder,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public Cursor getWeatherByLocationIdAndDate(Uri uri, String[] projection, String sortOrder) {
        long locationId = WeatherContract.WeatherEntry.getLocationIdFromUri(uri);
        long date = WeatherContract.WeatherEntry.getDateFromUri(uri);

        // No date was returned
        if (date == 0) {
            return null;
        }

        String[] selectionArgs = new String[] {
                Long.toString(locationId),
                Long.toString(date)
        };

        String selection = sWeatherByLocationIdWithStartDateSelection;

        return this.query(
                sWeatherByLocationSettingQueryBuilder,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    public Cursor getWeather(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return this.query(
                WeatherContract.WeatherEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    public Cursor getLocation(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return this.query(
                WeatherContract.LocationEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    public Cursor getCurrentConditions(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return this.query(
                WeatherContract.CurrentConditionsEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    public Cursor getCurrentConditionsWithLocationId(Uri uri, String[] projection, String sortOrder) {
        long locationId = WeatherContract.CurrentConditionsEntry.getIdFromUri(uri);

        String[] selectionArgs = new String[]{
                Long.toString(locationId)
        };

        String selection = sCurrentConditionsSelection;

        return this.query(
                WeatherContract.CurrentConditionsEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public Cursor getHourly(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return this.query(
                WeatherContract.HourlyForecastEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public Cursor getHourlyWithId(Uri uri,  String[] projection, String sortOrder) {
        long id = WeatherContract.HourlyForecastEntry.getIdFromUri(uri);

        // No date was returned
        if (id == 0) {
            return null;
        }

        String[] selectionArgs = new String[] {
                Long.toString(id)
        };

        String selection = sHourlyIdSelection;

        return this.query(
                WeatherContract.HourlyForecastEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public Cursor getHourlyByLocationId(Uri uri, String[] projection, String sortOrder) {
        long locationId = WeatherContract.HourlyForecastEntry.getLocationIdFromUri(uri);

        if (locationId == 0) {
            return null;
        }

        String[] selectionArgs = new String[] {
                Long.toString(locationId),
        };

        String selection = sLocationIdSelection;

        return this.query(
                sWeatherByLocationSettingQueryBuilder,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public Cursor getHourlyByLocationIdAndDate(Uri uri, String[] projection, String sortOrder) {
        long locationId = WeatherContract.HourlyForecastEntry.getLocationIdFromUri(uri);
        long date = WeatherContract.HourlyForecastEntry.getDateFromUri(uri);

        if (locationId == 0 || date == 0) {
            return null;
        }

        String[] selectionArgs = new String[] {
                Long.toString(locationId),
                Long.toString(date),
        };

        String selection = sHourlyByLocationIdWithDateSelection;

        return this.query(
                sWeatherByLocationSettingQueryBuilder,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    public void close() {
        mOpenHelper.close();
    }

}
