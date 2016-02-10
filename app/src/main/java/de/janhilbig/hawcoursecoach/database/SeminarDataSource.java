package de.janhilbig.hawcoursecoach.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Data source for seminar
public class SeminarDataSource {

    // Database fields
    private SQLiteDatabase database;
    private MyDBOpenHelper dbHelper;
    private String[] allColumns = {
            MyDBOpenHelper.SEMINARE_COLUMN_ID,
            MyDBOpenHelper.SEMINARE_COLUMN_TITLE,
            MyDBOpenHelper.SEMINARE_COLUMN_WEEKDAY,
            MyDBOpenHelper.SEMINARE_COLUMN_STARTTIME,
            MyDBOpenHelper.SEMINARE_COLUMN_ENDTIME,
            MyDBOpenHelper.SEMINARE_COLUM_ROOM_ID,
            MyDBOpenHelper.SEMINARE_COLUMN_SEMESTER_ID
    };

    // Constructor
    public SeminarDataSource(Context context) {
        // Init MyDBOpenHelper
        dbHelper = new MyDBOpenHelper(context);
    }

    // Open database
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    // Close database
    public void close() {
        dbHelper.close();
    }

    // Database query create
    public Seminar createSeminar(String title, long weekday, String starttime, String endtime, long room_id, long semester_id) {
        ContentValues values = new ContentValues();
        values.put(MyDBOpenHelper.SEMINARE_COLUMN_TITLE, title);
        values.put(MyDBOpenHelper.SEMINARE_COLUMN_WEEKDAY, weekday);
        values.put(MyDBOpenHelper.SEMINARE_COLUMN_STARTTIME, starttime);
        values.put(MyDBOpenHelper.SEMINARE_COLUMN_ENDTIME, endtime);
        values.put(MyDBOpenHelper.SEMINARE_COLUM_ROOM_ID, room_id);
        values.put(MyDBOpenHelper.SEMINARE_COLUMN_SEMESTER_ID, semester_id);
        long insertId = database.insert(MyDBOpenHelper.TABLE_SEMINARE, null,
                values);
        Cursor cursor = database.query(MyDBOpenHelper.TABLE_SEMINARE,
                allColumns, MyDBOpenHelper.SEMINARE_COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Seminar newSeminar = cursorToSeminar(cursor);
        cursor.close();
        return newSeminar;
    }

    // Database query delete
    public void deleteSeminar(Seminar seminar) {
        long id = seminar.getId();
        Log.w(SemesterDataSource.class.getName(), "Seminar mit der ID " + id + " gelöscht!");
        database.delete(MyDBOpenHelper.TABLE_SEMINARE, MyDBOpenHelper.SEMINARE_COLUMN_ID
                + " = " + id, null);
    }

    // Select all seminars
    public List<Seminar> getAllSeminars() {
        List<Seminar> seminars = new ArrayList<Seminar>();

        Cursor cursor = database.query(MyDBOpenHelper.TABLE_SEMINARE,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Seminar seminar = cursorToSeminar(cursor);
            seminars.add(seminar);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        // return the list
        return seminars;
    }

    // Select seminars with specific semester_id
    public List<Seminar> getSemesterSeminars(long semester_id) {
        List<Seminar> semesterSeminars = new ArrayList<Seminar>();

        Cursor cursor = database.query(MyDBOpenHelper.TABLE_SEMINARE,
                allColumns, MyDBOpenHelper.SEMINARE_COLUMN_SEMESTER_ID + "='" + semester_id + "'", null, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Seminar seminar = cursorToSeminar(cursor);
            semesterSeminars.add(seminar);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        // return the list
        return semesterSeminars;
    }

    // Convert cursor to seminar
    private Seminar cursorToSeminar(Cursor cursor) {
        Seminar seminar = new Seminar(null, 0, null, null, 0, 0);
        seminar.setId(cursor.getLong(0));
        seminar.setTitle(cursor.getString(1));
        seminar.setWeekday(cursor.getLong(2));
        seminar.setStarttime(cursor.getString(3));
        seminar.setEndtime(cursor.getString(4));
        seminar.setRoom_id(cursor.getLong(5));
        seminar.setSemester_id(cursor.getLong(6));
        return seminar;
    }
}