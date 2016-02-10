package de.janhilbig.hawcoursecoach.Adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import de.janhilbig.hawcoursecoach.Activities.HawMapActivity;
import de.janhilbig.hawcoursecoach.R;
import de.janhilbig.hawcoursecoach.database.Room;
import de.janhilbig.hawcoursecoach.database.Seminar;


public class SeminarArrayAdapter extends ArrayAdapter<Seminar> {
    public static final String ROOM_ID = "room_id";
    public static final String COLOR_ID = "color_id";
    private final Context context;
    private final ArrayList<Seminar> values;
    private Room roomtitle;


    // Constructor
    public SeminarArrayAdapter(Context context, ArrayList<Seminar> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    // getView Method
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.seminar_list_item, parent, false);
        // get the views
        TextView title = (TextView) rowView.findViewById(R.id.seminarTitle);
        TextView day = (TextView) rowView.findViewById(R.id.seminarDay);
        TextView begin = (TextView) rowView.findViewById(R.id.seminarBegin);
        TextView end = (TextView) rowView.findViewById(R.id.seminarEnd);
        TextView room = (TextView) rowView.findViewById(R.id.seminarRoom);
        // set the views
        title.setText(values.get(position).getTitle());
        day.setText(longToWeekday(values.get(position).getWeekday()));
        begin.setText(values.get(position).getStarttime());
        end.setText(values.get(position).getEndtime());
        room.setText(idToRoomTitle(values.get(position).getRoom_id()));
        // listener for row mapButton
        final Button button = (Button) rowView.findViewById(R.id.seminar_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), HawMapActivity.class);
                intent.putExtra(ROOM_ID, values.get(position).getRoom_id());
                intent.putExtra(COLOR_ID, 3);
                context.startActivity(intent);
            }
        });

        return rowView;
    }

    private String longToWeekday(long day_of_week){
        int day = (int) day_of_week;
        String weekday;
        switch (day){
            case 1 : weekday = "Sonntag";
                break;
            case 2 : weekday = "Montag";
                break;
            case 3 : weekday = "Dienstag";
                break;
            case 4 : weekday = "Mittwoch";
                break;
            case 5 : weekday = "Donnerstag";
                break;
            case 6 : weekday = "Freitag";
                break;
            case 7 : weekday = "Samstag";
                break;
            default: weekday = "some day";
                break;
        }
        return weekday;
    }

    private String idToRoomTitle(long room_id) {
        int id = (int) room_id;
        String title;
        switch (id) {
            case 0 : title = "E39";
                break;
            case 1 : title = "E42";
                break;
            case 2 : title = "E46";
                break;
            case 3 : title = "E48";
                break;
            case 4 : title = "E59";
                break;
            case 5 : title = "E62";
                break;
            case 6 : title = "E63";
                break;
            case 7 : title = "E64";
                break;
            default: title = "some room";
                break;
        }
        return title;
    }
}
