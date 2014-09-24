package com.tian.weather.city;

import com.tian.weather.provider.DbDataStore.CityTable;


public class CityProjection {

	public static final String[] SUMMARY_PROJECTION = new String[] {
		CityTable.ID,
		CityTable.PROVINCE_CODE,
		CityTable.CITY_CODE,
		CityTable.PROVINCE_NAME,
		CityTable.CITY_NAME
	};
	
	public static final int ID_INDX = 0;
	public static final int PROVINCE_CODE = 1;
	public static final int CITY_CODE = 2;
	public static final int PROVINCE_NAME = 3;
	public static final int CITY_NAME = 4;
}
