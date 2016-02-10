package de.janhilbig.hawcoursecoach.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Data source for semester
public class SemesterDataSource {

    // Database fields
    private SQLiteDatabase database;
    private MyDBOpenHelper dbHelper;
    private String[] allColumns = {
            MyDBOpenHelper.SEMESTER_COLUMN_ID,
            MyDBOpenHelper.SEMESTER_COLUMN_TITLE,
            MyDBOpenHelper.SEMESTER_COLUMN_STARTDATE,
            MyDBOpenHelper.SEMESTER_COLUMN_ENDDATE
    };

    // Constructor
    public SemesterDataSource(Context context) {
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

    public String getTitleById(long semester_id){
        String title;
        Cursor cursor = database.query(MyDBOpenHelper.TABLE_SEMESTER,
                allColumns, MyDBOpenHelper.SEMESTER_COLUMN_ID + "='" + semester_id + "'", null,
                null, null, null);
        cursor.moveToFirst();
        Semester newSemester = cursorToSemester(cursor);
        title = newSemester.getTitle();
        cursor.close();

        return title;
    }

    // Database query create
    public Semester createSemester(String title, String startdate, String enddate) {
        ContentValues values = new ContentValues();
        values.put(MyDBOpenHelper.SEMESTER_COLUMN_TITLE, title);
        values.put(MyDBOpenHelper.SEMESTER_COLUMN_STARTDATE, startdate);
        values.put(MyDBOpenHelper.SEMESTER_COLUMN_ENDDATE, enddate);
        long insertId = database.insert(MyDBOpenHelper.TABLE_SEMESTER, null,
                values);
        Cursor cursor = database.query(MyDBOpenHelper.TABLE_SEMESTER,
                allColumns, MyDBOpenHelper.SEMESTER_COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Semester newSemester = cursorToSemester(cursor);
        cursor.close();
        return newSemester;
    }

    // Database query delete
    public void deleteSemester(Semester semester) {
        long id = semester.getId();
        Log.w(SemesterDataSource.class.getName(), "Semester mit der ID " + id + " gelöscht!");
        database.delete(MyDBOpenHelper.TABLE_SEMESTER, MyDBOpenHelper.SEMESTER_COLUMN_ID
                + " = " + id, null);
    }

    // Some list
    public List<Semester> getAllSemester() {
        List<Semester> semesters = new ArrayList<Semester>();

        Cursor cursor = database.query(MyDBOpenHelper.TABLE_SEMESTER,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Semester semester = cursorToSemester(cursor);
            semesters.add(semester);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return semesters;
    }

    private Semester cursorToSemester(Cursor cursor) {
        Semester semester = new Semester(null, null, null);
        semester.setId(cursor.getLong(0));
        semester.setTitle(cursor.getString(1));
        semester.setStartdate(cursor.getString(2));
        semester.setEnddate(cursor.getString(3));
        return semester;
    }
}
