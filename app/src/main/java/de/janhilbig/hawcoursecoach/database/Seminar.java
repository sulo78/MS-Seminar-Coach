package de.janhilbig.hawcoursecoach.database;

import android.os.Parcel;
import android.os.Parcelable;

// Data model for seminar
public class Seminar implements Parcelable {
    private long id;
    private String title;
    private long weekday;
    private String starttime;
    private String endtime;
    private long room_id;
    private long semester_id;
    
    // Constructor
    public Seminar(String title, long weekday, String starttime, String endtime, long room_id, long semester_id) {
        this.title = title;
        this.weekday = weekday;
        this.starttime = starttime;
        this.endtime = endtime;
        this.room_id = room_id;
        this.semester_id = semester_id;
    }
    
    // Constructor (Parcel)
    private Seminar(Parcel in) {
        title = in.readString();
        weekday = in.readLong();
        starttime = in.readString();
        endtime = in.readString();
        room_id = in.readLong();
        semester_id = in.readLong();
    }

    public long getRoom_id() {
        return room_id;
    }

    public void setRoom_id(long room_id) {
        this.room_id = room_id;
    }

    public long getWeekday() {
        return weekday;
    }

    public void setWeekday(long weekday) {
        this.weekday = weekday;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getSemester_id() {
        return semester_id;
    }

    public void setSemester_id(long semester_id) {
        this.semester_id = semester_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeLong(weekday);
        dest.writeString(starttime);
        dest.writeString(endtime);
        dest.writeLong(room_id);
        dest.writeLong(semester_id);
    }

    public static final Parcelable.Creator<Seminar> CREATOR = new Parcelable.Creator<Seminar>() {
        public Seminar createFromParcel(Parcel in) {
            return new Seminar(in);
        }
        public Seminar[] newArray(int size) {
            return new Seminar[size];
        }
    };
}
