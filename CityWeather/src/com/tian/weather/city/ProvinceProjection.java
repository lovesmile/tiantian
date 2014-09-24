package com.tian.weather.city;

import com.tian.weather.provider.DbDataStore.ProvinceTable;

public class ProvinceProjection {
	public static final String[] SUMMARY_PROJECTION = new String[] {
		ProvinceTable.ID,
		ProvinceTable.PROVINCE_CODE,
		ProvinceTable.PROVINCE_NAME,
	};
	
	public static final int ID_INDX = 0;
	public static final int PROVINCE_CODE = 1;
	public static final int PROVINCE_NAME = 2;

}
