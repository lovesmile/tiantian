package com.tian.weather;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Xml;

import com.tian.weather.provider.DbProvider;
import com.tian.weather.provider.UriHelper;
import com.tian.weather.provider.DbDataStore.WeatherTable;
import com.tian.weather.statics.Statics;
import com.tian.weather.utils.EngLog;

public class WeatherUtils {

	private static final String TAG = "[ZHUANG]WeatherUtils";
	private static final String WEATHER_URL = "http://webservice.webxml.com.cn/WebServices/WeatherWS.asmx/getWeather";

	private static WeatherBean weather;

	public static void StartToGetWeather(final Context context,
			final long cityCode) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				boolean isSuccess = true;
				String url = WEATHER_URL + "?theCityCode=" + cityCode
						+ "&theUserId=";
				EngLog.i(TAG, "Get weather Url: " + url);
				DefaultHttpClient client = new DefaultHttpClient();
				// 请求超时
				client.getParams().setParameter(
						CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
				// 读取超时
				client.getParams().setParameter(
						CoreConnectionPNames.SO_TIMEOUT, 20000);
				HttpUriRequest req = new HttpGet(url);
				HttpResponse res;
				try {
					res = client.execute(req);
					if (res == null) {
						isSuccess = false;
					}
					HttpEntity entity = res.getEntity();
					if (entity == null || entity.getContentLength() <= 0) {
						isSuccess = false;
					}
					InputStream inputStream = entity.getContent();
					XmlPullParser parser = Xml.newPullParser();
					parser.setInput(inputStream, "utf-8");
					int types = parser.getEventType();

					if (Statics.DEBUG) {
						EngLog.d(TAG,
								"================= GET CITY WEATHER ======================");
					}

					int index = 0;
					String xmlStr = "";
					weather = new WeatherBean();
					while (types != XmlPullParser.END_DOCUMENT) {
						switch (types) {
						case XmlPullParser.START_DOCUMENT:
							break;
						case XmlPullParser.START_TAG:
							if ("string".equals(parser.getName())) {
								xmlStr = parser.nextText();
								if (Statics.DEBUG) {
									EngLog.d(TAG, "<string>" + xmlStr
											+ "</string>");
								}
								switch (index) {
								case 0:
									weather.PROVINCE_NAME = xmlStr;
									break;
								case 1:
									weather.CITY_NAME = xmlStr;
									break;
								case 2:
									weather.CITY_CODE = Long.valueOf(xmlStr);
									break;
								case 3:
									weather.UPDATE_TIME = xmlStr;
									break;
								case 4:
									weather.TODAY_NOW_WEATHER = xmlStr;
									break;
								case 5:
									weather.TODAY_AIR = xmlStr;
									break;
								case 6:
									weather.TODAY_CONTENT = xmlStr;
									break;
								case 7:
									weather.TODAY_WEATHER = xmlStr;
									break;
								case 8:
									weather.TODAY_TEMP = xmlStr;
									break;
								case 9:
									weather.TODAY_WIND = xmlStr;
									break;
								case 10:
									weather.TODAY_IMG1 = getDrawable(xmlStr);
									break;
								case 11:
									weather.TODAY_IMG2 = getDrawable(xmlStr);
									break;
								case 12:
									weather.AFTER_ONE_WEATHER = xmlStr;
									break;
								case 13:
									weather.AFTER_ONE_TEMP = xmlStr;
									break;
								case 14:
									weather.AFTER_ONE_WIND = xmlStr;
									break;
								case 15:
									weather.AFTER_ONE_IMG1 = getDrawable(xmlStr);
									break;
								case 16:
									weather.AFTER_ONE_IMG2 = getDrawable(xmlStr);
									break;
								case 17:
									weather.AFTER_TWO_WEATHER = xmlStr;
									break;
								case 18:
									weather.AFTER_TWO_TEMP = xmlStr;
									break;
								case 19:
									weather.AFTER_TWO_WIND = xmlStr;
									break;
								case 20:
									weather.AFTER_TWO_IMG1 = getDrawable(xmlStr);
									break;
								case 21:
									weather.AFTER_TWO_IMG2 = getDrawable(xmlStr);
									break;
								case 22:
									weather.AFTER_THREE_WEATHER = xmlStr;
									break;
								case 23:
									weather.AFTER_THREE_TEMP = xmlStr;
									break;
								case 24:
									weather.AFTER_THREE_WIND = xmlStr;
									break;
								case 25:
									weather.AFTER_THREE_IMG1 = getDrawable(xmlStr);
									break;
								case 26:
									weather.AFTER_THREE_IMG2 = getDrawable(xmlStr);
									break;
								case 27:
									weather.AFTER_FOUR_WEATHER = xmlStr;
									break;
								case 28:
									weather.AFTER_FOUR_TEMP = xmlStr;
									break;
								case 29:
									weather.AFTER_FOUR_WIND = xmlStr;
									break;
								case 30:
									weather.AFTER_FOUR_IMG1 = getDrawable(xmlStr);
									break;
								case 31:
									weather.AFTER_FOUR_IMG2 = getDrawable(xmlStr);
									break;
								default:
									break;
								}
								index++;
							}
							break;
						case XmlPullParser.END_TAG:
							break;

						case XmlPullParser.END_DOCUMENT:
							break;
						}

						types = parser.next();

					}
					if (Statics.DEBUG) {
						EngLog.d(TAG,
								"===================== END =========================");
					}
					inputStream.close();

				} catch (Exception e) {
					isSuccess = false;
					e.printStackTrace();

				} finally {
					Intent intent;
					if (isSuccess && weather != null) {
						if (Statics.DEBUG) {
							EngLog.d(TAG, "start to save city weather ...");
						}
						intent = new Intent(
								Statics.ACTION_REFRESH_WEATHER_SUCCESS);
						saveCityWeather(context, weather);
					} else {
						intent = new Intent(
								Statics.ACTION_REFRESH_WEATHER_FAILED);
					}
					context.sendBroadcast(intent);
				}
			}

		};
		thread.start();
		thread = null;
	}

	private static int getDrawable(String str) {
		int point = str.indexOf('.');
		String num = str.substring(0, point);
		return Integer.valueOf(num);
	}

	public static void saveCityWeather(Context context, WeatherBean weather) {
		Uri uri = UriHelper.getUri(DbProvider.WEATHER_TABLE);
		ContentValues value = new ContentValues();
		value.put(WeatherTable.PROVINCE_NAME, weather.PROVINCE_NAME);
		value.put(WeatherTable.CITY_NAME, weather.CITY_NAME);
		value.put(WeatherTable.CITY_CODE, weather.CITY_CODE);
		value.put(WeatherTable.UPDATE_TIME, weather.UPDATE_TIME);
		value.put(WeatherTable.TODAY_NOW_WEATHER, weather.TODAY_NOW_WEATHER);
		value.put(WeatherTable.TODAY_AIR, weather.TODAY_AIR);
		value.put(WeatherTable.TODAY_CONTENT, weather.TODAY_CONTENT);
		value.put(WeatherTable.TODAY_WEATHER, weather.TODAY_WEATHER);
		value.put(WeatherTable.TODAY_TEMP, weather.TODAY_TEMP);
		value.put(WeatherTable.TODAY_WIND, weather.TODAY_WIND);
		value.put(WeatherTable.TODAY_IMG1, weather.TODAY_IMG1);
		value.put(WeatherTable.TODAY_IMG2, weather.TODAY_IMG2);
		value.put(WeatherTable.AFTER_ONE_WEATHER, weather.AFTER_ONE_WEATHER);
		value.put(WeatherTable.AFTER_ONE_TEMP, weather.AFTER_ONE_TEMP);
		value.put(WeatherTable.AFTER_ONE_WIND, weather.AFTER_ONE_WIND);
		value.put(WeatherTable.AFTER_ONE_IMG1, weather.AFTER_ONE_IMG1);
		value.put(WeatherTable.AFTER_ONE_IMG2, weather.AFTER_ONE_IMG2);
		value.put(WeatherTable.AFTER_TWO_WEATHER, weather.AFTER_TWO_WEATHER);
		value.put(WeatherTable.AFTER_TWO_TEMP, weather.AFTER_TWO_TEMP);
		value.put(WeatherTable.AFTER_TWO_WIND, weather.AFTER_TWO_WIND);
		value.put(WeatherTable.AFTER_TWO_IMG1, weather.AFTER_TWO_IMG1);
		value.put(WeatherTable.AFTER_TWO_IMG2, weather.AFTER_TWO_IMG2);
		value.put(WeatherTable.AFTER_THREE_WEATHER, weather.AFTER_THREE_WEATHER);
		value.put(WeatherTable.AFTER_THREE_TEMP, weather.AFTER_THREE_TEMP);
		value.put(WeatherTable.AFTER_THREE_WIND, weather.AFTER_THREE_WIND);
		value.put(WeatherTable.AFTER_THREE_IMG1, weather.AFTER_THREE_IMG1);
		value.put(WeatherTable.AFTER_THREE_IMG2, weather.AFTER_THREE_IMG2);
		value.put(WeatherTable.AFTER_FOUR_WEATHER, weather.AFTER_FOUR_WEATHER);
		value.put(WeatherTable.AFTER_FOUR_TEMP, weather.AFTER_ONE_TEMP);
		value.put(WeatherTable.AFTER_FOUR_WIND, weather.AFTER_FOUR_WIND);
		value.put(WeatherTable.AFTER_FOUR_IMG1, weather.AFTER_FOUR_IMG1);
		value.put(WeatherTable.AFTER_FOUR_IMG2, weather.AFTER_FOUR_IMG2);
		context.getContentResolver().insert(uri, value);

	}
}
