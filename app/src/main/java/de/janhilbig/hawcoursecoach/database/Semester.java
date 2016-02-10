package de.janhilbig.hawcoursecoach.database;

import android.os.Parcel;
import android.os.Parcelable;

// Data model for semester
public class Semester implements Parcelable {
    // database fields
    private long id;
    private String title;
    private String startdate;
    private String enddate;

    // Constructor
    public Semester(String title, String startdate, String enddate) {
        this.title = title;
        this.startdate = startdate;
        this.enddate = enddate;
    }

    // Constructor (Parcel)
    private Semester(Parcel in) {
        title = in.readString();
        startdate = in.readString();
        enddate = in.readString();
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

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    // will be used for the ArrayAdapter in the ListView
    public String toString() {
        return title;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(startdate);
        dest.writeString(enddate);
    }

    public static final Parcelable.Creator<Semester> CREATOR = new Parcelable.Creator<Semester>() {
        public Semester createFromParcel(Parcel in) {
            return new Semester(in);
        }
        public Semester[] newArray(int size) {
            return new Semester[size];
        }
    };
}