package de.janhilbig.hawcoursecoach.database;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suloUser on 02.08.2015.
 */
public class Weekday {
    private int id;
    private String title;

    public Weekday(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static List<String> getWeekdays() {
        List<String> days = new ArrayList<>();
        days.add("Sonntag");
        days.add("Montag");
        days.add("Dienstag");
        days.add("Mittwoch");
        days.add("Donnerstag");
        days.add("Freitag");
        days.add("Samstag");
        return days;
    }
}