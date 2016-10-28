package com.example.android.sunshine.app.sync;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.example.android.sunshine.app.BuildConfig;
import com.example.android.sunshine.app.util.WeatherDataParser;
import com.example.android.sunshine.app.data.model.WeatherModel;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by justinmitchell on 25/10/2016.
 */

public class FetchWeatherTask extends AsyncTask<String, Void, WeatherModel[]> {

    private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

    private final ArrayAdapter<String> mForecastAdapter;
    private final Context context;
    private final String unitType;

    private final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily";
    private final String QUERY_PARAM = "q";
    private final String FORMAT_PARAM = "mode";
    private final String UNITS_PARAM = "units";
    private final String DAYS_PARAM = "cnt";
    private final String API_KEY = "APPID";

    private final int numDays = 7;

    public FetchWeatherTask(ArrayAdapter<String> mForecastAdapter, String unitType, Context context) {
        this.mForecastAdapter = mForecastAdapter;
        this.context = context;
        this.unitType = unitType;
    }

    /**
     * Method to perform the fetch and update in the background thread
     * @param params
     * @return
     */
    protected WeatherModel[] doInBackground(String... params) {
        try {
            String forecastJsonStr = fetchRawJsonFromUrl(params);
            WeatherDataParser weatherParser = new WeatherDataParser(forecastJsonStr);
            return weatherParser.fetchWeatherData(numDays);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        // This will only happen if there was an error getting or parsing the forecast.
        return null;
    }

    @Override
    protected void onPostExecute(WeatherModel[] items) {
        if (items != null) {
            mForecastAdapter.clear();
            for (WeatherModel item : items) {
                mForecastAdapter.add(item.getFormattedString(unitType, context));
            }
            mForecastAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Helper method to fetch the raw JSON data from the Open Weather API
     * @param params
     * @return
     */
    protected String fetchRawJsonFromUrl(String... params) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        URL url;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are available at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            Uri uriBuilder = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, params[0])
                    .appendQueryParameter(FORMAT_PARAM, params[1])
                    .appendQueryParameter(UNITS_PARAM, params[2])
                    .appendQueryParameter(DAYS_PARAM, params[3])
                    .appendQueryParameter(API_KEY, BuildConfig.OPEN_WEATHER_MAP_API_KEY)
                    .build();

            url = new URL(uriBuilder.toString());

            Log.d(LOG_TAG, "API URL: " + uriBuilder.toString());


            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                forecastJsonStr = null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            // Stream was empty.  No point in parsing.
            if (buffer.length() == 0) {
                return null;
            }

            forecastJsonStr = buffer.toString();

            Log.d(LOG_TAG, "JSON Output: " + forecastJsonStr);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error: " + e.getMessage(), e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            forecastJsonStr = null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        return forecastJsonStr;
    }
}
