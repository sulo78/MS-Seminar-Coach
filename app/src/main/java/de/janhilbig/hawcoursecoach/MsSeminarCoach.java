package de.janhilbig.hawcoursecoach;

import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


import de.janhilbig.hawcoursecoach.Activities.HawMapActivity;
import de.janhilbig.hawcoursecoach.Activities.ShowSemesterActivity;
import de.janhilbig.hawcoursecoach.database.Room;


public class MsSeminarCoach extends Application implements BootstrapNotifier {
    private static final String TAG = "MS Seminar Coach";
    private RegionBootstrap regionBootstrap;
    private BackgroundPowerSaver backgroundPowerSaver;
    private ShowSemesterActivity monitoringActivity = null;
    private BeaconManager beaconManager;
    // String for Beacon ID
    String beaconId;
    // CheckIn System
    public ArrayList<String> checkInList;
    public List<Region> regionList;
    // Rooms
    List<String> myRooms;
    long room_id;
    // Put extra IDs
    public static final String ROOM_ID = "room_id";
    public static final String COLOR_ID = "color_id";
    public static final String CHECK_IN_LIST = "check_in_list";
    // Alarm Manager
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;



    public void onCreate() {
        super.onCreate();
        beaconManager = org.altbeacon.beacon.BeaconManager.getInstanceForApplication(this);
        // By default the AndroidBeaconLibrary will only find AltBeacons.  If you wish to make it
        // find a different type of beacon, you must specify the byte layout for that beacon's
        // advertisement with a line like below.  The example shows how to find a beacon with the
        // same byte layout as AltBeacon but with a beaconTypeCode of 0xaabb.  To find the proper
        // layout expression for other beacon types, do a web search for "setBeaconLayout"
        // including the quotes.
        //
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        //

        // Init CheckInSystem
        List<Region> myBeacons = myRegions();
        regionList = new ArrayList<>();
        checkInList = new ArrayList<>();
        myRooms = Room.getRoomTitles();

        // set up background monitoring
        regionBootstrap = new RegionBootstrap(this, myBeacons);
        // simply constructing this class and holding a reference to it in your custom Application
        // class will automatically cause the BeaconLibrary to save battery whenever the application
        // is not visible.  This reduces bluetooth power usage by about 60%
        backgroundPowerSaver = new BackgroundPowerSaver(this);
        // Start Alarm Manager
        setUpAlarms();
    }

    @Override
    public void didEnterRegion(Region region) {
        // Get the entered region
        Region enterRegion = region;

        if (myRegions().contains(enterRegion)) {
            // if we found a beacon from our list we start ranging
            try {
                // start ranging for beacons.  This will provide an update once per second with the estimated
                // distance to the beacon in the didRAngeBeaconsInRegion method.
                beaconManager.startRangingBeaconsInRegion(enterRegion);
                beaconManager.setRangeNotifier(new RangeNotifier() {
                    @Override
                    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                        for (Beacon beacon : beacons) {
                            if (beacon.getDistance() < 6.5) {
                                Log.d(TAG, "I see a beacon that is less than 5 meters away.");
                                // Perform distance-specific action here
                                if (!regionList.contains(region)) {
                                    regionList.add(region);
                                    beaconId = region.getUniqueId();
                                    Log.w("CheckInSystem", "CheckIn: " + beaconId);
                                    // Find matching Room
                                    myRooms = Room.getRoomTitles();
                                    room_id = (long) myRooms.indexOf(beaconId);
                                    // Send Notification
                                    checkInNotification(beaconId);
                                    Log.w("MsSeminarCoach", " Room ID: " + room_id);
                                    checkInList.add(beaconId);
                                    updateAlarmIntent(checkInList);
                                }
                            }
                        }
                    }
                });
            } catch (RemoteException e) {   }
        }
    }

    @Override
    public void didExitRegion(Region region) {
        Region exitRegion = region;
        if (myRegions().contains(exitRegion)) {
            if (regionList.contains(exitRegion)) {
                regionList.remove(exitRegion);
                beaconId = exitRegion.getUniqueId();
                // Find matching Room
                myRooms = Room.getRoomTitles();
                room_id = (long) myRooms.indexOf(beaconId);
                // Send Notification
                checkOutNotification(beaconId);
                Log.w("CheckInSystem", "CheckOut: " + exitRegion.getUniqueId());
                checkInList.remove(beaconId);
                updateAlarmIntent(checkInList);
            }
        }
    }

    @Override
    public void didDetermineStateForRegion(int state, Region region) {

    }

    private void checkInNotification(String beaconId) {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("MS Seminar Coach")
                        .setContentText("In Raum " + beaconId + " eingecheckt.")
                        .setSmallIcon(R.drawable.ic_stat_notify_mssc)
                        .setSound(alarmSound)
                        .setAutoCancel(true)
                        .setColor(Color.GREEN);

        Intent notifyIntent = new Intent(this, HawMapActivity.class);
        notifyIntent.putExtra(ROOM_ID, room_id);
        notifyIntent.putExtra(COLOR_ID, 2);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    private void checkOutNotification(String beaconId) {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("MS Seminar Coach")
                        .setContentText("Aus Raum " + beaconId + " ausgecheckt.")
                        .setSmallIcon(R.drawable.ic_stat_notify_mssc)
                        .setSound(alarmSound)
                        .setAutoCancel(true)
                        .setColor(Color.BLUE);

        Intent notifyIntent = new Intent(this, HawMapActivity.class);
        notifyIntent.putExtra(ROOM_ID, room_id);
        notifyIntent.putExtra(COLOR_ID, 3);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    public void setMonitoringActivity(ShowSemesterActivity activity) {
        this.monitoringActivity = activity;
    }

    public static List<Region> myRegions() {
        List<Region> myRegions = new ArrayList<>();
        String appUUID = "28415da5346b-459d-9795-0a30092ccd30";
        // Add iBeacon IDs to list
        myRegions.add(new Region("E39", Identifier.parse(appUUID), Identifier.fromInt(1), Identifier.fromInt(39)));
        myRegions.add(new Region("E42", Identifier.parse(appUUID), Identifier.fromInt(1), Identifier.fromInt(42)));
        myRegions.add(new Region("E46", Identifier.parse(appUUID), Identifier.fromInt(1), Identifier.fromInt(46)));
        myRegions.add(new Region("E48", Identifier.parse(appUUID), Identifier.fromInt(1), Identifier.fromInt(48)));
        myRegions.add(new Region("E59", Identifier.parse(appUUID), Identifier.fromInt(1), Identifier.fromInt(59)));
        myRegions.add(new Region("E62", Identifier.parse(appUUID), Identifier.fromInt(1), Identifier.fromInt(62)));
        myRegions.add(new Region("E63", Identifier.parse(appUUID), Identifier.fromInt(1), Identifier.fromInt(63)));
        myRegions.add(new Region("E64", Identifier.parse(appUUID), Identifier.fromInt(1), Identifier.fromInt(64)));

        return myRegions;
    }

    private void setUpAlarms() {
        alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        updateAlarmIntent(checkInList);
        alarmMgr.setRepeating(AlarmManager.RTC, System.currentTimeMillis(),
                60 * 1000, alarmIntent);
    }

    private void updateAlarmIntent(ArrayList<String> checkInList) {
        Intent intent = new Intent(this, ReminderService.class);
        intent.putStringArrayListExtra(CHECK_IN_LIST, checkInList);
        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}