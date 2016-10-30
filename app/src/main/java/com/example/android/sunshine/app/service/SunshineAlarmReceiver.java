package com.example.android.sunshine.app.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by justinmitchell on 30/10/2016.
 */

public class SunshineAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent sendIntent  = new Intent(context, SunshineService.class);
        sendIntent.putExtra(SunshineService.LOCATION_QUERY_EXTRA, intent.getStringExtra(SunshineService.LOCATION_QUERY_EXTRA));
        context.startService(sendIntent);
    }
}
