package com.example.user.rideshareapp1;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class search_form extends ActionBarActivity {

    private TextView dateView;
    private TextView timeView;

    private Boolean timeStartFlag;

    private String timeStart,timeEnd;

    int year,month,day,hour,minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_form);

        Spinner origin = (Spinner) findViewById(R.id.search_origin);
        Spinner dest = (Spinner) findViewById(R.id.search_dest);

        fillSpinners(origin,dest);

        dateView = (TextView) findViewById(R.id.search_dateView);
        timeView = (TextView) findViewById(R.id.search_timeView);

        Button setDate = (Button) findViewById(R.id.search_setDate);
        Button setTimeStart = (Button) findViewById(R.id.search_timeStart);
        Button setTimeEnd = (Button) findViewById(R.id.search_timeEnd);
        Button cancel = (Button) findViewById(R.id.search_btnCancel);


        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month + 1, day);

        timeStart = "00:00";
        timeEnd = "23:59";

        timeView.setText(timeStart + " - " + timeEnd);

        setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDate();
            }
        });

        setTimeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeStartFlag = true;
                setTime();

            }
        });

        setTimeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeStartFlag = false;
                setTime();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    protected void fillSpinners(Spinner origin, Spinner dest){
        List<String> places = new ArrayList<String>();
        places.add("Wilf");
        places.add("Stern");
        places.add("LGA");
        places.add("JFK");
        places.add("Teanack");
        places.add("Long Island");
        places.add("LGA");
        ArrayAdapter<String> placeAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, places);
        placeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        origin.setAdapter(placeAdapter);
        dest.setAdapter(placeAdapter);
    }

    public void setDate() {
        showDialog(999);
    }

    public void setTime() {
        showDialog(888);

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        else if (id == 888){
            return new TimePickerDialog(this, myTimeListener, hour, minute, false);
        }

        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day
            showDate(arg1, arg2+1, arg3);
        }
    };

    private TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener(){
        @Override
        public void onTimeSet(TimePicker timePicker, int i, int i1) {

            if (timeStartFlag)
                timeStart = (i1<10) ? i+ ":0" + i1 : i + ":" + i1;
            else
                timeEnd = (i1<10) ? i+ ":0" + i1 : i + ":" + i1;

            showTime();
        }
    };

    private void showDate(int year, int month, int day) {
        dateView.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }

    private void showTime(){
        timeView.setText(new StringBuilder().append(timeStart).append("-")
                .append(timeEnd));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_form, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
