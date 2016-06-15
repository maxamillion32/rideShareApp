package com.example.user.rideshareapp1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by User on 6/9/2016.
 */
public class approveAdapter extends ArrayAdapter<Approve> {

    private final Context context;
    private final ArrayList<Approve> values;

    public approveAdapter(Context context,  ArrayList<Approve> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.approval_row_adapter, parent, false);

        TextView name = (TextView) rowView.findViewById(R.id.approval_name);
        TextView ride = (TextView) rowView.findViewById(R.id.approval_ride);

        name.setText(values.get(position).getName() + "         ");
        ride.setText(values.get(position).getDest() + "   ");

        return rowView;
    }
}
