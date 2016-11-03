package com.example.android.sunshine.app.util;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.android.sunshine.app.MainActivity;
import com.example.android.sunshine.app.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by justinmitchell on 29/10/2016.
 */

public class Utility {

    private static final String LOG_TAG = Utility.class.getSimpleName();

    public static final int PERMISSIONS_ALL = 1000;
    public static final int PERMISSIONS_REQUEST_GPS = 1001;
    public static final int PERMISSIONS_ACCESS_NETWORK_STATE = 1002;

    public static void showToast(Activity activity, String message, int duration) {
        Toast toast = Toast.makeText(activity, message, duration);
        toast.show();
    }

    /**
     * Helper method to provide the icon resource id according to the weather condition id returned
     * by the OpenWeatherMap call.
     * @param weatherId from OpenWeatherMap API response
     * @return resource id for the corresponding icon. -1 if no relation is found.
     */
    public static int getIconResourceForWeatherCondition(int weatherId) {
        // Based on weather code data found at:
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
        if (weatherId >= 200 && weatherId <= 232) {
            return R.drawable.ic_storm;
        } else if (weatherId >= 300 && weatherId <= 321) {
            return R.drawable.ic_light_rain;
        } else if (weatherId >= 500 && weatherId <= 504) {
            return R.drawable.ic_rain;
        } else if (weatherId == 511) {
            return R.drawable.ic_snow;
        } else if (weatherId >= 520 && weatherId <= 531) {
            return R.drawable.ic_rain;
        } else if (weatherId >= 600 && weatherId <= 622) {
            return R.drawable.ic_snow;
        } else if (weatherId >= 701 && weatherId <= 761) {
            return R.drawable.ic_fog;
        } else if (weatherId == 761 || weatherId == 781) {
            return R.drawable.ic_storm;
        } else if (weatherId == 800) {
            return R.drawable.ic_clear;
        } else if (weatherId == 801) {
            return R.drawable.ic_light_clouds;
        } else if (weatherId >= 802 && weatherId <= 804) {
            return R.drawable.ic_cloudy;
        }
        return -1;
    }

    /**
     * Helper method to provide the art resource id according to the weather condition id returned
     * by the OpenWeatherMap call.
     * @param weatherId from OpenWeatherMap API response
     * @return resource id for the corresponding image. -1 if no relation is found.
     */
    public static int getArtResourceForWeatherCondition(int weatherId) {
        // Based on weather code data found at:
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
        if (weatherId >= 200 && weatherId <= 232) {
            return R.drawable.art_storm;
        } else if (weatherId >= 300 && weatherId <= 321) {
            return R.drawable.art_light_rain;
        } else if (weatherId >= 500 && weatherId <= 504) {
            return R.drawable.art_rain;
        } else if (weatherId == 511) {
            return R.drawable.art_snow;
        } else if (weatherId >= 520 && weatherId <= 531) {
            return R.drawable.art_rain;
        } else if (weatherId >= 600 && weatherId <= 622) {
            return R.drawable.art_rain;
        } else if (weatherId >= 701 && weatherId <= 761) {
            return R.drawable.art_fog;
        } else if (weatherId == 761 || weatherId == 781) {
            return R.drawable.art_storm;
        } else if (weatherId == 800) {
            return R.drawable.art_clear;
        } else if (weatherId == 801) {
            return R.drawable.art_light_clouds;
        } else if (weatherId >= 802 && weatherId <= 804) {
            return R.drawable.art_clouds;
        }
        return -1;
    }



