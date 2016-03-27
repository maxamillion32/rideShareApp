package com.example.user.rideshareapp1;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class share_ride_post extends Activity {

    private int login;
    private TextView dateView;
    private TextView timeView;
    private Spinner origin,dest,rideType;
    private EditText comments,capacity;
    private int year,month,day,hour,minute;
    private String timeStart,timeEnd;
    Boolean timeStartFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_ride_post);

        login = getIntent().getExtras().getInt("login");

        rideType = (Spinner) findViewById(R.id.rideType);
        origin = (Spinner) findViewById(R.id.search_origin);
        dest = (Spinner) findViewById(R.id.search_dest);
        dateView = (TextView) findViewById(R.id.dateView);
        timeView = (TextView) findViewById(R.id.timeView);
        capacity = (EditText) findViewById(R.id.capacity);
        comments = (EditText) findViewById(R.id.comments);

        TextView errors = (TextView) findViewById(R.id.error);

        errors.setText("");

        Button setDate = (Button) findViewById(R.id.setDate);
        Button setTimeStart = (Button) findViewById(R.id.search_timeStart);
        Button setTimeEnd = (Button) findViewById(R.id.search_timeEnd);
        Button cancel = (Button) findViewById(R.id.btnCancel);

        Calendar  calendar = Calendar.getInstance();
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

        fillSpinners(origin, dest, rideType);

        Button shareRide = (Button) findViewById(R.id.search_btnSubmit);

        shareRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!checkIfNumber(capacity.getText().toString())){
                    capacity.setError("Capacity has to be number");
                    return;
                }


                PostRide post = new PostRide(login,rideType.getSelectedItem().toString(),
                        origin.getSelectedItem().toString(),
                        dest.getSelectedItem().toString(),dateView.getText().toString(),
                        timeStart,timeEnd,Integer.parseInt(capacity.getText().toString()),
                        comments.getText().toString());

                post.execute();
            }
        });
    }

    protected boolean checkIfNumber(String capacity){

        for (int i=0;i<capacity.length();i++)
            if (capacity.charAt(i)<48 || capacity.charAt(i) > 57)
                return false;

        return true;
    }

    protected void fillSpinners(Spinner origin, Spinner dest, Spinner rideType){
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

        List<String> types = new ArrayList<String>();
        types.add("Driver");
        types.add("Taxi");
        types.add("Uber");
        types.add("Lyft");
        types.add("Ride");
        ArrayAdapter<String> typesAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, types);
        typesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rideType.setAdapter(typesAdapter);
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

    @SuppressWarnings("deprecation")
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

    class PostRide extends AsyncTask<Void, Void, Boolean> {

        private int userID;
        private String type;
        private String origin;
        private String dest;
        private String date;
        private String timeStart;
        private String timeEnd;
        private int capacity;
        private String comments;

        public PostRide(int userID,String type,String origin, String dest, String date, String timeStart, String timeEnd, int capacity,String comments){
            this.userID = userID;
            this.type = type;
            this.origin = origin;
            this.dest = dest;
            this.date = date;
            this.timeStart = timeStart;
            this.timeEnd = timeEnd;
            this.capacity = capacity;
            this.comments = comments;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            return postRideToServer();
        }

        protected Boolean postRideToServer(){
            HttpClient httpClient = new DefaultHttpClient();
            // replace with your url
            HttpPost httpPost = new HttpPost("https://rideshare-server-yosef456.c9users.io/newride");


            //Post Data
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(7);

            nameValuePair.add(new BasicNameValuePair("userID", userID + ""));
            nameValuePair.add(new BasicNameValuePair("type", type));
            nameValuePair.add(new BasicNameValuePair("origin", origin));
            nameValuePair.add(new BasicNameValuePair("dest", dest));
            nameValuePair.add(new BasicNameValuePair("date",date));
            nameValuePair.add(new BasicNameValuePair("timeStart", timeStart));
            nameValuePair.add(new BasicNameValuePair("timeEnd", timeEnd));
            nameValuePair.add(new BasicNameValuePair("capacity", capacity + ""));
            nameValuePair.add(new BasicNameValuePair("comments", comments));


            //Encoding POST data
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            } catch (UnsupportedEncodingException e) {
                // log exception
                return false;
            }

            try {
                HttpResponse response = httpClient.execute(httpPost);

                String line="";
                String data="";
                try{
                    BufferedReader br=new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    while((line=br.readLine())!=null){

                        data+=line;
                    }
                    Log.i("RESPONSE", data.length() + "");
                }
                catch(Exception e){
                    return false;
                }

                return true;

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                return false;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success){
                Intent intent = new Intent(share_ride_post.this, my_rides.class);

                intent.putExtra("login", login);

                startActivity(intent);
            }
            else {
                TextView error = (TextView) findViewById(R.id.error);

                error.setText("An error has occurred");
            }

        }

        @Override
        protected void onCancelled() {

        }

    }
}


