package de.janhilbig.hawcoursecoach.database;

// SQLite Database configuration and creation

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDBOpenHelper extends SQLiteOpenHelper {

    // Table "Semester" description
    public static final String TABLE_SEMESTER = "Semester";
    public static final String SEMESTER_COLUMN_ID = "_id";
    public static final String SEMESTER_COLUMN_TITLE = "title";
    public static final String SEMESTER_COLUMN_STARTDATE = "startdate";
    public static final String SEMESTER_COLUMN_ENDDATE = "enddate";

    // Table "Seminare" description
    public static final String TABLE_SEMINARE = "Seminare";
    public static final String SEMINARE_COLUMN_ID = "_id";
    public static final String SEMINARE_COLUMN_TITLE = "title";
    public static final String SEMINARE_COLUMN_WEEKDAY = "weekday";
    public static final String SEMINARE_COLUMN_STARTTIME = "starttime";
    public static final String SEMINARE_COLUMN_ENDTIME = "endtime";
    public static final String SEMINARE_COLUM_ROOM_ID = "room_id";
    public static final String SEMINARE_COLUMN_SEMESTER_ID = "semester_id";

    // Database name and version
    private static final String DATABASE_NAME = "hawcoursecoach.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql-statement
    private static final String DATABASE_CREATE_SEMESTER = "create table " + TABLE_SEMESTER
            + "(" + SEMESTER_COLUMN_ID + " integer primary key autoincrement, "
            + SEMESTER_COLUMN_TITLE + " text not null,"
            + SEMESTER_COLUMN_STARTDATE + " text not null,"
            + SEMESTER_COLUMN_ENDDATE + " text not null);";

    private static final String DATABASE_CREATE_SEMINARE = "create table " + TABLE_SEMINARE
            + "(" + SEMINARE_COLUMN_ID + " integer primary key autoincrement, "
            + SEMINARE_COLUMN_TITLE + " text not null,"
            + SEMINARE_COLUMN_WEEKDAY + " integer not null,"
            + SEMINARE_COLUMN_STARTTIME + " text not null,"
            + SEMINARE_COLUMN_ENDTIME + " text not null,"
            + SEMINARE_COLUM_ROOM_ID + " integer not null,"
            + SEMINARE_COLUMN_SEMESTER_ID  + " integer not null);";

    // Constructor
    public MyDBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_SEMESTER);
        db.execSQL(DATABASE_CREATE_SEMINARE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MyDBOpenHelper.class.getName(),
                "Aktualisiere Datenbank von Version " + oldVersion + " auf "
                        + newVersion + ", dies wird alle Daten löschen.");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEMESTER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEMINARE);
    }
}