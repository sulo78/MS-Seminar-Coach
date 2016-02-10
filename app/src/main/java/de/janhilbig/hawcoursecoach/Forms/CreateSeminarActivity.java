package de.janhilbig.hawcoursecoach.Forms;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import de.janhilbig.hawcoursecoach.R;
import de.janhilbig.hawcoursecoach.Activities.ShowSeminarActivity;
import de.janhilbig.hawcoursecoach.database.Room;
import de.janhilbig.hawcoursecoach.database.Seminar;
import de.janhilbig.hawcoursecoach.database.Weekday;


public class CreateSeminarActivity extends ActionBarActivity implements TimePickerDialog.OnTimeSetListener {
    // TimePicker values
    int pickerStartHour, pickerStartMin;
    int pickerEndHour, pickerEndMin;
    int pickerID;
    private boolean startSet;
    // Text input fields
    EditText editTitle, editStart, editEnd;
    // Spinners
    Spinner daySpinner, roomSpinner;
    // Save button
    private Button saveButton;
    // Get semester_id from intent
    private long semester_id;
    // Put Extra
    public final static String NEW_SEMINAR = "new_seminar";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_seminar);
        // Get semester id
        Intent intent = getIntent();
        semester_id = intent.getLongExtra(ShowSeminarActivity.SEMESTER_ID, 0);
        Toast.makeText(this, "Semester ID:" + semester_id, Toast.LENGTH_SHORT).show();
        // Find views
        findViews();
        // Configure time input fields to force time picker use
        editStart.setKeyListener(null);
        editEnd.setKeyListener(null);
        startSet = false;
        // Init spinners
        initRoomSpinner();
        initDaySpinner();
        // Disable save saveButton when one of the text fields is empty
        saveButton.setEnabled(false);
        myTextWatcher(editTitle);
        myTextWatcher(editStart);
        myTextWatcher(editEnd);
        // Listener for save saveButton
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSeminar();
            }
        });
    }

    private void saveSeminar() {
        Seminar seminar = new Seminar(editTitle.getText().toString(), daySpinner.getSelectedItemId()+1,
                editStart.getText().toString(), editEnd.getText().toString(), roomSpinner.getSelectedItemId(), semester_id);
        // put extra item
        Intent resultIntent = new Intent();
        resultIntent.putExtra(NEW_SEMINAR, seminar);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    // TextWatcher
    private void myTextWatcher(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isEmpty(editTitle) && !isEmpty(editStart) && !isEmpty(editEnd)) {
                    saveButton.setEnabled(true);
                } else saveButton.setEnabled(false);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }


    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() >= 1) {
            return false;
        } else {
            return true;
        }
    }

    public void findViews() {
        editTitle = (EditText) findViewById(R.id.seminarTitel);
        editStart = (EditText) findViewById(R.id.seminarBegin);
        editEnd = (EditText) findViewById(R.id.seminarEnd);
        daySpinner = (Spinner) findViewById(R.id.spinnerDay);
        roomSpinner = (Spinner) findViewById(R.id.spinnerRoom);
        saveButton = (Button) findViewById(R.id.buttonSave);
    }

    public void setStart(View v) {
        pickerID = 1;
        hideSoftKeyboard(this);
        TimePickerFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public void setEnd(View v) {
        if (!startSet){
            hideSoftKeyboard(this);
            Toast.makeText(this, "Bitte zuerst die Startzeit angeben!", Toast.LENGTH_SHORT).show();
        } else {
            pickerID = 2;
            hideSoftKeyboard(this);
            TimePickerFragment newFragment = new TimePickerFragment();
            newFragment.show(getFragmentManager(), "timePicker");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_seminar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up saveButton, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                cancelActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (pickerID == 1) {
            pickerStartHour = hourOfDay;
            pickerStartMin = minute;
            editStart.setText(String.valueOf(checkDigit(pickerStartHour)) + ":" + String.valueOf(checkDigit(pickerStartMin) + " Uhr"));
            startSet = true;
        } else if (pickerID == 2){
            pickerEndHour = hourOfDay;
            pickerEndMin = minute;
            if (pickerEndHour > pickerStartHour) {
                editEnd.setText(String.valueOf(checkDigit(pickerEndHour)) + ":" + String.valueOf(checkDigit(pickerEndMin)) + " Uhr");
            } else if (pickerEndHour == pickerStartHour) {
                if (pickerEndMin > pickerStartMin) {
                    editEnd.setText(String.valueOf(checkDigit(pickerEndHour)) + ":" + String.valueOf(checkDigit(pickerEndMin)) + " Uhr");
                } else {
                    Toast.makeText(this, "Stupid!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Stupid!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Check Picker Values and return 0-prefix for single digits
    public String checkDigit(int number) {
        return number <= 9? "0" + number:String.valueOf(number);
    }

    public void initDaySpinner() {
        // Create Array Adapter
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, Weekday.getWeekdays());
        // Drop down layout style
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Attaching data adapter to spinner
        daySpinner.setAdapter(dayAdapter);
        // Set default value to monday
        daySpinner.setSelection(1);
    }

    public void initRoomSpinner() {
        // Creating adapter for spinner
        ArrayAdapter<String> roomAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, Room.getRoomTitles());
        // Drop down layout style
        roomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        roomSpinner.setAdapter(roomAdapter);
    }

    public void cancelActivity() {
        Intent cancelIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, cancelIntent);
        finish();
    }
}


