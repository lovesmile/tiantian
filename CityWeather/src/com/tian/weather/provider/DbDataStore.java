package com.tian.weather.provider;

import java.util.LinkedHashMap;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class DbDataStore {

	private static final String TAG = "[CityWeather]DbDataStore";
	
	public static final int DB_VERSION = 1;
	public static final String DB_NAME = "chinacity.db";

	public static final class ProvinceTable extends DbTable implements BaseColumns{
		
		private ProvinceTable(){};
		private static final ProvinceTable sInstance = new ProvinceTable();
		static ProvinceTable getInstance(){
			return sInstance;
		}
		public static final String TB_PROVINCE_NAME = "ProvinceTab";
		
		public static final String ID = "_id";
		public static final String PROVINCE_CODE = "ProvinceCode";
		public static final String PROVINCE_NAME = "ProvinceName";
		public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TB_PROVINCE_NAME + 
				"( " + 
				ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + 
				PROVINCE_CODE + " INTEGER UNIQUE ON CONFLICT REPLACE," + 
				PROVINCE_NAME + " TEXT" + 
				")";
		@Override
		String getName() {
			return TB_PROVINCE_NAME;
		}
		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE);
		}
	}
	
	public static final class CityTable extends DbTable implements BaseColumns{
		
		private CityTable(){};

		private static final CityTable sInstance = new CityTable();

		static CityTable getInstance() {
			return sInstance;
		}
		
		public static final String TB_CITY_NAME = "CityTab";
		
		public static final String ID = "_id";
		public static final String PROVINCE_CODE = "ProvinceCode";
		public static final String CITY_CODE = "CityCode";
		public static final String PROVINCE_NAME = "ProvinceName";
		public static final String CITY_NAME = "CityName";
		
		public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TB_CITY_NAME + 
				"( " + 
				ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + 
				PROVINCE_CODE + " INTEGER DEFAULT 0," + 
				CITY_CODE + " INTEGER UNIQUE ON CONFLICT REPLACE," +
				PROVINCE_NAME + " TEXT," + 
				CITY_NAME + " TEXT" + 
				")";

		@Override
		String getName() {
			return TB_CITY_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE);
		}
	}
	
	public static final class DefaultCityTable extends DbTable implements BaseColumns{
		private DefaultCityTable(){};
		private static final DefaultCityTable sInstance = new DefaultCityTable();
		static DefaultCityTable getInstance(){
			return sInstance;
		}
		
		public static final String TABLE_NAME = "DefaultCityTab";
		public static final String ID = "_id";
		public static final String PROVINCE_CODE = "ProvinceCode";
		public static final String PROVINCE_NAME = "ProvinceName";
		public static final String CITY_CODE = "CityCode";
		public static final String CITY_NAME = "CityName";
		
		public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
				"( " + 
				ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + 
				PROVINCE_CODE + " INTEGER DEFAULT 0," + 
				CITY_CODE + " INTEGER DEFAULT 0," +
				PROVINCE_NAME + " TEXT," + 
				CITY_NAME + " TEXT" + 
				" )";
		
		public static final String INIT_DEFAULT_CITY_TABLE =  "INSERT INTO " + TABLE_NAME + 
				" (" +
				PROVINCE_CODE + ',' +
				CITY_CODE + ',' + 
				PROVINCE_NAME + ',' +
				CITY_NAME +
				") " +
				"VALUES (0,0,NULL,NULL)" 
				;
		
		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE);
			db.execSQL(INIT_DEFAULT_CITY_TABLE);
		}
		
	}
	public static final class WeatherTable extends DbTable implements BaseColumns{
		
		private WeatherTable(){};
		private static final WeatherTable sInstance = new WeatherTable();
		static WeatherTable getInstance(){
			return sInstance;
		}
		public static final String TB_WEATHER_NAME = "WeatherTab";
		/*
		 * Information of city 
		 */
		public static final String ID = "_id";
		public static final String PROVINCE_NAME = "ProvinceName"; 
		public static final String CITY_NAME = "CityName"; 
		public static final String CITY_CODE = "CityCode"; 
		public static final String UPDATE_TIME = "UpdateTime"; 
		
		/*
		 * Today weather 
		 */
		public static final String TODAY_NOW_WEATHER = "TodayNowWeather"; 
		public static final String TODAY_AIR = "TodayAir";
		public static final String TODAY_CONTENT = "TodayContent";
		
		public static final String TODAY_WEATHER = "TodayWeather";
		public static final String TODAY_TEMP = "TodayTemp";
		public static final String TODAY_WIND = "TodayWind";
		public static final String TODAY_IMG1 = "TodayImg1";
		public static final String TODAY_IMG2 = "TodayImg2";
		
		/*
		 * Tomorrow weather 
		 */
		public static final String AFTER_ONE_WEATHER = "AfterOneWeather";
		public static final String AFTER_ONE_TEMP = "AfterOneTemp";
		public static final String AFTER_ONE_WIND = "AfterOneWind";
		public static final String AFTER_ONE_IMG1 = "AfterOneImg1";
		public static final String AFTER_ONE_IMG2 = "AfterOneImg2";
		
		/*
		 * Third day weather 
		 */
		public static final String AFTER_TWO_WEATHER = "AfterTwoWeather";
		public static final String AFTER_TWO_TEMP = "AfterTwoTemp";
		public static final String AFTER_TWO_WIND = "AfterTwoWind";
		public static final String AFTER_TWO_IMG1 = "AfterTwoImg1";
		public static final String AFTER_TWO_IMG2 = "AfterTwoImg2";
		
		/*
		 * Fourth day Tomorrow weather 
		 */
		public static final String AFTER_THREE_WEATHER = "AfterThreeWeather";
		public static final String AFTER_THREE_TEMP = "AfterThreeTemp";
		public static final String AFTER_THREE_WIND = "AfterThreeWind";
		public static final String AFTER_THREE_IMG1 = "AfterThreeImg1";
		public static final String AFTER_THREE_IMG2 = "AfterThreeImg2";
		
		/*
		 * Fifth day Tomorrow weather 
		 */
		public static final String AFTER_FOUR_WEATHER = "AfterFourWeather";
		public static final String AFTER_FOUR_TEMP = "AfterFourTemp";
		public static final String AFTER_FOUR_WIND = "AfterFourWind";
		public static final String AFTER_FOUR_IMG1 = "AfterFourImg1";
		public static final String AFTER_FOUR_IMG2 = "AfterFourImg2";

		public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TB_WEATHER_NAME + 
				"( " + 
				ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + 
				CITY_CODE + " INTEGER UNIQUE ON CONFLICT REPLACE," +
				PROVINCE_NAME + " TEXT," + 
				CITY_NAME + " TEXT," + 
				UPDATE_TIME + " TEXT," +
				TODAY_NOW_WEATHER + " TEXT," +
				TODAY_AIR + " TEXT," +
				TODAY_CONTENT + " TEXT," +
				TODAY_WEATHER + " TEXT," + 
				TODAY_TEMP + " TEXT," + 
				TODAY_WIND + " TEXT," + 
				TODAY_IMG1 + " INTEGER," + 
				TODAY_IMG2 + " INTEGER," +
				AFTER_ONE_WEATHER + " TEXT," + 
				AFTER_ONE_TEMP + " TEXT," + 
				AFTER_ONE_WIND + " TEXT," + 
				AFTER_ONE_IMG1 + " INTEGER," + 
				AFTER_ONE_IMG2 + " INTEGER," +
				AFTER_TWO_WEATHER + " TEXT," + 
				AFTER_TWO_TEMP + " TEXT," + 
				AFTER_TWO_WIND + " TEXT," + 
				AFTER_TWO_IMG1 + " INTEGER," + 
				AFTER_TWO_IMG2 + " INTEGER," +
				AFTER_THREE_WEATHER + " TEXT," + 
				AFTER_THREE_TEMP + " TEXT," + 
				AFTER_THREE_WIND + " TEXT," + 
				AFTER_THREE_IMG1 + " INTEGER," + 
				AFTER_THREE_IMG2 + " INTEGER," +
				AFTER_FOUR_WEATHER + " TEXT," +
				AFTER_FOUR_TEMP + " TEXT," +
				AFTER_FOUR_WIND + " TEXT," +
				AFTER_FOUR_IMG1 + " INTEGER," +
				AFTER_FOUR_IMG2 + " INTEGER" + 
				")";

		@Override
		String getName() {
			return TB_WEATHER_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE);
		}
	}
	
	static LinkedHashMap<String, DbTable> sDbTables = new LinkedHashMap<String, DbTable>();
	static{
		sDbTables.put(ProvinceTable.getInstance().getName(), ProvinceTable.getInstance());
		sDbTables.put(CityTable.getInstance().getName(), CityTable.getInstance());
		sDbTables.put(DefaultCityTable.getInstance().getName(), DefaultCityTable.getInstance());
		sDbTables.put(WeatherTable.getInstance().getName(), WeatherTable.getInstance());
	}
}
