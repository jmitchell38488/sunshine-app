package com.example.android.sunshine.app.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sunshine.app.R;
import com.example.android.sunshine.app.SettingsActivity;
import com.example.android.sunshine.app.data.WeatherContract;
import com.example.android.sunshine.app.data.model.LocationModel;
import com.example.android.sunshine.app.sync.SunshineSyncAdapter;
import com.example.android.sunshine.app.util.Preferences;
import com.example.android.sunshine.app.util.Utility;
import com.example.android.sunshine.app.view.TodayViewHolder;

/**
 * Created by justinmitchell on 1/11/2016.
 */
public class TodayFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = TodayFragment.class.getSimpleName();
    private static final int TODAY_LOADER = 50;

    private BroadcastReceiver mBroadcastReceiver;
    private SwipeRefreshLayout mSwipeRefresh;

    private Uri mUri;

    public TodayFragment() {
        // do nothing
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
        Utility.hideStatusBar(getActivity());

        long lastLocationId = Preferences.getLastUsedLocation(getActivity());

        // We haven't initialized a location yet, freshly installed?
        if (lastLocationId == 0) {
            Utility.getCurrentLocation(getActivity(), Preferences.getUseGpsForLocation(getActivity()));

        }

        if (lastLocationId > 0) {
            mUri = WeatherContract.WeatherEntry.buildWeatherTodayWithLocationId(lastLocationId);
            Log.d(LOG_TAG, mUri.toString());

            long todayDateTime = Preferences.getTodayDateTime(getActivity());
            if (todayDateTime == 0) {
                Preferences.setTodayDateTime(getActivity(), Utility.getMidnightTimeToday());
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.today, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            /*case R.id.action_refresh:
                Log.d(LOG_TAG, "Triggering action {action_refresh}");
                updateWeather();
                return true;

            case R.id.action_map:
                Log.d(LOG_TAG, "Triggering intent {Maps} to retrieve preferred location");
                openPreferredLocationInMap();
                break;*/
            case R.id.action_settings:
                Log.d(LOG_TAG, "Triggering intent {SettingsActivity}");
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openPreferredLocationInMap() {
        long lastUsedLocation = Preferences.getLastUsedLocation(getActivity());
        String mapError = getActivity().getString(R.string.pref_last_used_location_error);
        int toastDuration = Toast.LENGTH_SHORT;

        // No last used location, possibly not sync'd yet
        if (lastUsedLocation == 0) {
            Utility.showToast(getActivity(), mapError, toastDuration);
            return;
        }

        // This fetches the last known location/city used
        Uri locationUri = WeatherContract.LocationEntry.buildLocationUri(lastUsedLocation);
        Cursor cursor = getContext().getContentResolver().query(
                locationUri,
                WeatherContract.LocationEntry.PROJECTION,
                WeatherContract.LocationEntry.COLUMN_ID + " = ? ",
                new String[] {
                        Long.toString(lastUsedLocation)
                },
                null
        );

        if (cursor == null) {
            Utility.showToast(getActivity(), mapError, toastDuration);
            return;
        }

        cursor.moveToPosition(0);
        LocationModel location = new LocationModel();
        location.loadFromCursor(cursor);

        Uri geoLocation = Uri.parse("geo:" + location.getCoordLat() + "," + location.getCoordLon());

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(LOG_TAG, "Couldn't call " + geoLocation.toString() + ", no receiving apps installed!");
        }
    }

    private void updateWeather() {
        SunshineSyncAdapter.syncImmediately(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_today, container, false);
        TodayViewHolder viewHolder = new TodayViewHolder(view);
        view.setTag(viewHolder);

        Location location = Utility.getCurrentLocation(getActivity(), false);
        if (location != null) {
            TextView coords = (TextView) getView().findViewById(R.id.today_gps_coords);
            String s = "lat (" + location.getLatitude() + ") lon (" + location.getLongitude() + ")";
            coords.setText(s);
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(TODAY_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mUri == null) {
            return null;
        }

        long datetime = Utility.getMidnightTimeToday();

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                mUri,
                WeatherContract.FORECAST_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.v(LOG_TAG, "In onLoadFinished");

        if (!cursor.moveToFirst()) {
            Log.d(LOG_TAG, "Could not move to first, cursor count: " + cursor.getCount());
            return;
        }

        onWeatherRefresh(cursor);
    }

    public void refreshLoader() {
        Log.d(LOG_TAG, "Refreshing loader");
        getLoaderManager().restartLoader(TODAY_LOADER, null, this);
    }

    public void onLocationChanged(String newLocation) {
        Uri uri = mUri;

        if (uri == null) {
            return;
        }

        long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
        //Uri updatedUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
        //mUri = updatedUri;
        getLoaderManager().restartLoader(TODAY_LOADER, null, this);
    }

    public void onWeatherRefresh(Cursor cursor) {
        Log.v(LOG_TAG, "Reloading TODAY Weather");

        TodayViewHolder viewHolder = (TodayViewHolder) getView().getTag();
        viewHolder.reloadProperties(cursor, getActivity());

        Location location = Utility.getLastBestLocation(getActivity());
        if (location != null) {
            TextView coords = (TextView) getView().findViewById(R.id.today_gps_coords);
            String s = "lat (" + location.getLatitude() + ") lon (" + location.getLongitude() + ")";
            coords.setText(s);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        loader = new CursorLoader(
                getActivity(),
                mUri,
                WeatherContract.FORECAST_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onStart() {
        super.onStart();

        // Time ticker per minute: http://stackoverflow.com/a/13059819/1740059
        // This is a foreground only receiver
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
                    TodayViewHolder viewHolder = (TodayViewHolder) getView().getTag();
                    viewHolder.redrawDateTime();

                    Location location = Utility.getLastBestLocation(getActivity());
                    if (location != null) {
                        TextView coords = (TextView) getView().findViewById(R.id.today_gps_coords);
                        String s = "lat (" + location.getLatitude() + ") lon (" + location.getLongitude() + ")";
                        coords.setText(s);
                    }
                }
            }
        };

        getActivity().registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));

        mSwipeRefresh = (SwipeRefreshLayout) getActivity().findViewById(R.id.swiperefresh_today);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");

                // Signal SwipeRefreshLayout to start the progress indicator
                mSwipeRefresh.setRefreshing(true);

                // Peform update
                updateWeather();

                // Update coords
                Location location = Utility.getLastBestLocation(getActivity());
                if (location != null) {
                    TextView coords = (TextView) getView().findViewById(R.id.today_gps_coords);
                    String s = "lat (" + location.getLatitude() + ") lon (" + location.getLongitude() + ")";
                    coords.setText(s);
                }

                // Signal SwipeRefreshLayout to stop the progress indicator
                mSwipeRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mBroadcastReceiver != null) {
            getActivity().unregisterReceiver(mBroadcastReceiver);
        }
    }
}
