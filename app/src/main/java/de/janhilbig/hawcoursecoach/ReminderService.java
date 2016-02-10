package de.janhilbig.hawcoursecoach;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.janhilbig.hawcoursecoach.Activities.HawMapActivity;
import de.janhilbig.hawcoursecoach.database.Room;
import de.janhilbig.hawcoursecoach.database.Semester;
import de.janhilbig.hawcoursecoach.database.SemesterDataSource;
import de.janhilbig.hawcoursecoach.database.Seminar;
import de.janhilbig.hawcoursecoach.database.SeminarDataSource;

/**
 * Created by suloUser on 12.08.2015.
 */
public class ReminderService extends BroadcastReceiver {
    // Date for time conversion
    private Date date;
    // Time based events
    Calendar now = Calendar.getInstance();
    long today, seminar_start, seminar_end;
    // Set semester start and end
    Calendar storedSemesterStart = Calendar.getInstance();
    Calendar storedSemesterEnd = Calendar.getInstance();
    // Set a siminars date
    Calendar storedSeminarStart = Calendar.getInstance();
    Calendar storedSeminarEnd = Calendar.getInstance();
    // Create database helper
    private SemesterDataSource semesterDataSource;
    private SeminarDataSource seminarDataSource;
    // CheckIn System
    private List<String> myRooms;
    private List<String> checkInList;
    private long room_id, seminar_room;
    // Put extra IDs
    public static final String ROOM_ID = "room_id";
    public static final String COLOR_ID = "color_id";
    // Reminder Notification
    NotificationManager notificationManager;
    NotificationCompat.Builder builder;
    int numMessages;
    // Store Notification Count in Preferences
    public static final String PREFS_NAME = "de.janhilbig.hawcoursecoach.PREFS";
    public static final String KEY_COUNT = "notificationCount";
    public static final String LAST_UPDATE = "lastUpdate";
    SharedPreferences values;
    private int currentCount;
    private long lastUpdate;
    private long updateInterval;
    // NotificationID
    private long notifyID = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get Notification Count
        values = context.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
        currentCount = values.getInt(KEY_COUNT, 0);  // Sets to zero if not in prefs yet
        lastUpdate = values.getLong(LAST_UPDATE, 0); // Sets to zero if not in prefs yet
        Log.w("Last Update: ", getReadableTime(lastUpdate));
        updateInterval = 5*60*1000;

        // Get CheckIn List
        checkInList = intent.getStringArrayListExtra(MsSeminarCoach.CHECK_IN_LIST);
        // Get Rooms
        myRooms = Room.getRoomTitles();
        // Set now to current system time
        now.setTimeInMillis(System.currentTimeMillis());
        Log.w("REMINDER_SERVICE", "Now it's " + now.getTime());
        // Update last update in prefs
        if (now.getTimeInMillis() > lastUpdate + updateInterval) {
            lastUpdate = System.currentTimeMillis();
            currentCount = 0;
            SharedPreferences.Editor editor = values.edit();
            editor.putInt(KEY_COUNT, currentCount);
            editor.putLong(LAST_UPDATE, lastUpdate);
            editor.commit();
        }
        Log.w("CURRENT_COUNT", String.valueOf(currentCount));
        // Set todays weekday
        today = now.get(Calendar.DAY_OF_WEEK);

        // Init database connections
        semesterDataSource = new SemesterDataSource(context);
        seminarDataSource = new SeminarDataSource(context);
        try {
            semesterDataSource.open();
            seminarDataSource.open();
        } catch (SQLException dbException){
            Log.w("dbError", "Datenbankfehler: " + dbException);
        }
        List<Semester> semesterList = semesterDataSource.getAllSemester();

