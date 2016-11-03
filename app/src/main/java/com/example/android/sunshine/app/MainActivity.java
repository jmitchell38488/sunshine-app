package com.example.android.sunshine.app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.sunshine.app.fragment.TodayFragment;
import com.example.android.sunshine.app.sync.SunshineSyncAdapter;
import com.example.android.sunshine.app.util.Preferences;
import com.example.android.sunshine.app.util.Utility;


public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Preferences.incrementTimesRun(this);
        Utility.hideStatusBar(this);

        // Add TODAY fragment to main
        if (savedInstanceState == null) {
            TodayFragment fragment = new TodayFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.today_detail_container, fragment)
                    .commit();
        }

        getSupportActionBar().setElevation(0);

        SunshineSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TodayFragment tf = (TodayFragment) getSupportFragmentManager().findFragmentById(R.id.today_detail_container);
        tf.refreshLoader();
    }

}