    /**
     * Method to fetch the best last location from either the GPS or Network Provider
     * See: http://stackoverflow.com/questions/1513485/how-do-i-get-the-current-gps-location-programmatically-in-android
     *
     * @return the last know best location
     */
    public static Location getLastBestLocation(Context context) throws SecurityException {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(LOG_TAG, "Cannot fetch best last position, permission not granted");
            return null;
        }

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long GPSLocationTime = 0;
        if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if (0 < GPSLocationTime - NetLocationTime) {
            return locationGPS;
        } else {
            return locationNet;
        }
    }

    public static void checkLocationPermissions(Activity activity) {
        // Here, thisActivity is the current activity
        if (ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.d(LOG_TAG, "Rationale required for Manifest.permission.ACCESS_FINE_LOCATION");
            } else {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_GPS);
            }
        }
    }

    public static void checkNetworkPermissions(Activity activity) {
        // Here, thisActivity is the current activity
        if (ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.ACCESS_NETWORK_STATE)) {
                Log.d(LOG_TAG, "Rationale required for Manifest.permission.ACCESS_FINE_LOCATION");
            } else {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                        PERMISSIONS_ACCESS_NETWORK_STATE);
            }
        }
    }

    public static void checkRequiredPermissions(Activity activity) {
        // Here, thisActivity is the current activity
        if ((ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) ||
                (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)){

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_NETWORK_STATE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.d(LOG_TAG, "Rationale required");
            } else {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_ALL);
            }
        }
    }

    /**
     * Helper method to determine if the users' device is currently connected to either the cell
     * network or to a wifi network
     *
     * @param activity
     * @return
     */
    public static boolean checkNetworkConnectivity(Activity activity) {
        ConnectivityManager cm =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null) {
            return false;
        }

        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;

        return isConnected || isWiFi;
    }

    /**
     * Helper method to determine if the users' device is currently connected to a WiFi network.
     * The active network can either be cell or wifi, but by ensuring that the wifi component
     * is required in the return ensures that this returns true only when the user is connected
     * to a wifi network. This still doesn't guarantee network traffic.
     *
     * @param activity
     * @return
     */
    public static boolean checkWiFiConnectivity(Activity activity) {
        ConnectivityManager cm =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null) {
            return false;
        }

        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;

        return isConnected && isWiFi;
    }

    public static void hideStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT < 16) {
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = activity.getWindow().getDecorView();
            // Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
            // Remember that you should never show the action bar if the
            // status bar is hidden, so hide that too if necessary.
            ActionBar actionBar = activity.getActionBar();

            if (actionBar != null) {
                actionBar.hide();
            }
        }
    }

    /**
     * Helper method to return the midnight time for today. This is used to compare to the UTC
     * stored dates for the weather forecasts.
     * http://stackoverflow.com/a/6850919/1740059
     *
     * @return the unix timestamp in milliseconds for today at midnight
     */
    public static long getMidnightTimeToday() {
        Calendar date = new GregorianCalendar(TimeZone.getTimeZone("UTC"));

        // reset hour, minutes, seconds and millis
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        // next day
        date.add(Calendar.DAY_OF_MONTH, 1);

        return date.getTimeInMillis();
    }

    public static double getConvertedTemperature(double temperature, boolean isMetric) {
        double temp;
        if (!isMetric) {
            temp = 9 * temperature / 5 + 32;
        } else {
            temp = temperature;
        }

        return temp;
    }

    public static String getWindCompassDirections(double windDirection) {
        // From wind direction in degrees, determine compass direction as a string (e.g NW)
        // You know what's fun, writing really long if/else statements with tons of possible
        // conditions.  Seriously, try it!
        String direction = "Unknown";

        if (windDirection >= 337.5 || windDirection < 22.5) {
            direction = "N";
        } else if (windDirection >= 22.5 && windDirection < 67.5) {
            direction = "NE";
        } else if (windDirection >= 67.5 && windDirection < 112.5) {
            direction = "E";
        } else if (windDirection >= 112.5 && windDirection < 157.5) {
            direction = "SE";
        } else if (windDirection >= 157.5 && windDirection < 202.5) {
            direction = "S";
        } else if (windDirection >= 202.5 && windDirection < 247.5) {
            direction = "SW";
        } else if (windDirection >= 247.5 && windDirection < 292.5) {
            direction = "W";
        } else if (windDirection >= 292.5 && windDirection < 337.5) {
            direction = "NW";
        }

        return direction;
    }

    public static double getConvertedWindSpeed(double windSpeed, boolean isMetric) {
        return isMetric ? windSpeed : .621371192237334f * windSpeed;
    }

    /**
     * Helper method to capitalize each word in a sentence. This is similar to ucwords in php
     * http://stackoverflow.com/a/14848842/1740059
     * @param string
     * @return
     */
    public static String capitalize(String string) {
        String[] arr = string.split(" ");
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < arr.length; i++) {
            sb.append(Character.toUpperCase(arr[i].charAt(0)))
                    .append(arr[i].substring(1)).append(" ");
        }
        return sb.toString().trim();
    }

}
