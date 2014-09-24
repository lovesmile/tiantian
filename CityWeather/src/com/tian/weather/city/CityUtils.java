package com.tian.weather.city;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Xml;

import com.tian.weather.provider.DbDataStore.CityTable;
import com.tian.weather.provider.DbDataStore.ProvinceTable;
import com.tian.weather.provider.DbProvider;
import com.tian.weather.provider.UriHelper;
import com.tian.weather.statics.Statics;
import com.tian.weather.utils.EngLog;
import com.tian.weather.utils.NetworkUtils;

public class CityUtils {
private static final String TAG = "[CityWeather]CityUtils";
	
	private static final String PROVINCE_URL = "http://webservice.webxml.com.cn/WebServices/WeatherWS.asmx/getRegionProvince";
	private static final String CITY_URL = "http://webservice.webxml.com.cn/WebServices/WeatherWS.asmx/getSupportCityString?theRegionCode=";
	
	private static final int STATE_INIT   = 0;
	private static final int STATE_START  = 1;
	private static final int STATE_STOP   = 2;
	private static final int STATE_ERROR  = 3;
	
	private static final int ERROR_COUNT = 1000;
	
	private Context mContext;

	private static int index = 0;
	private ArrayList<Province> proList;

	public CityUtils(Context context) {
		this.mContext = context;
	}

