package com.indiansportsnews.attendanceapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AttendanceDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1 ;
    public static final String DATABASE_NAME = "attendance.db" ;
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + AttendanceDbContract.Entry.TABLE_NAME + " (" +
                    AttendanceDbContract.Entry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," +
                    AttendanceDbContract.Entry.COLUMN_NAME_NAME + " VARCHAR(50) ," +
                    AttendanceDbContract.Entry.COLUMN_NAME_ATT + " INTEGER ," +
                    AttendanceDbContract.Entry.COLUMN_NAME_TOT_ATT + " INTEGER ," +
                    AttendanceDbContract.Entry.COLUMN_NAME_POINTS + " INTEGER ," +
                    AttendanceDbContract.Entry.COLUMN_NAME_TOT_POINTS + " INTEGER ) ;" ;

    private static final String SQL_DELETE_ENTRIES1 =
            "DROP TABLE IF EXISTS " + AttendanceDbContract.Entry.TABLE_NAME ;


    public AttendanceDbHelper(Context context) {
        super(context , DATABASE_NAME , null , DATABASE_VERSION) ;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES) ;
    }

    public void onUpgrade(SQLiteDatabase db , int oldVersion , int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES1) ;
        onCreate(db) ;
    }

    public void onDowngrade(SQLiteDatabase db , int oldVersion , int newVersion) {
        onUpgrade(db , oldVersion , newVersion) ;
    }
}