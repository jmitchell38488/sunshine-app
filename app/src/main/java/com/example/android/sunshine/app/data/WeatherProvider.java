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
    static final int WEATHER_WITH_LOCATION_ID = 101;
    static final int WEATHER_WITH_LOCATION_ID_AND_DATE = 102;
    static final int WEATHER_WITH_LOCATION_ID_TODAY = 103;
    static final int LOCATION = 300;
    static final int CURRENT = 400;
    static final int CURRENT_WITH_LOCATION_ID = 401;
    static final int HOURLY = 500;
    static final int HOURLY_WITH_ID = 501;
    static final int HOURLY_WITH_LOCATION_ID = 502;
    static final int HOURLY_WITH_LOCATION_ID_AND_DATE = 503;

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

            // "weather/#/today"
            case WEATHER_WITH_LOCATION_ID_TODAY:
                retCursor = weatherStorage.getWeatherTodayByLocationId(uri, projection, sortOrder);
                break;

            // weather/#/#
            case WEATHER_WITH_LOCATION_ID_AND_DATE:
                retCursor = weatherStorage.getWeatherByLocationIdAndDate(uri, projection, sortOrder);
                break;

            // weather/#
            case WEATHER_WITH_LOCATION_ID:
                retCursor = weatherStorage.getWeatherByLocationId(uri, projection, sortOrder);
                break;

            // "weather/"
            case WEATHER:
                retCursor = weatherStorage.getWeather(projection, selection, selectionArgs, sortOrder);
                break;

            // "location/"
            case LOCATION:
                retCursor = weatherStorage.getLocation(projection, selection, selectionArgs, sortOrder);
                break;

            // "current/"
            case CURRENT:
                retCursor = weatherStorage.getCurrentConditions(projection, selection, selectionArgs, sortOrder);
                break;

            // "current/#"
            case CURRENT_WITH_LOCATION_ID:
                retCursor = weatherStorage.getCurrentConditionsWithLocationId(uri, projection, sortOrder);
                break;

            // "hourly/"
            case HOURLY:
                retCursor = weatherStorage.getHourly(projection, selection, selectionArgs, sortOrder);
                break;

            // "hourly/#"
            case HOURLY_WITH_ID:
                retCursor = weatherStorage.getCurrentConditionsWithLocationId(uri, projection, sortOrder);
                break;

            // "hourly/location/#"
            case HOURLY_WITH_LOCATION_ID:
                retCursor = weatherStorage.getHourlyByLocationId(uri, projection, sortOrder);
                break;

            // "hourly/location/#/#"
            case HOURLY_WITH_LOCATION_ID_AND_DATE:
                retCursor = weatherStorage.getHourlyByLocationIdAndDate(uri, projection, sortOrder);
                break;

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

            case CURRENT: {
                long id = weatherStorage.insert(WeatherContract.CurrentConditionsEntry.TABLE_NAME, contentValues);

                if (id > 0) {
                    returnUri = WeatherContract.CurrentConditionsEntry.buildCurrentConditionsUri(id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }

                break;
            }

            case HOURLY: {
                long id = weatherStorage.insert(WeatherContract.HourlyForecastEntry.TABLE_NAME, contentValues);

                if (id > 0) {
                    returnUri = WeatherContract.HourlyForecastEntry.buildHourlyForecastUri(id);
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

            case CURRENT:
                rowsDeleted = weatherStorage.delete(WeatherContract.CurrentConditionsEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case HOURLY:
                rowsDeleted = weatherStorage.delete(WeatherContract.HourlyForecastEntry.TABLE_NAME, selection, selectionArgs);
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
                rowsUpdated = weatherStorage.update(WeatherContract.WeatherEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            case LOCATION:
                rowsUpdated = weatherStorage.update(WeatherContract.LocationEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            case CURRENT:
                rowsUpdated = weatherStorage.update(WeatherContract.CurrentConditionsEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            case HOURLY:
                rowsUpdated = weatherStorage.update(WeatherContract.HourlyForecastEntry.TABLE_NAME, values, selection, selectionArgs);
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
        int returnCount = 0;

        switch (match) {
            case WEATHER:
                returnCount = weatherStorage.bulkInsert(WeatherContract.WeatherEntry.TABLE_NAME, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            case CURRENT:
                returnCount = weatherStorage.bulkInsert(WeatherContract.CurrentConditionsEntry.TABLE_NAME, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            case HOURLY:
                returnCount = weatherStorage.bulkInsert(WeatherContract.HourlyForecastEntry.TABLE_NAME, values);
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
            case WEATHER_WITH_LOCATION_ID_AND_DATE:
            case WEATHER_WITH_LOCATION_ID_TODAY:
                return WeatherContract.WeatherEntry.CONTENT_ITEM_TYPE;

            case WEATHER:
            case WEATHER_WITH_LOCATION_ID:
                return WeatherContract.WeatherEntry.CONTENT_TYPE;

            case LOCATION:
                return WeatherContract.LocationEntry.CONTENT_TYPE;

            case CURRENT:
                return WeatherContract.CurrentConditionsEntry.CONTENT_TYPE;

            case CURRENT_WITH_LOCATION_ID:
                return WeatherContract.CurrentConditionsEntry.CONTENT_ITEM_TYPE;

            case HOURLY:
            case HOURLY_WITH_LOCATION_ID:
                return WeatherContract.HourlyForecastEntry.CONTENT_TYPE;

            case HOURLY_WITH_ID:
            case HOURLY_WITH_LOCATION_ID_AND_DATE:
                return WeatherContract.HourlyForecastEntry.CONTENT_ITEM_TYPE;

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

        // Match weather with any string (eg /weather/3059,au)
        matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/*", WEATHER_WITH_LOCATION_ID);

        // Match weather with location id and today (eg /weather/1/today)
        matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/#/today", WEATHER_WITH_LOCATION_ID_TODAY);

        // Match location (eg /location)
        matcher.addURI(authority, WeatherContract.PATH_LOCATION, LOCATION);

        // Match current conditions (eg /current)
        matcher.addURI(authority, WeatherContract.PATH_CURRENT, CURRENT);

        // Match current conditions with location id (/current/1)
        matcher.addURI(authority, WeatherContract.PATH_CURRENT + "/#", CURRENT_WITH_LOCATION_ID);

        // Match hourly conditions (/hourly)
        matcher.addURI(authority, WeatherContract.PATH_HOURLY, HOURLY);

        // Match hourly conditions (/hourly/1)
        matcher.addURI(authority, WeatherContract.PATH_HOURLY + "/#", HOURLY_WITH_ID);

        // Match hourly conditions with location id (/hourly/location/1)
        matcher.addURI(authority, WeatherContract.PATH_HOURLY + "/location/#", HOURLY_WITH_LOCATION_ID);

        // Match hourly conditions with location id and date (/hourly/location/1/12345)
        matcher.addURI(authority, WeatherContract.PATH_HOURLY + "/location/#/#", HOURLY_WITH_LOCATION_ID_AND_DATE);

        return matcher;
    }

}
