package com.example.assignment2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "Location.db", null, 1);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create Table Location(id INTEGER PRIMARY KEY AUTOINCREMENT, address TEXT, latitude REAL, longitude REAL)");
    }

    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop Table if exists Location");
    }

    public void deleteTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("drop Table if exists Location");
    }

    //Inserts the address, latitude, and longitude into the database
    public Boolean insertData(String address, double latitude, double longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("address", address);
        contentValues.put("latitude", latitude);
        contentValues.put("longitude", longitude);
        long result = db.insert("Location", null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    //Returns all rows in the table
    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select address, latitude, longitude from Location", null);
        return cursor;
    }

    //Returns all rows that contain the queried address
    public Cursor query(String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select address, latitude, longitude from Location WHERE address LIKE ?", new String[]{"%"+address+"%"});
        return cursor;
    }

    //Returns the corresponding id for an address
    public int getId(String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select id from Location WHERE address = ?", new String[]{address});
        cursor.moveToNext();
        return cursor.getInt(0);
    }

    //Checks if an address is already in the database
    public Boolean checkAddress(String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from Location WHERE address = ?", new String[]{address});
        return (cursor.getCount() <= 0);
    }

    //Gets the address, latitude, and longitude for a given row id
    public Cursor queryId(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select address, latitude, longitude from Location WHERE id = " + id, null);
        return cursor;
    }

    //Deletes a row corresponding the given id
    public void deleteLocation(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM Location WHERE id = " + id);
    }

    //Updated the address, latitude, and longitude for a given row id
    public void updateLocation(int id, String address, double latitude, double longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE Location SET address = ?, latitude = " + latitude + ", longitude = " + longitude + " WHERE id = " + id, new String[]{address});
    }
}
