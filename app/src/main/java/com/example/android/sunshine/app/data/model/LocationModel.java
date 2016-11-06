package com.example.android.sunshine.app.data.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.android.sunshine.app.data.WeatherContract;

/**
 * Created by justinmitchell on 28/10/2016.
 */

public class LocationModel {

    private final String LOG_TAG = LocationModel.class.getSimpleName();

    // _id
    private long id;

    // location_id
    private long locationId;

    // city_name
    private String cityName;

    // country_name
    private String countryName;

    // coord_lat
    private double coordLat;

    // coord_long
    private double coordLon;

    public LocationModel() {
        // Do nothing
    }

    public LocationModel(long id, long locationId, String cityName, String countryName, double coordLat, double coordLon) {
        this.id = id;
        this.locationId = locationId;
        this.cityName = cityName;
        this.countryName = countryName;
        this.coordLat = coordLat;
        this.coordLon = coordLon;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getLocationId() {
        return locationId;
    }

    public void setLocationId(long locationId) {
        this.locationId = locationId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public double getCoordLat() {
        return coordLat;
    }

    public void setCoordLat(double coordLat) {
        this.coordLat = coordLat;
    }

    public double getCoordLon() {
        return coordLon;
    }

    public void setCoordLon(double coordLon) {
        this.coordLon = coordLon;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();

        values.put(WeatherContract.LocationEntry.COLUMN_LOCATION_ID, locationId);
        values.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, cityName);
        values.put(WeatherContract.LocationEntry.COLUMN_COUNTRY_NAME, countryName);
        values.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT, coordLat);
        values.put(WeatherContract.LocationEntry.COLUMN_COORD_LON, coordLon);

        return values;
    }

    public void loadFromCursor(Cursor cursor) {
        int _id = cursor.getColumnIndex(WeatherContract.LocationEntry.COLUMN_ID);
        int _locationId = cursor.getColumnIndex(WeatherContract.LocationEntry.COLUMN_LOCATION_ID);
        int _cityName = cursor.getColumnIndex(WeatherContract.LocationEntry.COLUMN_CITY_NAME);
        int _countryName = cursor.getColumnIndex(WeatherContract.LocationEntry.COLUMN_COUNTRY_NAME);
        int _coordLat = cursor.getColumnIndex(WeatherContract.LocationEntry.COLUMN_COORD_LAT);
        int _coordLon = cursor.getColumnIndex(WeatherContract.LocationEntry.COLUMN_COORD_LON);

        id = cursor.getLong(_id);
        locationId = cursor.getLong(_locationId);
        cityName = cursor.getString(_cityName);
        countryName = cursor.getString(_countryName);
        coordLat = cursor.getDouble(_coordLat);
        coordLon = cursor.getDouble(_coordLon);
    }
}
