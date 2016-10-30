package com.example.android.sunshine.app.sync;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.sunshine.app.R;
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
        ListItemViewHolder listItemViewHolder = (ListItemViewHolder) view.getTag();
        boolean isMetric = Utility.getUnitType(context).equals(context.getString(R.string.pref_units_metric));

        // Set icon
        int viewType = getItemViewType(cursor.getPosition());

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

        // Set temperature high
        listItemViewHolder.highTempView.setText(weatherModel.getFormattedMaxTemperature(context, isMetric));

        // Set temperature low
        listItemViewHolder.lowTempView.setText(weatherModel.getFormattedMinTemperature(context, isMetric));

        // Set temperature low
        listItemViewHolder.descriptionView.setText(weatherModel.getDescription());
    }

}