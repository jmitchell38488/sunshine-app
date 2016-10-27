package com.example.android.sunshine.app.util;

import com.example.android.sunshine.app.R;

import android.content.Context;
import android.util.Log;

/**
 * Created by justinmitchell on 25/10/2016.
 */

public class WeatherModel {

    private final String LOG_TAG = WeatherModel.class.getSimpleName();

    protected String day;
    protected String description;
    protected double tempLow;
    protected double tempHigh;

    public WeatherModel(String day, String description, double tempLow, double tempHigh) {
        this.day = day;
        this.description = description;
        this.tempLow = tempLow;
        this.tempHigh = tempHigh;
    }

    public String getDay() {
        return day;
    }

    public String getDescription() {
        return description;
    }

    public double getTempLow() {
        return tempLow;
    }

    public double getTempHigh() {
        return tempHigh;
    }

    public String getFormattedString(String unitType, Context context) {
        String highAndLow = formatHighLows(unitType, context);
        return day + " - " + description + " - " + highAndLow;
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(String unitType, Context context) {
        double high = tempHigh, low = tempLow;
        String unitStr = "c";

        if (unitType.equals(context.getString(R.string.pref_units_imperial))) {
            high = (tempHigh * 1.8) + 32;
            low = (tempLow * 1.8) + 32;
            unitStr = "f";
        } else if (!unitType.equals(context.getString(R.string.pref_units_metric))) {
            Log.d(LOG_TAG, "Unit type not found: " + unitType);
            unitStr = "";
        }

        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        return roundedHigh + unitStr + "/" + roundedLow + unitStr;
    }
}
