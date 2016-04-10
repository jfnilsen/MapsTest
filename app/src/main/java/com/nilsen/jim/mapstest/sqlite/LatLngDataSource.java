package com.nilsen.jim.mapstest.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.gms.maps.model.LatLng;

import java.sql.SQLException;
import java.util.ArrayList;

public class LatLngDataSource {

        private SQLiteDatabase database = null;
        private SqliteHelper dbhelper = null;

        public LatLngDataSource(Context context){
            dbhelper = new SqliteHelper(context);
        }

        public void open() throws SQLException {
            if(dbhelper!=null)database = dbhelper.getWritableDatabase();
        }
        public void close(){
            dbhelper.close();
        }

        public void createLatLngData(double lat, double lng){

            ContentValues values = new ContentValues();
            values.put(SqliteHelper.KEY_LATITUDE, lat);
            values.put(SqliteHelper.KEY_LONGDITUDE, lng);
            try {
                open();
                database.insert(SqliteHelper.LATLNG_TABLE, null, values);
            } catch (SQLException e) {
                e.printStackTrace();
            }finally {
                close();
            }

        }

        public void deleteAllStoredData(){
            try {
                open();
                database.delete(SqliteHelper.LATLNG_TABLE, null, null);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                close();
            }

        }

        public ArrayList<LatLng> getAllLatLngPositions() {
            try {
                open();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            SQLiteDatabase db = dbhelper.getReadableDatabase();
            ArrayList<LatLng> latLngs = new ArrayList<>();

            String[] projection = {SqliteHelper.KEY_PRIMARY_ID, SqliteHelper.KEY_LATITUDE, SqliteHelper.KEY_LONGDITUDE};

            String sortOrder = SqliteHelper.KEY_PRIMARY_ID + " ASC";

            Cursor cursor = db.query(
                    SqliteHelper.LATLNG_TABLE, projection, null, null,
                    null, null, sortOrder);
            cursor.moveToFirst();

            while (!cursor.isLast() && !cursor.isBeforeFirst()) {
                double lat = cursor.getDouble(cursor.getColumnIndexOrThrow(SqliteHelper.KEY_LATITUDE));
                double lng = cursor.getDouble(cursor.getColumnIndexOrThrow(SqliteHelper.KEY_LONGDITUDE));
                latLngs.add(new LatLng(lat,lng));

                cursor.moveToNext();
            }
            close();
            return latLngs;
        }

}
