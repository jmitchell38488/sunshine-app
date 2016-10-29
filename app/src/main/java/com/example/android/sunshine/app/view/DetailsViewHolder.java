package com.example.android.sunshine.app.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.app.R;

/**
 * Created by justinmitchell on 30/10/2016.
 */

public class DetailsViewHolder {

    public final ImageView iconView;
    public final TextView dayView;
    public final TextView dateView;
    public final TextView descriptionView;
    public final TextView highTempView;
    public final TextView lowTempView;
    public final TextView humidityView;
    public final TextView pressureView;
    public final TextView windView;

    public DetailsViewHolder(View view) {
        iconView = (ImageView) view.findViewById(R.id.detail_icon);
        dayView = (TextView) view.findViewById(R.id.detail_day_textview);
        dateView = (TextView) view.findViewById(R.id.detail_date_textview);
        descriptionView = (TextView) view.findViewById(R.id.detail_forecast_textview);
        highTempView = (TextView) view.findViewById(R.id.detail_high_textview);
        lowTempView = (TextView) view.findViewById(R.id.detail_low_textview);
        humidityView = (TextView) view.findViewById(R.id.detail_humidity_textview);
        pressureView = (TextView) view.findViewById(R.id.detail_pressure_textview);
        windView = (TextView) view.findViewById(R.id.detail_wind_textview);
    }

}