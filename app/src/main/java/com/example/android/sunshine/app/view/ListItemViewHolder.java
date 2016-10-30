package com.example.android.sunshine.app.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.app.R;

/**
 * Created by justinmitchell on 30/10/2016.
 */

public class ListItemViewHolder {

    public final ImageView iconView;
    public final TextView dateView;
    public final TextView descriptionView;
    public final TextView highTempView;
    public final TextView lowTempView;
    public final TextView locationView;

    public ListItemViewHolder(View view) {
        iconView = (ImageView) view.findViewById(R.id.list_item_icon);
        dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
        descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
        highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
        lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);

        if (view.findViewById(R.id.list_item_location_textview) != null) {
            locationView = (TextView) view.findViewById(R.id.list_item_location_textview);
        } else {
            locationView = null;
        }
    }

}