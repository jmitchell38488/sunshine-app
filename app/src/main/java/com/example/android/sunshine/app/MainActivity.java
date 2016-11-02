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

    public static final int PERMISSIONS_ALL = 1000;
    public static final int PERMISSIONS_REQUEST_GPS = 1001;
    public static final int PERMISSIONS_ACCESS_NETWORK_STATE = 1002;

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

        Utility.checkRequiredPermissions(this);

        SunshineSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TodayFragment tf = (TodayFragment) getSupportFragmentManager().findFragmentById(R.id.today_detail_container);
        tf.refreshLoader();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_GPS:
                if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //
                }
                break;

            case PERMISSIONS_ACCESS_NETWORK_STATE:
                if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //
                }
                break;
        }
    }

}
