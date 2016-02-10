package de.janhilbig.hawcoursecoach.database;

import java.util.ArrayList;
import java.util.List;

// Data model for room
public class Room {

    // Database fields
    private long id;
    private String title;
    private long map_x;
    private long map_y;

    // Constructor
    public Room(long id, String title, long map_x, long map_y) {
        this.title = title;
        this.map_x = map_x;
        this.map_y = map_y;
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

    public long getMap_x() {
        return map_x;
    }

    public void setMap_x(long map_x) {
        this.map_x = map_x;
    }

    public long getMap_y() {
        return map_y;
    }

    public void setMap_y(long map_y) {
        this.map_y = map_y;
    }

    public String roomIdToTitle(long id) {
        this.id = id;
        return title;
    }

    public static List<Room> getRooms() {
        List<Room> rooms = new ArrayList<>();

        rooms.add(new Room(0, "E39", 505, 567));
        rooms.add(new Room(1, "E42", 633, 470));
        rooms.add(new Room(2, "E46", 522, 470));
        rooms.add(new Room(3, "E48", 645, 567));
        rooms.add(new Room(4, "E59", 898, 440));
        rooms.add(new Room(5, "E62", 898, 326));
        rooms.add(new Room(6, "E63", 823, 325));
        rooms.add(new Room(7, "E64", 898, 235));

        return rooms;
    }

    public static List<String> getRoomTitles() {
        List<String> titles = new ArrayList<>();
        for (int i = 0; i < getRooms().size(); i++ ) {
            String title = getRooms().get(i).getTitle();
            titles.add(title);
        }
        return titles;
    }
}