package de.janhilbig.hawcoursecoach.Activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import de.janhilbig.hawcoursecoach.MsSeminarCoach;
import de.janhilbig.hawcoursecoach.R;
import de.janhilbig.hawcoursecoach.Adapter.SeminarArrayAdapter;


public class HawMapActivity extends ActionBarActivity {

    private HawMap hawMap;
    private long room_id;
    private long checkIn_id;
    private int color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hawMap = new HawMap(this);
        // Get room_id
        Intent getIntent = getIntent();
        room_id = getIntent.getLongExtra(SeminarArrayAdapter.ROOM_ID, 0);
        color = getIntent.getIntExtra(SeminarArrayAdapter.COLOR_ID, 0);
        //Toast.makeText(this, "Room ID:" + room_id, Toast.LENGTH_SHORT).show();
        // Get room_id from CheckInSystem
        Intent getCheckIn = getIntent();
        checkIn_id = getCheckIn.getLongExtra(MsSeminarCoach.ROOM_ID, 0);
        color = getCheckIn.getIntExtra(MsSeminarCoach.COLOR_ID, 0);
        //Toast.makeText(this, "CheckInRoom ID:" + checkIn_id, Toast.LENGTH_SHORT).show();
        hawMap.setMyColor(color);
        hawMap.switchRooms(room_id);
        hawMap.switchRooms(checkIn_id);
        setContentView(hawMap);
    }

    @Override
    public void onNewIntent(Intent newIntent) {
        this.setIntent(newIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        hawMap.pause();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hawMap.resume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_haw_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up saveButton, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}