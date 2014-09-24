package com.tian.weather.provider;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.tian.weather.statics.Statics;
import com.tian.weather.utils.EngLog;

public class DbProvider extends ContentProvider{
	
	private static final String TAG = "[CityWeather]DbProvider";
	/* URI authority string */
    public static final String AUTHORITY = "com.tian.weather.provider.dbprovider";
    
    /* URI paths names */
    public static final String PROVINCE_TABLE = "ProvinceTab";
    public static final String CITY_TABLE = "CityTab";
    public static final String DEFAULT_CITY_TABLE = "DefaultCityTab";
    public static final String WEATHER_TABLE = "WeatherTab";
    
    private DbHelper dbHelper;
    
    /* UriMatcher codes */
    private static final int PROVINCE_MATCH = 0;
    private static final int PROVINCE_MATCH_IDS = 1;
    private static final int CITYS_MATCH = 10;
    private static final int CITYS_MATCH_IDS = 11;
    private static final int WEATHERS_MATCH= 20;
    private static final int WEATHERS_MATCH_IDS = 21;
    private static final int DEFAULT_MATCH = 30;
    private static final int DEFAULT_MATCH_IDS = 31;
    
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
    	sUriMatcher.addURI(AUTHORITY, PROVINCE_TABLE, PROVINCE_MATCH);
        sUriMatcher.addURI(AUTHORITY, PROVINCE_TABLE + "/#", PROVINCE_MATCH_IDS);
        sUriMatcher.addURI(AUTHORITY, CITY_TABLE, CITYS_MATCH);
        sUriMatcher.addURI(AUTHORITY, CITY_TABLE + "/#", CITYS_MATCH_IDS);
        sUriMatcher.addURI(AUTHORITY, WEATHER_TABLE, WEATHERS_MATCH);
        sUriMatcher.addURI(AUTHORITY, WEATHER_TABLE + "/#", WEATHERS_MATCH_IDS);
        sUriMatcher.addURI(AUTHORITY, DEFAULT_CITY_TABLE, DEFAULT_MATCH);
        sUriMatcher.addURI(AUTHORITY, DEFAULT_CITY_TABLE + "/#", DEFAULT_MATCH_IDS);
        
    }

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		if (Statics.DEBUG) {
            EngLog.d(TAG, "delete(" + uri + ", ...)");
        }
        int match = sUriMatcher.match(uri);
        if (match == UriMatcher.NO_MATCH) {
            EngLog.e(TAG, "delete(): Wrong URI: " + uri);
            throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        if (uriWithID(match)) {
            selection = BaseColumns._ID + "=" + uri.getLastPathSegment() + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
        }

        SQLiteDatabase db;
        try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLiteException e) {
            //TODO Implement proper error handling
                EngLog.e(TAG, "delete(): Error opening writable database " + e);
            
            throw e;
        }

        int count;
        synchronized (this) {
            try {
                count = db.delete(tableName(match), selection, selectionArgs);
            } catch (SQLException e) {
                //TODO Implement proper error handling
                    EngLog.e(TAG, "delete(): DB rows delete error " + e);
                
                throw e;
            }
        }

        if (Statics.DEBUG)
            EngLog.d(TAG, "delete(): " + count + " rows deleted");

        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		if(Statics.DEBUG){
			EngLog.d(TAG, "insert(" + uri + ", ...)");
		}
		int match = sUriMatcher.match(uri);
		if (match == UriMatcher.NO_MATCH) {
            EngLog.e(TAG, "insert(): Wrong URI: " + uri);
            throw new IllegalArgumentException("Wrong URI: " + uri);
        }
		
		if (uriWithID(match)) {
            EngLog.e(TAG, "insert(): Insert not allowed for this URI: " + uri);
            throw new IllegalArgumentException("Insert not allowed for this URI: " + uri);
        }
		
		SQLiteDatabase db;
        long rowId;

        try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLiteException e) {
            // TODO Implement proper error handling
                EngLog.e(TAG, "insert(): Error opening writeable database" + e);
            throw e;
        }
        
        synchronized (this) {
            try {
                rowId = db.insert(tableName(match), null, values);
            } catch (SQLException e) {
                // TODO Implement proper error handling
                    EngLog.e(TAG, "Insert() failed " + e);
                
                throw e;
            }
        }
        
        if (rowId <= 0) {
                EngLog.e(TAG, "insert(): Error: insert() returned " + rowId);
            throw new RuntimeException("DB insert failed");
        }
        
        uri = ContentUris.withAppendedId(UriHelper.removeQuery(uri), rowId);
        if (Statics.DEBUG) {
            EngLog.d(TAG, "insert(): new uri with rowId: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

		return uri;
	}

	@Override
	public boolean onCreate() {
		dbHelper = new DbHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		if (Statics.DEBUG) {
            EngLog.d(TAG, "query(" + uri + ",...)");
        }
        int match = sUriMatcher.match(uri);
        if (match == UriMatcher.NO_MATCH) {
                EngLog.e(TAG, "query(): Wrong URI");
            throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        int where_append_count = 0;
        qb.setTables(tableName(match));
        if (uriWithID(match)) {
            qb.appendWhere((where_append_count++ == 0 ? "" : " AND ") + (BaseColumns._ID + "=" + uri.getLastPathSegment()));
        }

        if (TextUtils.isEmpty(sortOrder)) {
            sortOrder = "_ID ASC";
        }

        SQLiteDatabase db;
        try {
            db = dbHelper.getReadableDatabase();
        } catch (SQLiteException e) {
            //TODO Implement proper error handling
                EngLog.e(TAG, "query(): Error opening readable database " + e);
            
            throw e;
        }

        Cursor cursor;
        synchronized (this) {
            try {
                cursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            } catch (Throwable e) {
                    EngLog.e(TAG, "query(): Exception at db query " + e);
                
                throw new RuntimeException("Exception at db query: " + e.getMessage());
            }
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        if (cursor == null) {
            if (Statics.DEBUG) {
                EngLog.d(TAG, "query(): null cursor returned from db query");
            }
        }

        if (Statics.DEBUG) {
            EngLog.d(TAG, "query(): Cursor has " + cursor.getCount() + " rows");
        }
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		if (Statics.DEBUG) {
            EngLog.d(TAG, "update(" + uri + ", ...)");
        }
        int match = sUriMatcher.match(uri);
        if (match == UriMatcher.NO_MATCH) {
            EngLog.e(TAG, "update(): Wrong URI: " + uri);
            throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        if (uriWithID(match)) {
            selection = BaseColumns._ID + "=" + uri.getLastPathSegment() + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
        }

        SQLiteDatabase db;
        try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLiteException e) {
            //TODO Implement proper error handling
            EngLog.e(TAG, "update(): Error opening writable database " + e);
            
            throw e;
        }

        int count;
        synchronized (this) {
            try {
                count = db.update(tableName(match), values, selection, selectionArgs);
            } catch (SQLException e) {
                //TODO Implement proper error handling
                EngLog.e(TAG, "update() failed " + e);
                
                throw e;
            }
        }

        if (Statics.DEBUG) {
            EngLog.d(TAG, "update(): " + count + " rows updated");
        }

        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
	}

	private String tableName(int uri_match) {
		switch (uri_match) {
		case PROVINCE_MATCH:
			return PROVINCE_TABLE;
		case CITYS_MATCH:
			return CITY_TABLE;
		case WEATHERS_MATCH:
			return WEATHER_TABLE;
		case DEFAULT_MATCH:
			return DEFAULT_CITY_TABLE;
		default:
			throw new Error(TAG + " No table defined for #" + uri_match);
		}
	}

	private boolean uriWithID(int uri_match) {
		switch (uri_match) {
		case PROVINCE_MATCH_IDS:
		case CITYS_MATCH_IDS:
		case WEATHERS_MATCH_IDS:
		case DEFAULT_MATCH_IDS:
			return true;

		default:
			return false;
		}
	}

    
}
