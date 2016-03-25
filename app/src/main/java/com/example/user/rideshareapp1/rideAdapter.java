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

        TextView origin = (TextView) rowView.findViewById(R.id.search_origin);
        TextView dest = (TextView) rowView.findViewById(R.id.search_dest);
        TextView timeStart = (TextView) rowView.findViewById(R.id.search_timeStart);
        TextView timeEnd = (TextView) rowView.findViewById(R.id.search_timeEnd);
        TextView driver = (TextView) rowView.findViewById(R.id.driver);

        origin.setText(values.get(position).getOrigin() + "   ");
        dest.setText(values.get(position).getDest() + "   ");
        timeStart.setText(values.get(position).getTimeStart() + "   -");
        timeEnd.setText(values.get(position).getTimeEnd() + "   ");
        driver.setText(values.get(position).getDriver() + "   ");

        return rowView;
    }
}