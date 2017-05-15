package com.taha.alrehab.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class UserDBHandler extends SQLiteOpenHelper {
    public static final String TABLE_USER = "user";
    public static final String COLUMN_USERID = "userId";
    private static final String TAG = UserDBHandler.class.getSimpleName();


    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Usersdb.db";


    //We need to pass database information along to superclass
    public UserDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    public UserDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_USER + "(" +
                COLUMN_USERID + " varchar(50) PRIMARY KEY NOT NULL); ";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    //Add a new row to the database
    public void addUser(String userID) {
        try {

            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_USERID, userID);

            db.insert(TABLE_USER, null, values);
            db.close();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public int updateUser(String userID) {

        deleteUser(userID);
        addUser(userID);
        return 1;

    }


    public void deleteUser(String userID) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_USER + " WHERE " + COLUMN_USERID + "='" + userID + "';");
        db.close();
    }


    public String getUserId() {
        String userId = "";
        SQLiteDatabase db = getReadableDatabase();
        String query = "select * from " + TABLE_USER + ";";

        Cursor cursor = db.rawQuery(query, null);


        if (cursor != null) {

            if (cursor.moveToFirst()) {


                try {
                    userId = cursor.getString(cursor.getColumnIndex(COLUMN_USERID));

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }

            }
        }
        db.close();
        try {
            if (cursor != null)
                cursor.close();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return userId;
    }
}