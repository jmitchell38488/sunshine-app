package com.example.android.sunshine.app.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.example.android.sunshine.app.data.storage.IStorage;
import com.example.android.sunshine.app.data.storage.WeatherStorageFactory;

/**
 * Created by justinmitchell on 26/10/2016.
 */

public class WeatherProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private IStorage weatherStorage;

    static final int WEATHER = 100;
    static final int WEATHER_WITH_LOCATION = 101;
    static final int WEATHER_WITH_LOCATION_AND_DATE = 102;
    static final int LOCATION = 300;

    @Override
    public boolean onCreate() {
        weatherStorage = WeatherStorageFactory.getDataStorageAccessor(getContext());
        return true;
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        weatherStorage.close();
        super.shutdown();
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {

            // "weather/*/*"
            case WEATHER_WITH_LOCATION_AND_DATE: {
                retCursor = weatherStorage.getWeatherByLocationSettingAndDate(uri, projection, sortOrder);
                break;
            }

            // "weather/*"
            case WEATHER_WITH_LOCATION: {
                retCursor = weatherStorage.getWeatherByLocationSetting(uri, projection, sortOrder);
                break;
            }

            // "weather"
            case WEATHER: {
                retCursor = weatherStorage.getWeather(projection, selection, selectionArgs, sortOrder);
                break;
            }

            // "location"
            case LOCATION: {
                retCursor = weatherStorage.getWeather(projection, selection, selectionArgs, sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case WEATHER: {
                normalizeDate(contentValues);
                long id = weatherStorage.insert(WeatherContract.WeatherEntry.TABLE_NAME, contentValues);

                if (id > 0) {
                    returnUri = WeatherContract.WeatherEntry.buildWeatherUri(id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }

                break;
            }

            case LOCATION: {
                long id = weatherStorage.insert(WeatherContract.LocationEntry.TABLE_NAME, contentValues);

                if (id > 0) {
                    returnUri = WeatherContract.LocationEntry.buildLocationUri(id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }

                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        // this makes delete all rows return the number of rows deleted
        if (null == selection) {
            selection = "1";
        }

        switch (match) {
            case WEATHER:
                rowsDeleted = weatherStorage.delete(WeatherContract.WeatherEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case LOCATION:
                rowsDeleted = weatherStorage.delete(WeatherContract.LocationEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case WEATHER:
                normalizeDate(values);
                rowsUpdated = weatherStorage.update(WeatherContract.WeatherEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            case LOCATION:
                rowsUpdated = weatherStorage.update(WeatherContract.LocationEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case WEATHER:
                for (ContentValues recordSet : values) {
                    normalizeDate(recordSet);
                }

                int returnCount = weatherStorage.bulkInsert(WeatherContract.WeatherEntry.TABLE_NAME, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    /**
     * This method is responsible to matching an incoming URI with the URI matches that we've
     * already set up in buildUriMatcher. The method will return an integer value corresponding with
     * the valid route type that has been requested. If none is found then an exception is thrown
     *
     * @param uri The request URI
     * @return The valid corresponding route type
     * @throws UnsupportedOperationException When there's no matching URI type
     */
    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case WEATHER_WITH_LOCATION_AND_DATE:
                return WeatherContract.WeatherEntry.CONTENT_ITEM_TYPE;
            case WEATHER_WITH_LOCATION:
                return WeatherContract.WeatherEntry.CONTENT_TYPE;
            case WEATHER:
                return WeatherContract.WeatherEntry.CONTENT_TYPE;
            case LOCATION:
                return WeatherContract.LocationEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    /**
     * This method is responsible for constructing a list of valid URIs that the application can
     * handle. This is the basic internal/external routing list.
     * # corresponds to a number
     * * corresponds to a string
     *
     * @return UriMatcher The list of valid routes
     */
    static UriMatcher buildUriMatcher() {
        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = WeatherContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, WeatherContract.PATH_WEATHER, WEATHER);
        matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/*", WEATHER_WITH_LOCATION);
        matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/*/#", WEATHER_WITH_LOCATION_AND_DATE);

        matcher.addURI(authority, WeatherContract.PATH_LOCATION, LOCATION);
        return matcher;
    }


    private void normalizeDate(ContentValues values) {
        // normalize the date value
        if (values.containsKey(WeatherContract.WeatherEntry.COLUMN_DATE)) {
            long dateValue = values.getAsLong(WeatherContract.WeatherEntry.COLUMN_DATE);
            values.put(WeatherContract.WeatherEntry.COLUMN_DATE, WeatherContract.normalizeDate(dateValue));
        }
    }

}
