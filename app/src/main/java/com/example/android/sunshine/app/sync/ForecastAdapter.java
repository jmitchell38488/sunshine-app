package com.example.android.sunshine.app.sync;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.sunshine.app.R;
import com.example.android.sunshine.app.data.WeatherContract;
import com.example.android.sunshine.app.data.model.CurrentConditionsModel;
import com.example.android.sunshine.app.data.model.LocationModel;
import com.example.android.sunshine.app.data.model.WeatherModel;
import com.example.android.sunshine.app.util.Utility;
import com.example.android.sunshine.app.view.ListItemViewHolder;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {

    private final int VIEW_TYPE_TODAY = 0;
    private final int VIEW_TYPE_FUTURE_DAY = 1;

    private boolean mUseTodayLayout = true;

    public ForecastAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        this.mUseTodayLayout = useTodayLayout;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 && mUseTodayLayout) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;

        if (viewType == VIEW_TYPE_TODAY) {
            layoutId = R.layout.list_item_forecast_today;
        } else {
            layoutId = R.layout.list_item_forecast;
        }

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ListItemViewHolder listItemViewHolder = new ListItemViewHolder(view);
        view.setTag(listItemViewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        WeatherModel weatherModel = new WeatherModel();
        weatherModel.loadFromCursor(cursor);

        LocationModel locationModel = new LocationModel();
        locationModel.loadFromCursor(cursor);

        CurrentConditionsModel currentModel = null;

        ListItemViewHolder listItemViewHolder = (ListItemViewHolder) view.getTag();
        boolean isMetric = Utility.getUnitType(context).equals(context.getString(R.string.pref_units_metric));
        int viewType = getItemViewType(cursor.getPosition());

        // We only want to do this on "TODAY"
        if (viewType == VIEW_TYPE_TODAY || listItemViewHolder.currentTempView != null) {
            // Fetch the current conditions
            Uri currentUri = WeatherContract.CurrentConditionsEntry.buildCurrentConditionsUri(weatherModel.getLocationId());
            Cursor conditionsCursor = context.getContentResolver().query(
                    currentUri,
                    WeatherContract.CurrentConditionsEntry.FORECAST_COLUMNS,
                    WeatherContract.CurrentConditionsEntry.COLUMN_LOC_KEY + " = ?",
                    new String[]{
                            Long.toString(weatherModel.getLocationId())
                    },
                    WeatherContract.CurrentConditionsEntry.COLUMN_DATE + " DESC"
            );

            if (conditionsCursor != null && conditionsCursor.moveToFirst()) {
                currentModel = new CurrentConditionsModel();
                currentModel.loadFromCursor(conditionsCursor);
            }
        }

        // Set icon
        switch (viewType) {
            case VIEW_TYPE_TODAY: {
                listItemViewHolder.iconView.setImageResource(Utility.getArtResourceForWeatherCondition((int) weatherModel.getWeatherId()));
                break;
            }
            case VIEW_TYPE_FUTURE_DAY: {
                listItemViewHolder.iconView.setImageResource(Utility.getIconResourceForWeatherCondition((int) weatherModel.getWeatherId()));
                break;
            }
        }

        // Set accessibility property
        listItemViewHolder.iconView.setContentDescription(weatherModel.getDescription());

        // Set date
        listItemViewHolder.dateView.setText(weatherModel.getFriendlyDayString(context));

        // If there are no current conditions, make sure to shift the high/low to the higher priority views
        if (currentModel == null) {
            if (viewType == VIEW_TYPE_TODAY) {
                listItemViewHolder.highTempView.setTextSize(72);
                listItemViewHolder.lowTempView.setTextSize(36);
                listItemViewHolder.currentTempView.setEnabled(false);
            }
        } else if (listItemViewHolder.currentTempView != null) {
            // Set current high
            listItemViewHolder.currentTempView.setText(currentModel.getFormattedCurrentTemperature(context, isMetric));
        }

        // Set temperature high
        listItemViewHolder.highTempView.setText(weatherModel.getFormattedMaxTemperature(context, isMetric));

        // Set temperature low
        listItemViewHolder.lowTempView.setText(weatherModel.getFormattedMinTemperature(context, isMetric));

        // Set temperature low
        listItemViewHolder.descriptionView.setText(weatherModel.getDescription());

        // Set location
        if (listItemViewHolder.locationView != null) {
            listItemViewHolder.locationView.setText(locationModel.getCityName());
        }
    }

}