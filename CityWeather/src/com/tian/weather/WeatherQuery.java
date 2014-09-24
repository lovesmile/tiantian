package com.tian.weather;

import com.tian.weather.provider.DbProvider;
import com.tian.weather.provider.UriHelper;
import com.tian.weather.provider.DbDataStore.WeatherTable;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class WeatherQuery {

	private static final String TAG = "[ZHUANG]WeatherQuery";
	
	private static final String QUERY_SELECTION_WEATHER_BY_CITY_CODE = WeatherTable.CITY_CODE + "=?";
	
	/**
	 * Get city's weather from db 
	 * @param context
	 * @param cityCode
	 * @return weather
	 */
	public static WeatherBean readCityWeatherInDb(Context context, long cityCode){
		Uri uri = UriHelper.getUri(DbProvider.WEATHER_TABLE);
		Cursor cursor = context.getContentResolver().query(uri, WeatherProjection.SUMMARY_PROJECTION, 
				QUERY_SELECTION_WEATHER_BY_CITY_CODE, new String[]{"" + cityCode}, null);
		if (!cursor.moveToFirst()) {
			if(cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			return null;
		}
		
		WeatherBean weather = new WeatherBean();
		weather.ID = cursor.getLong(WeatherProjection.ID);
		weather.PROVINCE_NAME = cursor.getString(WeatherProjection.PROVINCE_NAME);
		weather.CITY_CODE = cursor.getLong(WeatherProjection.CITY_CODE);
		weather.CITY_NAME = cursor.getString(WeatherProjection.CITY_NAME);
		weather.UPDATE_TIME = cursor.getString(WeatherProjection.UPDATE_TIME);
		
		weather.TODAY_NOW_WEATHER = cursor.getString(WeatherProjection.TODAY_NOW_WEATHER);
		weather.TODAY_AIR = cursor.getString(WeatherProjection.TODAY_AIR);
		weather.TODAY_CONTENT = cursor.getString(WeatherProjection.TODAY_CONTENT);
		
		weather.TODAY_WEATHER = cursor.getString(WeatherProjection.TODAY_WEATHER);
		weather.TODAY_TEMP = cursor.getString(WeatherProjection.TODAY_TEMP);
		weather.TODAY_WIND = cursor.getString(WeatherProjection.TODAY_WIND);
		weather.TODAY_IMG1 = cursor.getInt(WeatherProjection.TODAY_IMG1);
		weather.TODAY_IMG2 = cursor.getInt(WeatherProjection.TODAY_IMG2);
		
		weather.AFTER_ONE_WEATHER = cursor.getString(WeatherProjection.AFTER_ONE_WEATHER);
		weather.AFTER_ONE_TEMP = cursor.getString(WeatherProjection.AFTER_ONE_TEMP);
		weather.AFTER_ONE_WIND = cursor.getString(WeatherProjection.AFTER_ONE_WIND);
		weather.AFTER_ONE_IMG1 = cursor.getInt(WeatherProjection.AFTER_ONE_IMG1);
		weather.AFTER_ONE_IMG2 = cursor.getInt(WeatherProjection.AFTER_ONE_IMG2);
		
		weather.AFTER_TWO_WEATHER = cursor.getString(WeatherProjection.AFTER_TWO_WEATHER);
		weather.AFTER_TWO_TEMP = cursor.getString(WeatherProjection.AFTER_TWO_TEMP);
		weather.AFTER_TWO_WIND = cursor.getString(WeatherProjection.AFTER_TWO_WIND);
		weather.AFTER_TWO_IMG1 = cursor.getInt(WeatherProjection.AFTER_TWO_IMG1);
		weather.AFTER_TWO_IMG2 = cursor.getInt(WeatherProjection.AFTER_TWO_IMG2);
		
		weather.AFTER_THREE_WEATHER = cursor.getString(WeatherProjection.AFTER_THREE_WEATHER);
		weather.AFTER_THREE_TEMP = cursor.getString(WeatherProjection.AFTER_THREE_TEMP);
		weather.AFTER_THREE_WIND = cursor.getString(WeatherProjection.AFTER_THREE_WIND);
		weather.AFTER_THREE_IMG1 = cursor.getInt(WeatherProjection.AFTER_THREE_IMG1);
		weather.AFTER_THREE_IMG2 = cursor.getInt(WeatherProjection.AFTER_THREE_IMG2);
		
		weather.AFTER_FOUR_WEATHER = cursor.getString(WeatherProjection.AFTER_FOUR_WEATHER);
		weather.AFTER_FOUR_TEMP = cursor.getString(WeatherProjection.AFTER_FOUR_TEMP);
		weather.AFTER_FOUR_WIND = cursor.getString(WeatherProjection.AFTER_FOUR_WIND);
		weather.AFTER_FOUR_IMG1 = cursor.getInt(WeatherProjection.AFTER_FOUR_IMG1);
		weather.AFTER_FOUR_IMG2 = cursor.getInt(WeatherProjection.AFTER_FOUR_IMG2);
		
		return weather;
	}
	
}
