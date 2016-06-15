package com.example.user.rideshareapp1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by User on 6/14/2016.
 */
public class ChatAdapter extends ArrayAdapter<ChatItem> {
    private final Context context;
    private final ArrayList<ChatItem> values;

    public ChatAdapter(Context context,  ArrayList<ChatItem> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.chat_item_row, parent, false);

        TextView name = (TextView) rowView.findViewById(R.id.chatItem_name);
        TextView ride = (TextView) rowView.findViewById(R.id.charItem_content);

        name.setText(values.get(position).getName() + ":");
        ride.setText(values.get(position).getContent());

        return rowView;
    }
}
