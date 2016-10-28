package com.example.android.sunshine.app.data.model;

/**
 * Created by justinmitchell on 28/10/2016.
 */

public class LocationModel {

    private final String LOG_TAG = LocationModel.class.getSimpleName();

    // _id
    private int id;

    // location_setting
    private String locationSetting;

    // city_name
    private String cityName;

    // coord_lat
    private long coordLat;

    // coord_long
    private long coordLong;

    public LocationModel() {
        // Do nothing
    }

    public LocationModel(int id, String locationSetting, String cityName, long coordLat, long coordLong) {
        this.id = id;
        this.locationSetting = locationSetting;
        this.cityName = cityName;
        this.coordLat = coordLat;
        this.coordLong = coordLong;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public long getCoordLat() {
        return coordLat;
    }

    public void setCoordLat(long coordLat) {
        this.coordLat = coordLat;
    }

    public long getCoordLong() {
        return coordLong;
    }

    public void setCoordLong(long coordLong) {
        this.coordLong = coordLong;
    }
}
