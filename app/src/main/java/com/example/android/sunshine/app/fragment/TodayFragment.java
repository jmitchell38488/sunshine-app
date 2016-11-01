package com.example.android.sunshine.app.fragment;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.sunshine.app.R;
import com.example.android.sunshine.app.data.WeatherContract;
import com.example.android.sunshine.app.data.model.LocationModel;
import com.example.android.sunshine.app.data.model.WeatherModel;
import com.example.android.sunshine.app.util.Utility;
import com.example.android.sunshine.app.view.DetailsViewHolder;

/**
 * Created by justinmitchell on 1/11/2016.
 */

public class TodayFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = TodayFragment.class.getSimpleName();
    private static final int TODAY_LOADER = 0;

    private Uri mUri;

    public TodayFragment() {
        setHasOptionsMenu(false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_today, container, false);
        DetailsViewHolder viewHolder = new DetailsViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(TODAY_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    public void onLocationChanged(String newLocation) {
        Uri uri = mUri;

        if (uri == null) {
            return;
        }

        long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
        Uri updatedUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
        mUri = updatedUri;
        getLoaderManager().restartLoader(TODAY_LOADER, null, this);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

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


        DetailsViewHolder viewHolder = (DetailsViewHolder) getView().getTag();
        Context context = getActivity();

        long weatherId = weatherModel.getWeatherId();
        boolean isMetric = Utility.getUnitType(context).equals(context.getString(R.string.pref_units_metric));

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
    }

}
