package com.tian.weather.provider;


import com.tian.weather.statics.Statics;
import com.tian.weather.utils.EngLog;

import android.net.Uri;

public class UriHelper {

	private static  final String TAG = "[CityWeather]UriHelper";
	
	public static Uri getUri(String path) {
		if (Statics.DEBUG)
			EngLog.d(TAG, "getUri(" + path + ')');

		Uri uri = prepare(path).build();

		if (Statics.DEBUG)
			EngLog.d(TAG, "uri: " + uri);

		return uri;
	}

	private static Uri.Builder prepare(String path) {
		return new Uri.Builder().scheme("content")
				.authority(DbProvider.AUTHORITY).path(path).query("") 
				.fragment("");

	}
	
	static Uri removeQuery(Uri uri) {
        if (Statics.DEBUG)
            EngLog.d(TAG, "removeQuery(" + uri + ")");

        Uri newUri = uri.buildUpon().query("").build();

        if (Statics.DEBUG)
            EngLog.d(TAG, "new uri: " + newUri);
        
        return newUri;
    }

}
