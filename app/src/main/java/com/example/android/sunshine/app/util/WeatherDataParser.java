package com.example.android.sunshine.app.util;

import android.text.format.Time;

import com.example.android.sunshine.app.data.model.LocationModel;
import com.example.android.sunshine.app.data.model.WeatherModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by justinmitchell on 25/10/2016.
 */

public class WeatherDataParser {

    private JSONObject forecastJson;

    public WeatherDataParser(String weatherJson) throws JSONException {
        parseJson(weatherJson);
    }

    public final void parseJson(String weatherJson) throws JSONException {
        forecastJson = new JSONObject(weatherJson);
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    public WeatherModel[] fetchWeatherData(int numDays) throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String OWM_LIST = "list";
        final String OWM_WEATHER = "weather";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_WEATHER_ID = "id";
        final String OWM_WEATHER_DESC = "main";
        final String OWM_TEMP_MIN = "min";
        final String OWM_TEMP_MAX = "max";
        final String OWM_PRESSURE = "pressure";
        final String OWM_HUMIDITY = "humidity";
        final String OWM_WIND_SPEED = "speed";
        final String OWM_WIND_DIR = "deg";

        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

        // OWM returns daily forecasts based upon the local time of the city that is being
        // asked for, which means that we need to know the GMT offset to translate this data
        // properly.

        // Since this data is also sent in-order and the first day is always the
        // current day, we're going to take advantage of that to get a nice
        // normalized UTC date for all of our weather.

        Time dayTime = new Time();
        dayTime.setToNow();

        // we start at the day returned by local time. Otherwise this is a mess.
        int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

        // now we work exclusively in UTC
        dayTime = new Time();

        WeatherModel[] weatherItems = new WeatherModel[numDays];
        for(int i = 0; i < weatherArray.length(); i++) {
            // initialize
            long dateTime;
            WeatherModel model = new WeatherModel();

            // Update the date
            dateTime = dayTime.setJulianDay(julianStartDay+i);
            model.setDate(dateTime);

            // Get the JSON object representing the day
            JSONObject dayForecast = weatherArray.getJSONObject(i);
            JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            JSONObject tempObject = dayForecast.getJSONObject(OWM_TEMPERATURE);

            // Update Weather Model
            model.setId(0);
            model.setLocationId(0);
            model.setWeatherId(weatherObject.getInt(OWM_WEATHER_ID));
            model.setShortDesc(weatherObject.getString(OWM_WEATHER_DESC));
            model.setMin(tempObject.getDouble(OWM_TEMP_MIN));
            model.setMax(tempObject.getDouble(OWM_TEMP_MAX));
            model.setHumidity(dayForecast.getDouble(OWM_HUMIDITY));
            model.setPressure(dayForecast.getDouble(OWM_PRESSURE));
            model.setWind(dayForecast.getDouble(OWM_WIND_SPEED));
            model.setDegrees(dayForecast.getInt(OWM_WIND_DIR));

            weatherItems[i] = model;
        }

        return weatherItems;
    }

    public LocationModel fetchLocationData(String locationSetting) throws JSONException {
        final String OWM_CITY = "city";
        final String OWM_COORD = "coord";

        final String OWM_ID = "id";
        final String OWM_CITY_NAME = "city_name";
        final String OWM_COORD_LAT = "lat";
        final String OWM_COORD_LON = "lon";

        JSONObject location = forecastJson.getJSONObject(OWM_CITY);
        JSONObject coords = location.getJSONObject(OWM_COORD);

        LocationModel model = new LocationModel();

        model.setId(location.getInt(OWM_ID));
        model.setCityName(location.getString(OWM_CITY_NAME));
        model.setCoordLat(coords.getDouble(OWM_COORD_LAT));
        model.setCoordLong(coords.getDouble(OWM_COORD_LON));
        model.setLocationSetting(locationSetting);

        return model;
    }

    /* The date/time conversion code is going to be moved outside the asynctask later,
     * so for convenience we're breaking it out into its own method now.
     */
    private String getReadableDateString(long time){
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("E, MMM d");
        return format.format(date).toString();
    }
}
