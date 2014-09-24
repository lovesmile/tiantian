package com.tian.weather.city;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.tian.weather.city.CityBean;
import com.tian.weather.city.ProvinceBean;
import com.tian.weather.provider.DbDataStore.ProvinceTable;
import com.tian.weather.provider.DbProvider;
import com.tian.weather.provider.UriHelper;
import com.tian.weather.provider.DbDataStore.CityTable;
import com.tian.weather.provider.DbDataStore.DefaultCityTable;
import com.tian.weather.utils.EngLog;

public class CityQuery {

	private static final String TAG = "[CityWeather]CityQuery";

	public static final String QUERY_CITY_SELECTION_BY_PRO_CODE = CityTable.PROVINCE_CODE
			+ "=?";
	public static final String QUERY_CITY_SELECTION_BY_CITY_NAME = CityTable.CITY_NAME
			+ "=?";
	public static final String QUERY_CITY_SELECTION_BY_ID = CityTable.ID + "=?";
	public static final String QUERY_CITY_SELECTION_BY_CITY_CODE = CityTable.CITY_CODE
			+ "=?";

	/**
	 * get the count of city database
	 * 
	 * @param context
	 * @return the count of city database
	 */
	public static int getCityCountFromDb(Context context) {
		int count = 0;
		Uri uri = UriHelper.getUri(DbProvider.PROVINCE_TABLE);
		try {
			Cursor cursor = context.getContentResolver().query(uri,
					ProvinceProjection.SUMMARY_PROJECTION, null, null, null);
			if (cursor != null) {
				count = cursor.getCount();
				cursor.close();
				cursor = null;
				EngLog.i(TAG, "Return city count: " + count);
			}
		} catch (Exception e) {
			EngLog.e(TAG, e.toString());
		}
		return count;

	}

	/**
	 * get city by city code
	 * 
	 * @param context
	 * @param city
	 *            code
	 * @return city
	 */
	public static CityBean getCityByCityCode(Context context, long cityCode) {
		Uri uri = UriHelper.getUri(DbProvider.CITY_TABLE);
		Cursor cursor = context.getContentResolver().query(uri,
				CityProjection.SUMMARY_PROJECTION,
				QUERY_CITY_SELECTION_BY_CITY_CODE,
				new String[] { "" + cityCode }, null);
		if (cursor == null || !cursor.moveToFirst()) {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			return null;
		}
		if (cursor != null) {
			CityBean city = new CityBean();
			city.provinceName = cursor.getString(CityProjection.PROVINCE_NAME);
			city.cityName = cursor.getString(CityProjection.CITY_NAME);
			city.provinceCode = cursor.getLong(CityProjection.PROVINCE_CODE);
			city.cityCode = cursor.getLong(CityProjection.CITY_CODE);
			EngLog.i(TAG, "Return city: " + city.toString());
			cursor.close();
			cursor = null;
			return city;
		}

		return null;
	}

	/**
	 * read the info of city by _id
	 * 
	 * @param context
	 * @param _id
	 * @return city info
	 */
	public static final CityBean readCityFromDb(Context context, long _id) {
		Uri uri = UriHelper.getUri(DbProvider.CITY_TABLE);
		Cursor cursor = context.getContentResolver().query(uri,
				CityProjection.SUMMARY_PROJECTION, QUERY_CITY_SELECTION_BY_ID,
				new String[] { "" + _id }, null);
		if (cursor == null || !cursor.moveToFirst()) {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			return null;
		}
		if (cursor != null) {
			CityBean city = new CityBean();
			city.provinceName = cursor.getString(CityProjection.PROVINCE_NAME);
			city.cityName = cursor.getString(CityProjection.CITY_NAME);
			city.provinceCode = cursor.getLong(CityProjection.PROVINCE_CODE);
			city.cityCode = cursor.getLong(CityProjection.CITY_CODE);
			EngLog.i(TAG, "Return city: " + city.toString());
			cursor.close();
			cursor = null;
			return city;
		}

		return null;

	}

	/**
	 * get city list through searching
	 * 
	 * @param context
	 * @param str
	 * @return city list
	 */
	public static final ArrayList<CityBean> getCityListBySearch(
			Context context, String str) {

		ArrayList<CityBean> list = new ArrayList<CityBean>();
		Uri uri = UriHelper.getUri(DbProvider.CITY_TABLE);
		Cursor cursor = context.getContentResolver().query(uri,
				CityProjection.SUMMARY_PROJECTION, null, null, null);
		if (cursor == null || !cursor.moveToFirst()) {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			return null;
		}
		do {

			String cityName = cursor.getString(CityProjection.CITY_NAME);
			if (!TextUtils.isEmpty(cityName) && cityName.startsWith(str)) {
				CityBean city = new CityBean();
				city.cityCode = cursor.getLong(CityProjection.CITY_CODE);
				city.cityName = cursor.getString(CityProjection.CITY_NAME);
				city.provinceCode = cursor
						.getLong(CityProjection.PROVINCE_CODE);
				city.provinceName = cursor
						.getString(CityProjection.PROVINCE_NAME);
				list.add(city);
			} else {
				continue;
			}

		} while (cursor.moveToNext());

		return list;
	}

