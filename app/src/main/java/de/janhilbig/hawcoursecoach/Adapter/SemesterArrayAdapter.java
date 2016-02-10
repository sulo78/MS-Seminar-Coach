package de.janhilbig.hawcoursecoach.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import de.janhilbig.hawcoursecoach.R;
import de.janhilbig.hawcoursecoach.database.Semester;


public class SemesterArrayAdapter extends ArrayAdapter<Semester> {
    private final Context context;
    private final ArrayList<Semester> values;


    // Constructor
    public SemesterArrayAdapter(Context context, ArrayList<Semester> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    // getView Method
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.semester_list_item, parent, false);
        TextView title = (TextView) rowView.findViewById(R.id.semesterTitle);
        TextView time = (TextView) rowView.findViewById(R.id.semesterTime);
        title.setText(values.get(position).getTitle());
        time.setText("Vom " + values.get(position).getStartdate() + " bis " + values.get(position).getEnddate());

        return rowView;
    }
}
