package com.tian.weather.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.tian.weather.statics.Statics;
import com.tian.weather.utils.EngLog;

public class DbUtils {
	 private static final String TAG = "[CityWeather]DbUtils";
	    
	    private static final String SQLITE_STMT_LIST_TABLES =
	        "SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%' AND name NOT LIKE 'android%'";
	    private static final String SQLITE_TABLE_NAME_COLUMN = "name";
	    private static final String SQLITE_STMT_TEMPLATE_LIST_COLUMNS = "SELECT * FROM %s LIMIT 1";
	    private static final String SQLITE_STMT_TEMPLATE_DROP_TABLE = "DROP TABLE IF EXISTS %s";
	    private static final String SQLITE_STMT_TEMPLATE_RENAME_TABLE = "ALTER TABLE %s RENAME TO %s";
	    private static final String SQLITE_STMT_TEMPLATE_COPY_COLUMNS = "INSERT INTO %s (%s) SELECT %s FROM %s";

	    
	    /**
	     * @param db
	     * @return Collection object containing table names in the database 
	     */
	    static Collection<String> listTables(SQLiteDatabase db) {
	        if (Statics.DEBUG) {
	            EngLog.d(TAG, "listTables()...");
	        }

	        Cursor cursor = db.rawQuery(SQLITE_STMT_LIST_TABLES, null);
	        if (cursor == null || !cursor.moveToFirst()) {
	            if (Statics.DEBUG) {
	                EngLog.d(TAG, "listTables(): no tables in the database; return null");
	            }

	            if (cursor != null) {
	                cursor.close();
	            }
	            return null;
	        }
	        
	        int table_name_column = cursor.getColumnIndex(SQLITE_TABLE_NAME_COLUMN);
	        HashSet<String> tables = new HashSet<String>(cursor.getCount());
	        do {
	            tables.add(cursor.getString(table_name_column));
	        } while (cursor.moveToNext());
	        cursor.close();

	        if (Statics.DEBUG) {
	           EngLog.d(TAG, "listTables(): " + tables);
	        }
	        
	        return tables;
	    }

	    
	    /**
	     * @param db
	     * @param table
	     * @return List of column names in the DB table
	     */
	    static List<String> listColumns(SQLiteDatabase db, String table) {
	        if (Statics.DEBUG) {
	            EngLog.d(TAG, "listColumns(" + table + ")...");
	        }
	        
	        Cursor cursor = db.rawQuery(String.format(SQLITE_STMT_TEMPLATE_LIST_COLUMNS, table), null);
	        if (cursor == null) {
	            if (Statics.DEBUG) {
	                EngLog.d(TAG, "listColumns(" + table + "): no columns in table " + table);
	            }
	            return null;
	        }
	        
	        List<String> columns = Arrays.asList(cursor.getColumnNames());
	        cursor.close();

	        return columns;
	    }
	    
	    
	    /**
	     * @param db
	     * @param table
	     */
	    static void dropTable(SQLiteDatabase db, String table) {
	        if (Statics.DEBUG) {
	            EngLog.d(TAG, "dropTable(" + table + ")...");
	        }

	        db.execSQL(String.format(SQLITE_STMT_TEMPLATE_DROP_TABLE, table));
	    }
	    

	    /**
	     * @param db
	     * @param oldName
	     * @param newName
	     */
	    static void renameTable(SQLiteDatabase db, String oldName, String newName) {
	        if (Statics.DEBUG) {
	            EngLog.d(TAG, "renameTable(" + oldName + ", " + newName + ")...");
	        }

	        db.execSQL(String.format(SQLITE_STMT_TEMPLATE_RENAME_TABLE, oldName, newName));
	    }
	    
	    
	    /**
	     * Copies the content of the matching columns from the old table to the new one during table upgrade
	     * New columns (i.e. those which did not exist in the old DB scheme)
	     * are filled with the default column values according to the table scheme,
	     * or with NULL if no default value is specified.
	     *  
	     * @param db
	     * @param oldTable
	     * @param newTable
	     */
	    static void joinColumns(SQLiteDatabase db, String oldTable, String newTable) {
	        if (Statics.DEBUG) {
	            EngLog.d(TAG, "joinColumns(" + oldTable + ", " + newTable + ")...");
	        }
	        
	        //Delete all records in the new table before copying from the old table
	        db.delete(newTable, null, null);
	        
	        //Find columns which exist in both tables
	        ArrayList<String> old_columns = new ArrayList<String>(listColumns(db, oldTable));
	        List<String> new_columns = listColumns(db, newTable);
	        old_columns.retainAll(new_columns);

	        String common_columns = TextUtils.join(",", old_columns);
	        if (Statics.DEBUG) {
	            EngLog.d(TAG, "joinColumns: Common columns: " + common_columns);
	        }
	        
	        //Copy records from old table to new table
	        db.execSQL(String.format(SQLITE_STMT_TEMPLATE_COPY_COLUMNS, newTable, common_columns, common_columns, oldTable));
	    }

}
