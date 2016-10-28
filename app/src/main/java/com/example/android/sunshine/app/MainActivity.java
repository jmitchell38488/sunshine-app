package com.example.android.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(LOG_TAG, "Action: onCreate");

        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                Log.d(LOG_TAG, "Triggering intent {SettingsActivity}");
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                break;

            case R.id.action_map:
                Log.d(LOG_TAG, "Triggering intent {Maps} to retrieve preferred location");
                openPreferredLocationInMap();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openPreferredLocationInMap() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String location = prefs.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));

        // Set the URI for the geo location for maps
        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q", location)
                .build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "Action: onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "Action: onStop");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "Action: onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "Action: onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "Action: onDestroy");
    }

}
