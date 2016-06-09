package com.example.user.rideshareapp1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by User on 3/4/2016.
 */
public class rideAdapter extends ArrayAdapter<Ride> {
    private final Context context;
    private final ArrayList<Ride> values;

    public rideAdapter(Context context,  ArrayList<Ride> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.ride_row_adapter, parent, false);

        TextView origin = (TextView) rowView.findViewById(R.id.row_origin);
        TextView dest = (TextView) rowView.findViewById(R.id.row_dest);
        TextView date = (TextView) rowView.findViewById(R.id.row_date);
        TextView timeStart = (TextView) rowView.findViewById(R.id.row_timeStart);
        TextView timeEnd = (TextView) rowView.findViewById(R.id.row_timeEnd);

        origin.setText(values.get(position).getOrigin() + "         ");
        dest.setText(values.get(position).getDest() + "   ");
        date.setText(values.get(position).getDate() + "  ");
        timeStart.setText(values.get(position).getTimeStart() + "    -     ");
        timeEnd.setText(values.get(position).getTimeEnd() + "   ");

        return rowView;
    }
}