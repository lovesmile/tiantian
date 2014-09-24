package com.tian.weather;

import com.tian.weather.provider.DbDataStore.WeatherTable;


public class WeatherProjection {

	public static final String[] SUMMARY_PROJECTION = new String[] {
		WeatherTable.ID,                  //0
		WeatherTable.PROVINCE_NAME,       //1
		WeatherTable.CITY_NAME,           //2 
		WeatherTable.CITY_CODE,           //3
		WeatherTable.UPDATE_TIME,         //4
		WeatherTable.TODAY_NOW_WEATHER,   //5
		WeatherTable.TODAY_AIR,           //6
		WeatherTable.TODAY_CONTENT,       //7
		WeatherTable.TODAY_WEATHER,       //8
		WeatherTable.TODAY_TEMP,          //9
		WeatherTable.TODAY_WIND,          //10
		WeatherTable.TODAY_IMG1,          //11
		WeatherTable.TODAY_IMG2,          //12
		WeatherTable.AFTER_ONE_WEATHER,   //13
		WeatherTable.AFTER_ONE_TEMP,      //14
		WeatherTable.AFTER_ONE_WIND,      //15
		WeatherTable.AFTER_ONE_IMG1,      //16
		WeatherTable.AFTER_ONE_IMG2,      //17
		WeatherTable.AFTER_TWO_WEATHER,   //18
		WeatherTable.AFTER_TWO_TEMP,      //19
		WeatherTable.AFTER_TWO_WIND,      //20
		WeatherTable.AFTER_TWO_IMG1,      //21
		WeatherTable.AFTER_TWO_IMG2,      //22
		WeatherTable.AFTER_THREE_WEATHER, //23
		WeatherTable.AFTER_THREE_TEMP,    //24
		WeatherTable.AFTER_THREE_WIND,    //25
		WeatherTable.AFTER_THREE_IMG1,    //26
		WeatherTable.AFTER_THREE_IMG2,    //27
		WeatherTable.AFTER_FOUR_WEATHER,  //28
		WeatherTable.AFTER_FOUR_TEMP,     //29
		WeatherTable.AFTER_FOUR_WIND,     //30
		WeatherTable.AFTER_FOUR_IMG1,     //31
		WeatherTable.AFTER_FOUR_IMG2      //32
	};
	
	public static final int ID = 0;
	public static final int PROVINCE_NAME = 1; 
	public static final int CITY_NAME = 2; 
	public static final int CITY_CODE = 3; 
	public static final int UPDATE_TIME = 4; 
	public static final int TODAY_NOW_WEATHER = 5; 
	public static final int TODAY_AIR = 6;
	public static final int TODAY_CONTENT = 7;
	public static final int TODAY_WEATHER = 8;
	public static final int TODAY_TEMP = 9;
	public static final int TODAY_WIND = 10;
	public static final int TODAY_IMG1 = 11;
	public static final int TODAY_IMG2 = 12;
	public static final int AFTER_ONE_WEATHER = 13;
	public static final int AFTER_ONE_TEMP = 14;
	public static final int AFTER_ONE_WIND = 15;
	public static final int AFTER_ONE_IMG1 = 16;
	public static final int AFTER_ONE_IMG2 = 17;
	public static final int AFTER_TWO_WEATHER = 18;
	public static final int AFTER_TWO_TEMP = 19;
	public static final int AFTER_TWO_WIND = 20;
	public static final int AFTER_TWO_IMG1 = 21;
	public static final int AFTER_TWO_IMG2 = 22;
	public static final int AFTER_THREE_WEATHER = 23;
	public static final int AFTER_THREE_TEMP = 24;
	public static final int AFTER_THREE_WIND = 25;
	public static final int AFTER_THREE_IMG1 = 26;
	public static final int AFTER_THREE_IMG2 = 27;
	public static final int AFTER_FOUR_WEATHER = 28;
	public static final int AFTER_FOUR_TEMP = 29;
	public static final int AFTER_FOUR_WIND = 30;
	public static final int AFTER_FOUR_IMG1 = 31;
	public static final int AFTER_FOUR_IMG2 = 32;	
}
