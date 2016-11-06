package com.example.android.sunshine.app.util;

import android.text.format.Time;

import com.example.android.sunshine.app.data.model.CurrentConditionsModel;
import com.example.android.sunshine.app.data.model.LocationModel;
import com.example.android.sunshine.app.data.model.WeatherModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    public WeatherModel[] convertWeatherData() throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String OWM_LIST = "list";
        final String OWM_DATE = "dt";
        final String OWM_WEATHER = "weather";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_WEATHER_ID = "id";
        final String OWM_WEATHER_DESC = "description";
        final String OWM_TEMP_MIN = "min";
        final String OWM_TEMP_MAX = "max";
        final String OWM_PRESSURE = "pressure";
        final String OWM_HUMIDITY = "humidity";
        final String OWM_WIND_SPEED = "speed";
        final String OWM_WIND_DIR = "deg";

        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

        WeatherModel[] weatherItems = new WeatherModel[weatherArray.length()];
        for(int i = 0; i < weatherArray.length(); i++) {
            // initialize
            WeatherModel model = new WeatherModel();

            // Get the JSON object representing the day
            JSONObject dayForecast = weatherArray.getJSONObject(i);
            JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            JSONObject tempObject = dayForecast.getJSONObject(OWM_TEMPERATURE);

            // Dates from Open Weather Map are UTC/GMT 02:00:00, so need to subtract 2 hours from the
            // times when fetching daily forecast data
            long dateTime = dayForecast.getLong(OWM_DATE);

            // Dates are UTC, but offset by 2 hours
            dateTime -= 60*60*2;
            // Convert to milliseconds
            dateTime *= 1000;

            // Update Weather Model
            model.setId(0);
            model.setDateTime(dateTime);
            model.setLocationId(0);
            model.setWeatherId(weatherObject.getInt(OWM_WEATHER_ID));
            model.setDescription(weatherObject.getString(OWM_WEATHER_DESC));
            model.setLow(tempObject.getDouble(OWM_TEMP_MIN));
            model.setHigh(tempObject.getDouble(OWM_TEMP_MAX));
            model.setHumidity(dayForecast.getDouble(OWM_HUMIDITY));
            model.setPressure(dayForecast.getDouble(OWM_PRESSURE));
            model.setWindSpeed(dayForecast.getDouble(OWM_WIND_SPEED));
            model.setWindDirection(dayForecast.getInt(OWM_WIND_DIR));

            weatherItems[i] = model;
        }

        return weatherItems;
    }

    public LocationModel convertLocationData(String locationSetting) throws JSONException {
        final String OWM_CITY = "city";
        final String OWM_COUNTRY = "country";
        final String OWM_COORD = "coord";

        final String OWM_ID = "id";
        final String OWM_CITY_NAME = "name";
        final String OWM_COORD_LAT = "lat";
        final String OWM_COORD_LON = "lon";

        JSONObject location = forecastJson.getJSONObject(OWM_CITY);
        JSONObject coords = location.getJSONObject(OWM_COORD);

        LocationModel model = new LocationModel();

        model.setLocationId(location.getLong(OWM_ID));
        model.setCityName(location.getString(OWM_CITY_NAME));
        model.setCountryName(location.getString(OWM_COUNTRY));
        model.setCoordLat(coords.getDouble(OWM_COORD_LAT));
        model.setCoordLon(coords.getDouble(OWM_COORD_LON));

        return model;
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    public CurrentConditionsModel convertCurrentConditionsData() throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String OWM_WEATHER = "weather";
        final String OWM_MAIN = "main";
        final String OWM_WIND = "wind";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_WEATHER_ID = "id";
        final String OWM_WEATHER_DESC = "description";
        final String OWM_TEMP_MIN = "temp_min";
        final String OWM_TEMP_MAX = "temp_max";
        final String OWM_PRESSURE = "pressure";
        final String OWM_HUMIDITY = "humidity";
        final String OWM_WIND_SPEED = "speed";
        final String OWM_WIND_DIR = "deg";

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

        CurrentConditionsModel currentModel = new CurrentConditionsModel();

        currentModel.setDateTime(julianStartDay);

        // Get the JSON object representing the day
        JSONObject weatherObj = (JSONObject) forecastJson.getJSONArray(OWM_WEATHER).get(0);
        JSONObject mainObj = forecastJson.getJSONObject(OWM_MAIN);
        JSONObject windObj = forecastJson.getJSONObject(OWM_WIND);

        // Update Current Conditions Model
        currentModel.setId(0);
        currentModel.setLocationId(0);
        currentModel.setWeatherId(weatherObj.getInt(OWM_WEATHER_ID));
        currentModel.setDescription(weatherObj.getString(OWM_WEATHER_DESC));
        currentModel.setTemp(mainObj.getDouble(OWM_TEMPERATURE));
        currentModel.setLow(mainObj.getDouble(OWM_TEMP_MIN));
        currentModel.setHigh(mainObj.getDouble(OWM_TEMP_MAX));
        currentModel.setHumidity(mainObj.getDouble(OWM_HUMIDITY));
        currentModel.setPressure(mainObj.getDouble(OWM_PRESSURE));
        currentModel.setWindSpeed(windObj.getDouble(OWM_WIND_SPEED));
        currentModel.setWindDirection(windObj.getInt(OWM_WIND_DIR));

        return currentModel;
    }

}
