package de.janhilbig.hawcoursecoach.Activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.altbeacon.beacon.BeaconManager;

import java.sql.SQLException;
import java.util.ArrayList;

import de.janhilbig.hawcoursecoach.Forms.CreateSemesterActivity;
import de.janhilbig.hawcoursecoach.MsSeminarCoach;
import de.janhilbig.hawcoursecoach.R;
import de.janhilbig.hawcoursecoach.Adapter.SemesterArrayAdapter;
import de.janhilbig.hawcoursecoach.database.Semester;
import de.janhilbig.hawcoursecoach.database.SemesterDataSource;


public class ShowSemesterActivity extends ActionBarActivity {
    // Semester data
    private ArrayList<Semester> values;
    private SemesterArrayAdapter adapter;
    private SemesterDataSource dataSource;
    private Semester semester;
    // EXTRA data
    final static String SEMESTER_ID = "semester_id";
    // Beacon Manager
    public BeaconManager beaconManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_semester);
        beaconManager = BeaconManager.getInstanceForApplication(this);

        verifyBluetooth();
        initActivity();
    }

    public void initActivity() {
        // Find ListView
        final ListView listView = (ListView) findViewById(R.id.listView);
        // Set text if ListView is empty
        listView.setEmptyView(findViewById(R.id.empty_semesterlist));
        // Open database
        dataSource = new SemesterDataSource(this);
        try {
            dataSource.open();
        } catch (SQLException dbException){
            Log.w("dbError", "Datenbankfehler: " + dbException);
        }
        // Get all semesters
        values = (ArrayList<Semester>) dataSource.getAllSemester();

        // Set SemesterArrayAdapter for listview
        adapter = new SemesterArrayAdapter(this, values);
        listView.setAdapter(adapter);

        // Select on click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(ShowSemesterActivity.this, ShowSeminarActivity.class);
                // Select Semester
                semester = null;
                semester = (Semester) listView.getAdapter().getItem(position);
                long semester_id = semester.getId();
                intent.putExtra(SEMESTER_ID, semester_id);
                startActivity(intent);
            }
        });

        // Delete on longClick
        listView.setLongClickable(true);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View v, final int position, long id) {
                // Select Semester
                semester = null;
                semester = (Semester) listView.getAdapter().getItem(position);
                // Alert dialog
                AlertDialog.Builder alert = new AlertDialog.Builder(ShowSemesterActivity.this);
                alert.setTitle("Semester löschen?");
                alert.setMessage("Bist du sicher, dass du den Eintrag "
                        + "\""
                        + semester.getTitle()
                        + "\""
                        + " löschen möchtest?");

                alert.setPositiveButton("Ja", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Delete Semester from Database and Adapter
                        dataSource.deleteSemester(semester);
                        adapter.remove(semester);
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

    // Action Bar Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_semester, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up saveButton, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.add_semester:
                addSemester();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    final static int REQUEST_CREATE_ACTIVITY = 0;
    private void addSemester(){
        // call create semester activity
        Intent intent = new Intent(this, CreateSemesterActivity.class);
        // Daten einer anderen Activity empfangen wenn diese beendet wird
        startActivityForResult(intent, REQUEST_CREATE_ACTIVITY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CREATE_ACTIVITY && resultCode == Activity.RESULT_OK) {
            Semester semester = data.getParcelableExtra(CreateSemesterActivity.NEW_SEMESTER);
            String title = semester.getTitle();
            String startdate = semester.getStartdate();
            String enddate = semester.getEnddate();
            try {
                dataSource.open();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            dataSource.createSemester(title, startdate, enddate);
            adapter.add(semester);
            adapter.notifyDataSetChanged();
        }
    }

    private void verifyBluetooth() {

        try {
            if (!BeaconManager.getInstanceForApplication(this).checkAvailability()) {
                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setTitle("Bluetooth nicht eingeschaltet");
                builder.setMessage("Bitte schalte zuerst Bluetooth ein und starte die Anwendung erneut.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                        System.exit(0);
                    }
                });
                builder.show();
            }
        }
        catch (RuntimeException e) {
            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Bluetooth LE nicht verfügbar.");
            builder.setMessage("Entschuldigung, dieses Gerät unterstützt kein Bluetooth LE.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    finish();
                    System.exit(0);
                }

            });
            builder.show();

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        ((MsSeminarCoach) this.getApplicationContext()).setMonitoringActivity(this);
        initActivity();
    }

    @Override
    protected void onPause() {
        ((MsSeminarCoach) this.getApplicationContext()).setMonitoringActivity(null);
        dataSource.close();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataSource.close();
        ((MsSeminarCoach) this.getApplicationContext()).setMonitoringActivity(null);
    }
}