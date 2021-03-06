package com.example.android.sunshine.app.data.storage;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.example.android.sunshine.app.data.WeatherContract;

/**
 * Created by justinmitchell on 28/10/2016.
 */

public interface IStorage {

    public Cursor getWeatherByLocationSetting(Uri uri, String[] projection, String sortOrder);

    public Cursor getWeatherByLocationSettingAndDate(Uri uri, String[] projection, String sortOrder);

    public Cursor getWeather(String[] projection, String selection, String[] selectionArgs, String sortOrder);

    public Cursor getLocation(String[] projection, String selection, String[] selectionArgs, String sortOrder);

    public Cursor getCurrentConditions(String[] projection, String selection, String[] selectionArgs, String sortOrder);

    public Cursor getCurrentConditionsWithLocationId(Uri uri, String[] projection, String sortOrder);

    public StorageType getType();

    public long insert(String tableName, ContentValues contentValues);

    public int delete(String tableName, String selection, String[] selectionArgs);

    public int update(String tableName, ContentValues values, String selection, String[] selectionArgs);

    public int bulkInsert(String tableName, ContentValues[] values);

    public void close();

    public enum StorageType {
        SQLITE, XMLFILE, JSONFILE, TXTFILE
    }

}
