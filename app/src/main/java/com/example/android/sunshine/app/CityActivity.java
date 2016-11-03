package com.example.android.sunshine.app;

import android.os.Bundle;
import android.app.Activity;

public class CityActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
