package com.indiansportsnews.attendanceapp;

import android.provider.BaseColumns;

public final class AttendanceDbContract {
    public AttendanceDbContract() {}

    public static abstract class Entry implements BaseColumns {
        public static final String TABLE_NAME = "attendance" ;
        public static final String COLUMN_NAME_ID = "id" ;
        public static final String COLUMN_NAME_NAME = "name" ;
        public static final String COLUMN_NAME_ATT = "att" ;
        public static final String COLUMN_NAME_TOT_ATT = "tot_att" ;
        public static final String COLUMN_NAME_POINTS = "points" ;
        public static final String COLUMN_NAME_TOT_POINTS = "tot_points" ;
    }
}