	private Handler getCityHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.arg1) {
			case STATE_INIT:
				if (index < proList.size()) {
					SaveCityByPro(proList.get(0));
				} else {
					sendFailedBroadcast();
				}
				break;
			case STATE_START:
				index++;
				if (index < proList.size()) {
					SaveCityByPro(proList.get(index));
				}
				if (index == proList.size()){
					Intent intent = new Intent(Statics.ACTION_DOWNLOAD_CITYS_SUCCESS);
					mContext.sendBroadcast(intent);
				}
				break;
			case STATE_STOP:
				index = ERROR_COUNT;
				break;
			case STATE_ERROR:
				sendFailedBroadcast();
				break;
			}
		}
	};

	/*
	 * The main public interface of save city. 
	 * If you want to download all city and save them, you must call this function.
	 * the 'index' is a key parameter, it add by itself, and we get the position of 
	 * province in list by it.  
	 *
	 */
	public void saveCity() {
		index = 0;
		StartToGetProvinceList();
	}
	
	private void sendFailedBroadcast(){
		index = ERROR_COUNT;
		Intent intent = new Intent(Statics.ACTION_DOWNLOAD_CITYS_FAILED);
		mContext.sendBroadcast(intent);
	}

	/*
	 * to get list of provinces 
	 */
	public void StartToGetProvinceList() {
		proList = new ArrayList<Province>();
		Thread thread = new Thread() {
			@Override
			public void run() {
				boolean isSuccess = true;
				EngLog.i(TAG, "Get Province Url: " + PROVINCE_URL);
				DefaultHttpClient client = new DefaultHttpClient();
				HttpUriRequest req = new HttpGet(PROVINCE_URL);
				HttpResponse res;
				try {
					res = client.execute(req);
					if(res == null){
						isSuccess = false;
					}
					HttpEntity entity = res.getEntity();
					if(entity == null || entity.getContentLength() <= 0){
						isSuccess = false;
					}
					InputStream inputStream = entity.getContent();
					XmlPullParser parser = Xml.newPullParser();
					parser.setInput(inputStream, "utf-8");
					int types = parser.getEventType();
					while (types != XmlPullParser.END_DOCUMENT) {
						switch (types) {
						case XmlPullParser.START_DOCUMENT:
							break;
						case XmlPullParser.START_TAG:
							if ("string".equals(parser.getName())) {
								String provinceStr = parser.nextText();
								if(Statics.DEBUG){
									EngLog.d(TAG, "get province " + provinceStr);
								}
								Province province = new Province();
								int n = provinceStr.indexOf(',');
								province.name = provinceStr.substring(0, n);
								province.code = Long.valueOf(provinceStr
										.substring(n + 1));
								proList.add(province);
							}
							break;
						case XmlPullParser.END_TAG:
							break;

						case XmlPullParser.END_DOCUMENT:
							break;
						}

						types = parser.next();

					}
					inputStream.close();
				} catch (XmlPullParserException e) {
					isSuccess = false;
					e.printStackTrace();

				} catch (IOException e) {
					isSuccess = false;
					e.printStackTrace();

				} finally{
					Message msg = new Message();
					boolean isNetworkError = NetworkUtils.getNetworkState(mContext) == NetworkUtils.NetworkState.NONE;
					if(isNetworkError || proList == null || proList.size() <= 0 || !isSuccess){
						msg.arg1 = STATE_ERROR;
					} else {
						msg.arg1 = STATE_INIT;
					}
					if(Statics.DEBUG){
						EngLog.d(TAG, "send province message " + msg.arg1);
					}
					getCityHandler.sendMessage(msg);
				}
			}

		};
		thread.start();
		thread = null;
	}

	public void SaveCityByPro(final Province pro) {
		if(Statics.DEBUG){
			EngLog.d(TAG, "get city by province " + pro.name);
		}
		
		// save information of province to database
		savePro(pro);
		if(NetworkUtils.getNetworkState(mContext) == NetworkUtils.NetworkState.NONE){
			sendFailedBroadcast();
			return;
		}
		Intent intent = new Intent(Statics.ACTION_DOWNLOAD_PROVINCE);
		intent.putExtra(Statics.EXTRA_PROVINCE, pro.name);
		if(Statics.DEBUG){
			EngLog.e(TAG, "send broadcast of province :" + pro.name);
		}
		mContext.sendBroadcast(intent);
		final ArrayList<City> cityList = new ArrayList<City>();
		Thread thread = new Thread() {
			@Override
			public void run() {
				boolean isSuccess = true;
				String url = getCityUrl(pro.code);
				EngLog.e(TAG, "Get City Url: " + url);
				DefaultHttpClient client = new DefaultHttpClient();
				HttpUriRequest req = new HttpGet(url);
				HttpResponse res;
				try {
					res = client.execute(req);
					HttpEntity entity = res.getEntity();
					InputStream inputStream = entity.getContent();
					XmlPullParser parser = Xml.newPullParser();
					parser.setInput(inputStream, "utf-8");
					int types = parser.getEventType();
					while (types != XmlPullParser.END_DOCUMENT) {
						switch (types) {
						case XmlPullParser.START_DOCUMENT:
							break;
						case XmlPullParser.START_TAG:
							if ("string".equals(parser.getName())) {
								String cityStr = parser.nextText();
								Log.e(TAG, "get city " + cityStr);
								int n = cityStr.indexOf(',');
								City city = new City();
								city.province = pro.name;
								city.provinceCode = pro.code;
								city.city = cityStr.substring(0, n);
								city.cityCode = Long.valueOf(cityStr
										.substring(n + 1));
								cityList.add(city);
							}
							break;
						case XmlPullParser.END_TAG:
							break;

						}
						types = parser.next();

					}
					inputStream.close();
				} catch (XmlPullParserException e) {
					isSuccess = false;
					e.printStackTrace();

				} catch (IOException e) {
					isSuccess = false;
					e.printStackTrace();
				} finally{
					if(!isSuccess || cityList == null || cityList.size() <= 0){
						Message msg = new Message();
						msg.arg1 = STATE_ERROR;
						getCityHandler.sendMessage(msg);
					} else {
						saveCity(cityList);
						cityList.clear();
					}
				}

			}

		};
		thread.start();
		thread = null;
	}

	private void savePro(Province pro){
		Uri uri = UriHelper.getUri(DbProvider.PROVINCE_TABLE);
		if(Statics.DEBUG){
			EngLog.d(TAG, "save province: " + pro.name);
		}
		ContentValues value = new ContentValues();
		value.put(ProvinceTable.PROVINCE_NAME, pro.name);
		value.put(ProvinceTable.PROVINCE_CODE, pro.code);
		mContext.getContentResolver().insert(uri, value);
	}
	
	private void saveCity(ArrayList<City> list) {

		Uri uri = UriHelper.getUri(DbProvider.CITY_TABLE);
		EngLog.e(TAG, "save uri " + uri);
		for (int i = 0; i < list.size(); i++) {
			if(Statics.DEBUG){
				EngLog.e(TAG, "province: " + list.get(i).province + "  city: "
						+ list.get(i).city);
			}
			ContentValues value = new ContentValues();
			value.put(CityTable.PROVINCE_CODE, list.get(i).provinceCode);
			value.put(CityTable.CITY_CODE, list.get(i).cityCode);
			value.put(CityTable.PROVINCE_NAME, list.get(i).province);
			value.put(CityTable.CITY_NAME, list.get(i).city);
			mContext.getContentResolver().insert(uri, value);
		}
		Message msg = new Message();
		msg.arg1 = STATE_START;
		getCityHandler.sendMessage(msg);
	}

	private String getCityUrl(long provinceCode) {
		String code = String.valueOf(provinceCode);
		return CITY_URL	+ code;
	}

	private static class Province {
		public String name;
		public long code;
	}

	private static class City {
		public String province;
		public String city;
		public long provinceCode;
		public long cityCode;
	}
	
	public void stopGetCity(){
		Message msg = new Message();
		msg.arg1 = STATE_STOP;
		getCityHandler.sendMessage(msg);
	}

}
