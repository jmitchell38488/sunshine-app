package com.example.android.sunshine.app.data.storage;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by justinmitchell on 28/10/2016.
 */

public class WeatherStorageFactory {

    private static List<IStorage> instances;

    static {
        instances = new ArrayList<>();
    }

    public static IStorage getDataStorageAccessor(Context context) {
        if (instances.isEmpty()) {
            // For now, just use the default sqlite db
            instances.add(new WeatherSqlite(context));
        }

        return instances.get(0);
    }

}
