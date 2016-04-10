package com.nilsen.jim.mapstest.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqliteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "mapstest_latlng.db";
    private static final int DATABASE_VERSION = 2;
    public static final String LATLNG_TABLE = "LatLngTable";

    public static final String KEY_PRIMARY_ID = "primary_id" ;
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGDITUDE = "longditude";

    private static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + LATLNG_TABLE + " (" + KEY_PRIMARY_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_LATITUDE + " DOUBLE, "
            + KEY_LONGDITUDE + " DOUBLE " + ");";


    public SqliteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + LATLNG_TABLE);
        onCreate(db);
    }

}
