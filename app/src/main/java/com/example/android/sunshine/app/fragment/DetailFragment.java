package com.example.android.sunshine.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.net.Uri;

import com.example.android.sunshine.app.R;
import com.example.android.sunshine.app.data.WeatherContract;
import com.example.android.sunshine.app.data.model.LocationModel;
import com.example.android.sunshine.app.data.model.WeatherModel;
import com.example.android.sunshine.app.util.Utility;
import com.example.android.sunshine.app.view.DetailsViewHolder;

/**
 * Created by justinmitchell on 29/10/2016.
 */
public class DetailFragment extends Fragment implements LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    private static final int DETAIL_LOADER = 0;

    public static final String DETAIL_URI = "URI";

    private ShareActionProvider mShareActionProvider;
    private String mForecastStr;
    private Uri mUri;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailfragment, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        } else {
            Log.d(LOG_TAG, "Could not create Share Action Provider: Share Action Provider is null");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }

        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        DetailsViewHolder viewHolder = new DetailsViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, (mForecastStr != null ? mForecastStr : "") + FORECAST_SHARE_HASHTAG);

        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mUri == null) {
            return null;
        }

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
            return;
        }

        WeatherModel weatherModel = new WeatherModel();
        weatherModel.loadFromCursor(cursor);

        LocationModel locationModel = new LocationModel();
        locationModel.loadFromCursor(cursor);

        long weatherId = weatherModel.getWeatherId();
        DetailsViewHolder viewHolder = (DetailsViewHolder) getView().getTag();
        Context context = getActivity();
        boolean isMetric = Utility.getUnitType(context).equals(context.getString(R.string.pref_units_metric));

        // Do friendly formatting for sharing
        mForecastStr = weatherModel.getFormattedString(Utility.getUnitType(context), context);

        // Set icon
        viewHolder.iconView.setImageResource(Utility.getArtResourceForWeatherCondition((int) weatherModel.getWeatherId()));

        // Set accessibility property
        viewHolder.iconView.setContentDescription(weatherModel.getDescription());

        // Set day
        viewHolder.dayView.setText(weatherModel.getDayName(context));

        // Set date
        viewHolder.dateView.setText(weatherModel.getFormattedMonthDay(context));

        // Set description
        viewHolder.descriptionView.setText(weatherModel.getDescription());

        // Set temperature high
        viewHolder.highTempView.setText(weatherModel.getFormattedMaxTemperature(context, isMetric));

        // Set temperature low
        viewHolder.lowTempView.setText(weatherModel.getFormattedMinTemperature(context, isMetric));

        // Set humidity
        viewHolder.humidityView.setText(weatherModel.getFormattedHumidity(context));

        // Set pressure
        viewHolder.pressureView.setText(weatherModel.getFormattedPressure(context));

        // Set wind details
        viewHolder.windView.setText(weatherModel.getFormattedWindDetails(context));

        // Set location
        if (viewHolder.locationView != null) {
            viewHolder.locationView.setText(locationModel.getCityName());
        }

        // If onCreateOptionsMenu has already happened, we need to update the share intent now.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }

    public void onLocationChanged(String newLocation) {
        Uri uri = mUri;

        if (uri == null) {
            return;
        }

        long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
        Uri updatedUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
        mUri = updatedUri;
        getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
    }

}
