package com.example.android.sunshine.app.data.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.example.android.sunshine.app.data.WeatherContract;
import com.example.android.sunshine.app.data.WeatherDbHelper;

/**
 * Created by justinmitchell on 27/10/2016.
 */

public class WeatherSqlite implements IStorage {

    private WeatherDbHelper mOpenHelper;

    private static final SQLiteQueryBuilder sWeatherByLocationSettingQueryBuilder;

    static {
        sWeatherByLocationSettingQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sWeatherByLocationSettingQueryBuilder.setTables(
                WeatherContract.WeatherEntry.TABLE_NAME + " INNER JOIN " +
                        WeatherContract.LocationEntry.TABLE_NAME +
                        " ON " + WeatherContract.WeatherEntry.TABLE_NAME +
                        "." + WeatherContract.WeatherEntry.COLUMN_LOC_KEY +
                        " = " + WeatherContract.LocationEntry.TABLE_NAME +
                        "." + WeatherContract.LocationEntry._ID);
    }

    private static final String sLocationSettingSelection =
            WeatherContract.LocationEntry.TABLE_NAME+
                    "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? ";

    private static final String sLocationSettingWithStartDateSelection =
            WeatherContract.LocationEntry.TABLE_NAME+
                    "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
                    WeatherContract.WeatherEntry.COLUMN_DATE + " >= ? ";


    private static final String sLocationSettingAndDaySelection =
            WeatherContract.LocationEntry.TABLE_NAME +
                    "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
                    WeatherContract.WeatherEntry.COLUMN_DATE + " = ? ";

    public Cursor query(String[] projectionIn, String selection, String[] selectionArgs) {
        return this.query(
                projectionIn,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
    }

    public Cursor query(String[] projectionIn, String selection, String[] selectionArgs, String groupBy, String having, String sortOrder) {
        return sWeatherByLocationSettingQueryBuilder.query(
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

    public Cursor getWeatherByLocationSetting(Uri uri, String[] projection, String sortOrder) {
        String locationSetting = WeatherContract.WeatherEntry.getLocationSettingFromUri(uri);
        long startDate = WeatherContract.WeatherEntry.getStartDateFromUri(uri);

        String[] selectionArgs;
        String selection;

        if (startDate == 0) {
            selection = sLocationSettingSelection;
            selectionArgs = new String[]{locationSetting};
        } else {
            selectionArgs = new String[]{locationSetting, Long.toString(startDate)};
            selection = sLocationSettingWithStartDateSelection;
        }

        return this.query(
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    public Cursor getWeatherByLocationSettingAndDate(Uri uri, String[] projection, String sortOrder) {
        String locationSetting = WeatherContract.WeatherEntry.getLocationSettingFromUri(uri);
        long date = WeatherContract.WeatherEntry.getDateFromUri(uri);

        return this.query(
                projection,
                sLocationSettingAndDaySelection,
                new String[]{locationSetting, Long.toString(date)},
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

    public void close() {
        mOpenHelper.close();
    }

}