	/**
	 * get city bean through GridView item click
	 * 
	 * @param context
	 * @param str
	 * @return city list
	 */
	public static final CityBean getCityBeanByGridView(Context context,
			String str) {
		CityBean city = new CityBean();
		Uri uri = UriHelper.getUri(DbProvider.CITY_TABLE);
		Cursor cursor = null;
		try {
			cursor = context.getContentResolver().query(uri,
					CityProjection.SUMMARY_PROJECTION, null, null, null);
			if (cursor == null || !cursor.moveToFirst()) {
				if (cursor != null && !cursor.isClosed()) {
					cursor.close();
				}
				return null;
			}
			do {

				String cityName = cursor.getString(CityProjection.CITY_NAME);
				if (!TextUtils.isEmpty(cityName) && cityName.equals(str)) {
					city.cityCode = cursor.getLong(CityProjection.CITY_CODE);
					city.cityName = cursor.getString(CityProjection.CITY_NAME);
					city.provinceCode = cursor
							.getLong(CityProjection.PROVINCE_CODE);
					city.provinceName = cursor
							.getString(CityProjection.PROVINCE_NAME);
				} else {
					continue;
				}
			} while (cursor.moveToNext());
			return city;
		} catch (Exception e) {
			return null;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * get city list by province code
	 * 
	 * @param context
	 * @param procode
	 * @return city list
	 */
	public static final ArrayList<CityBean> getCityList(Context context,
			long proCode) {
		ArrayList<CityBean> list = new ArrayList<CityBean>();
		Uri uri = UriHelper.getUri(DbProvider.CITY_TABLE);
		Cursor cursor = context.getContentResolver()
				.query(uri, CityProjection.SUMMARY_PROJECTION,
						QUERY_CITY_SELECTION_BY_PRO_CODE,
						new String[] { "" + proCode },
						CityTable.PROVINCE_NAME + " ASC");
		if (cursor == null || !cursor.moveToFirst()) {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			return null;
		}
		do {
			CityBean city = new CityBean();
			city.cityCode = cursor.getLong(CityProjection.CITY_CODE);
			city.cityName = cursor.getString(CityProjection.CITY_NAME);
			city.provinceCode = cursor.getLong(CityProjection.PROVINCE_CODE);
			city.provinceName = cursor.getString(CityProjection.PROVINCE_NAME);
			list.add(city);

		} while (cursor.moveToNext());
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return list;
	}

	/**
	 * get the list of all provinces
	 * 
	 * @param context
	 * @return provinces list
	 */
	public static final ArrayList<ProvinceBean> getAllProvinceList(
			Context context) {
		ArrayList<ProvinceBean> list = new ArrayList<ProvinceBean>();
		Uri uri = UriHelper.getUri(DbProvider.PROVINCE_TABLE);
		Cursor cursor = context.getContentResolver().query(uri,
				ProvinceProjection.SUMMARY_PROJECTION, null, null,
				ProvinceTable.PROVINCE_NAME );
		if (cursor == null || !cursor.moveToFirst()) {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			return null;
		}
		do {
			ProvinceBean pro = new ProvinceBean();
			pro.provinceName = cursor.getString(ProvinceProjection.PROVINCE_NAME);
			pro.provinceCode = cursor.getLong(ProvinceProjection.PROVINCE_CODE);
			list.add(pro);

		} while (cursor.moveToNext());
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return list;
	}

	/**
	 * save the default city in table 'DefaultCityTab'
	 * 
	 * @param context
	 * @param cityCode
	 * @return boolean
	 */
	public static boolean saveDefaultCity(Context context, long cityCode) {
		CityBean city = getCityByCityCode(context, cityCode);
		if (city != null) {
			Uri uri = UriHelper.getUri(DbProvider.DEFAULT_CITY_TABLE);

			ContentValues values = new ContentValues();
			values.put(DefaultCityTable.CITY_NAME, city.cityName);
			values.put(DefaultCityTable.CITY_CODE, city.cityCode);
			values.put(DefaultCityTable.PROVINCE_NAME, city.provinceName);
			values.put(DefaultCityTable.PROVINCE_CODE, city.provinceCode);

			context.getContentResolver().update(uri, values, null, null);

			return true;
		}
		return false;
	}

	/**
	 * get default city code
	 * 
	 * @param context
	 * @return city code
	 */
	public static long getCityCodeFromDefaultCity(Context context) {
		Uri uri = UriHelper.getUri(DbProvider.DEFAULT_CITY_TABLE);
		Cursor cursor = context.getContentResolver().query(uri,
				new String[] { DefaultCityTable.CITY_NAME }, null, null, null);
		if (cursor == null || !cursor.moveToFirst()) {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			return 0;
		}
		long code = cursor.getLong(0);
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return code;

	}

	/**
	 * get default city code
	 * 
	 * @param context
	 * @return city code
	 */
	public static CityBean getCityFromDefaultCity(Context context) {
		Uri uri = UriHelper.getUri(DbProvider.DEFAULT_CITY_TABLE);
		Cursor cursor = context.getContentResolver().query(uri,
				CityProjection.SUMMARY_PROJECTION, null, null, null);
		if (cursor == null || !cursor.moveToFirst()) {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			return null;
		}
		CityBean city = new CityBean();
		city.cityCode = cursor.getLong(CityProjection.CITY_CODE);
		city.cityName = cursor.getString(CityProjection.CITY_NAME);
		city.provinceCode = cursor.getLong(CityProjection.PROVINCE_CODE);
		city.provinceName = cursor.getString(CityProjection.PROVINCE_NAME);
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return city;

	}
}