        // Start with looping through semesters
        for (int i = 0; i < semesterList.size(); i++ ) {
            long semesterstart;
            long semesterend;
            try {
                // Set Calender to stored start and end by parsing date-strings to millis
                semesterstart = dayToMillis(semesterList.get(i).getStartdate());
                storedSemesterStart.setTimeInMillis(semesterstart);
                Log.w("REMINDER_SERVICE", "Semesterstart: " + storedSemesterStart.toString());
                semesterend = dayToMillis(semesterList.get(i).getEnddate());
                storedSemesterEnd.setTimeInMillis(semesterend);
                Log.w("REMINDER_SERVICE", "Semesterende: " + storedSemesterEnd.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // Check if now is during current semester
            if (now.after(storedSemesterStart) && now.before(storedSemesterEnd)) {
                // Get semester id of current semester
                long semester_id = semesterList.get(i).getId();
                // Get seminars of current semester
                List<Seminar> seminarList = seminarDataSource.getSemesterSeminars(semester_id);
                // Start looping through semester seminars
                for (int j = 0; j < seminarList.size(); j++) {
                    // Check if day of seminar is today
                    if (today == seminarList.get(j).getWeekday()) {
                        // If day matches get start- and end-time
                        try {
                            seminar_start = createTimestamp(seminarList.get(j).getStarttime());
                            seminar_end = createTimestamp(seminarList.get(j).getEndtime());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        // Create Seminar Date
                        storedSeminarStart.setTimeInMillis(seminar_start);
                        Log.w("REMINDER_SERVICE", seminarList.get(j).getTitle() + " Start: " + storedSeminarStart.toString());
                        storedSeminarEnd.setTimeInMillis(seminar_end);
                        Log.w("REMINDER_SERVICE", seminarList.get(j).getTitle() + " Ende: " + storedSeminarEnd.toString());
                        // Check if seminar is now
                        if (now.after(storedSeminarStart) && now.before(storedSeminarEnd)) {
                            Log.w("SEMINAR_EVENT", "Es findet gerade das Seminar " + seminarList.get(j).getTitle()  + " statt!");
                            if (!checkInList.isEmpty()) {
                                for (int k = 0; k < checkInList.size(); k++) {
                                    String checkedIn = checkInList.get(k);
                                    seminar_room = seminarList.get(j).getRoom_id();
                                    // Find matching Room
                                    myRooms = Room.getRoomTitles();
                                    room_id = (long) myRooms.indexOf(checkedIn);
                                    if (seminar_room == room_id) {
                                        Log.w("REMINDER_SERVICE", "Seminar CheckIn success!");
                                    } else {
                                        Log.w("REMINDER_SERVICE", "Du verpasst gerade ein Seminar!");
                                        if (currentCount < 1) {
                                            notifyID = seminarList.get(j).getId();
                                            sendReminder(context, seminarList.get(j).getTitle(), myRooms.get((int)seminar_room), room_id);
                                            currentCount++;
                                            SharedPreferences.Editor editor = values.edit();
                                            editor.putInt(KEY_COUNT, currentCount);
                                            editor.commit();
                                        }

                                    }
                                }
                            } else {
                                Log.w("REMINDER_SERVICE", "Du verpasst gerade ein Seminar!");
                                if (currentCount < 1) {
                                    // Set Notification ID
                                    int notifyID = 1;
                                    sendReminder(context, seminarList.get(j).getTitle(), myRooms.get((int)seminar_room), room_id);
                                    currentCount++;
                                    SharedPreferences.Editor editor = values.edit();
                                    editor.putInt(KEY_COUNT, currentCount);
                                    editor.commit();
                                }
                            }
                        }
                    }
                }
            }
        }
        // Close Database
        semesterDataSource.close();
        seminarDataSource.close();
    }

    // Time conversion methods
    public String getReadableTime(long millis){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date resultdate = new Date(millis);
        String readable = sdf.format(resultdate);
        return readable;
    }

    public long dayToMillis(String datetime) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        try {
            date = sdf.parse(datetime);
        } catch (ParseException e) {
            Log.w("DATUMSFEHLER!", e);
        }
        return date.getTime();
    }

    public long createTimestamp(String datetime) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String bastelstunde = null;

        bastelstunde = getDateString(now) + " " + datetime;

        try {
            date = sdf.parse(bastelstunde);
        } catch (ParseException e) {
            Log.w("DATUMSFEHLER!", e);
        }
        return date.getTime();
    }

    public String getDateString(Calendar calendar) {
        String myDateString = null;

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        if (calendar != null) {
            myDateString = sdf.format(calendar.getTime());
        }
        return myDateString;
    }

    private void sendReminder(Context context, String seminar_title, String room, long room_id) {
        // Get default notification sound
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        builder =
                new NotificationCompat.Builder(context)
                        .setContentTitle("Du verpasst gerade ein Seminar!")
                        .setContentText(seminar_title + " im Raum " + room + ".")
                        .setSmallIcon(R.drawable.ic_stat_notify_mssc)
                        .setSound(alarmSound)
                        .setAutoCancel(true)
                        .setColor(Color.MAGENTA);

        Intent notifyIntent = new Intent(context, HawMapActivity.class);
        notifyIntent.putExtra(ROOM_ID, room_id);
        notifyIntent.putExtra(COLOR_ID, 1);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int)notifyID, builder.build());
    }
}