package de.janhilbig.hawcoursecoach.Forms;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import de.janhilbig.hawcoursecoach.R;
import de.janhilbig.hawcoursecoach.database.Semester;


public class CreateSemesterActivity extends ActionBarActivity {
    // EXTRA_DATA
    public final static String NEW_SEMESTER = "new_semester";

    // datepicker objects
    private int pickYear, pickMonth, pickDay;
    // id's of datepicker
    static final int ANFANG_ID = 0;
    static final int ENDE_ID = 1;
    // Dates for check
    Date startDate, endDate;
    // Text input fields
    EditText semestertitel, semesteranfang, semesterende;
    // Calender icons
    ImageView calAnfang, calEnde;
    // save saveButton
    private Button saveButton;
    // Context for InputManager
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_semester);
        // Find all the views
        findViews();
        showDialogOnClick();
        // User can't input corruptet date values
        semesteranfang.setKeyListener(null);
        semesterende.setKeyListener(null);
        // Calender Object for startvalue of DatePicker
        final Calendar calendar = Calendar.getInstance();
        pickYear = calendar.get(Calendar.YEAR);
        pickMonth = calendar.get(Calendar.MONTH);
        pickDay = calendar.get(Calendar.DAY_OF_MONTH);
        // save saveButton
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSemester();
            }
        });

        // deactivate save-saveButton if one of the input fields are empty
        saveButton.setEnabled(false);
        myTextWatcher(semestertitel);
        myTextWatcher(semesteranfang);
        myTextWatcher(semesterende);


    }

    // TextWatcher
    private void myTextWatcher(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isEmpty(semestertitel) && !isEmpty(semesteranfang) && !isEmpty(semesterende)){
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

    private void saveSemester() {
        Semester semester = new Semester(semestertitel.getText().toString(),
                semesteranfang.getText().toString(), semesterende.getText().toString());
        // put extra item
        Intent resultIntent = new Intent();
        resultIntent.putExtra(NEW_SEMESTER, semester);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    private void findViews() {
        semestertitel = (EditText)findViewById(R.id.editSemestertitel);
        semesteranfang = (EditText)findViewById(R.id.editSemesteranfang);
        semesterende = (EditText)findViewById(R.id.editSemesterende);
        calAnfang = (ImageView)findViewById(R.id.imageCalAnfang);
        calEnde = (ImageView)findViewById(R.id.imageCalEnde);
        saveButton = (Button)findViewById(R.id.button_create);
    }

    public void showDialogOnClick() {
        calAnfang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(ANFANG_ID);
                hideSoftKeyboard(CreateSemesterActivity.this);
            }
        });
        calEnde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(ENDE_ID);
                hideSoftKeyboard(CreateSemesterActivity.this);
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == ANFANG_ID) {
            return new DatePickerDialog(this, dpickerListenerAnfang, pickYear, pickMonth, pickDay);
        }
        if (id == ENDE_ID) {
            return new DatePickerDialog(this, dpickerListenerEnde, pickYear, pickMonth, pickDay);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener dpickerListenerAnfang = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            pickYear = year;
            pickMonth = monthOfYear+1;
            pickDay = dayOfMonth;
            String date = checkDigit(pickDay)+"-"+checkDigit(pickMonth)+"-"+checkDigit(pickYear);
            semesteranfang.setText(date);
            startDate = new Date(pickYear, pickMonth, pickDay);
        }
    };

    private DatePickerDialog.OnDateSetListener dpickerListenerEnde = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            pickYear = year;
            pickMonth = monthOfYear+1;
            pickDay = dayOfMonth;
            String date = checkDigit(pickDay)+"-"+checkDigit(pickMonth)+"-"+checkDigit(pickYear);
            endDate = new Date(pickYear, pickMonth, pickDay);
            if (startDate != null){
                if (!endDate.before(startDate)) {
                    semesterende.setText(date);
                } else {
                    Toast.makeText(getApplicationContext(), "Ende vor Anfang!?", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Bitte zuerst das Anfangsdatum angeben!", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_semester, menu);
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

    // Check Picker Values and return 0-prefix for single digits
    public String checkDigit(int number) {
        return number <= 9? "0" + number:String.valueOf(number);
    }

    public void cancelActivity() {
        Intent cancelIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, cancelIntent);
        finish();
    }
}