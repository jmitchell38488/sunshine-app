package com.example.android.sunshine.app.data.model;

import android.content.ContentValues;

import com.example.android.sunshine.app.data.WeatherContract;

/**
 * Created by justinmitchell on 28/10/2016.
 */

public class LocationModel {

    private final String LOG_TAG = LocationModel.class.getSimpleName();

    // _id
    private long id;

    // location_setting
    private String locationSetting;

    // city_name
    private String cityName;

    // coord_lat
    private double coordLat;

    // coord_long
    private double coordLong;

    public LocationModel() {
        // Do nothing
    }

    public LocationModel(long id, String locationSetting, String cityName, double coordLat, double coordLong) {
        this.id = id;
        this.locationSetting = locationSetting;
        this.cityName = cityName;
        this.coordLat = coordLat;
        this.coordLong = coordLong;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLocationSetting() {
        return locationSetting;
    }

    public void setLocationSetting(String locationSetting) {
        this.locationSetting = locationSetting;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public double getCoordLat() {
        return coordLat;
    }

    public void setCoordLat(double coordLat) {
        this.coordLat = coordLat;
    }

    public double getCoordLong() {
        return coordLong;
    }

    public void setCoordLong(double coordLong) {
        this.coordLong = coordLong;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();

        values.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, locationSetting);
        values.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, cityName);
        values.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT, coordLat);
        values.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG, coordLong);

        return values;
    }
}
