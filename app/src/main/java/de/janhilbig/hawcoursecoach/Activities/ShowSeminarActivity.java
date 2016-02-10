package de.janhilbig.hawcoursecoach.Activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;

import de.janhilbig.hawcoursecoach.Forms.CreateSeminarActivity;
import de.janhilbig.hawcoursecoach.R;
import de.janhilbig.hawcoursecoach.Adapter.SeminarArrayAdapter;
import de.janhilbig.hawcoursecoach.database.SemesterDataSource;
import de.janhilbig.hawcoursecoach.database.Seminar;
import de.janhilbig.hawcoursecoach.database.SeminarDataSource;


public class ShowSeminarActivity extends ActionBarActivity {

    // Store semester_id
    public final static String SEMESTER_ID = "semester_id";
    private long semester_id;
    // Seminar data vor ListView
    private ArrayList<Seminar> values;
    private SeminarArrayAdapter adapter;
    private SeminarDataSource dataSource;
    private Seminar seminar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_course);
        initActivity();
    }

    private void initActivity() {
        // Find ListView
        final ListView listView = (ListView) findViewById(R.id.listView);
        // Set text if ListView is empty
        listView.setEmptyView(findViewById(R.id.empty_courselist));
        // Get semester id
        Intent intent = getIntent();
        semester_id = intent.getLongExtra(ShowSemesterActivity.SEMESTER_ID, 1);
        Toast.makeText(this, "Semester ID:" + semester_id, Toast.LENGTH_SHORT).show();
        // Load semester title for activity title
        loadTitle();
        // Init data source
        dataSource = new SeminarDataSource(this);
        try {
            dataSource.open();
            Log.w("DATENBANK", "Datenbank geöffnet!");
        } catch (SQLException dbException){
            Log.w("dbError", "Datenbankfehler: " + dbException);
        }

        values = (ArrayList<Seminar>) dataSource.getSemesterSeminars(semester_id);

        adapter = new SeminarArrayAdapter(this, values);
        listView.setAdapter(adapter);

        // Delete on longClick
        listView.setLongClickable(true);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View v, final int position, long id) {
                // Select Semester
                seminar = null;
                seminar = (Seminar) listView.getAdapter().getItem(position);
                // Alert dialog
                AlertDialog.Builder alert = new AlertDialog.Builder(ShowSeminarActivity.this);
                alert.setTitle("Seminar löschen?");
                alert.setMessage("Bist du sicher, dass du den Eintrag "
                        + "\""
                        + seminar.getTitle()
                        + "\""
                        + " löschen möchtest?");

                alert.setPositiveButton("Ja", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Delete Semester from Database and Adapter
                        dataSource.deleteSeminar(seminar);
                        adapter.remove(seminar);
                        dialog.dismiss();
                    }
                });

                alert.setNegativeButton("Nein", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Just close the dialog
                        dialog.dismiss();
                    }
                });

                alert.show();

                return true;
            }
        });
    }

    private void loadTitle() {
        SemesterDataSource dataSource = new SemesterDataSource(this);
        try {
            dataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String title = dataSource.getTitleById(semester_id);
        getSupportActionBar().setTitle(title);
        dataSource.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_course, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up saveButton, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.add_seminar:
                addSeminar();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    final static int REQUEST_CREATE_ACTIVITY = 1;
    private void addSeminar() {
       Intent intent = new Intent(this, CreateSeminarActivity.class);
       intent.putExtra(SEMESTER_ID, semester_id);
       startActivityForResult(intent, REQUEST_CREATE_ACTIVITY);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CREATE_ACTIVITY && resultCode == Activity.RESULT_OK) {
            Seminar seminar = data.getParcelableExtra(CreateSeminarActivity.NEW_SEMINAR);
            String title = seminar.getTitle();
            long weekday = seminar.getWeekday();
            String start = seminar.getStarttime();
            String end = seminar.getEndtime();
            long room_id = seminar.getRoom_id();
            long semester_id = seminar.getSemester_id();

            try {
                dataSource.open();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            dataSource.createSeminar(title, weekday, start, end, room_id, semester_id);
            adapter.add(seminar);
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        initActivity();
    }

    @Override
    protected void onPause() {
        dataSource.close();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataSource.close();
    }
}
