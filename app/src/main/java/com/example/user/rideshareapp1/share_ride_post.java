package com.example.user.rideshareapp1;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
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
import android.widget.Toast;

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
import java.util.List;


public class share_ride_post extends Activity {

    private DatePicker datePicker;
    private int year, month, day;
    EditText dateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_ride_post);

        final Spinner origin = (Spinner) findViewById(R.id.origin);
        final Spinner dest = (Spinner) findViewById(R.id.dest);
        dateView = (EditText) findViewById(R.id.ride_date);
        final EditText startTime = (EditText) findViewById(R.id.start_time);
        final EditText endTime = (EditText) findViewById(R.id.end_time);
        final EditText capacity = (EditText) findViewById(R.id.end_time);
        final EditText comments = (EditText) findViewById(R.id.comments_field);

        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDate();
            }
        });

        List<String> list = new ArrayList<String>();
        list.add("Wilf");
        list.add("Stern");
        list.add("LGA");
        list.add("JFK");
        list.add("Teanack");
        list.add("Long Island");
        list.add("LGA");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        origin.setAdapter(dataAdapter);
        dest.setAdapter(dataAdapter);

        Button shareRide = (Button) findViewById(R.id.startRide);

        final String login = getIntent().getStringExtra("login");

        shareRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HttpClient httpClient = new DefaultHttpClient();
                // replace with your url
                HttpPost httpPost = new HttpPost("https://rideshare-server-yosef456.c9users.io/newride");


                //Post Data
                List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(7);
                nameValuePair.add(new BasicNameValuePair("userID", login));
                nameValuePair.add(new BasicNameValuePair("origin", origin.getSelectedItem().toString()));
                nameValuePair.add(new BasicNameValuePair("dest", dest.getSelectedItem().toString()));
                nameValuePair.add(new BasicNameValuePair("timeStart", startTime.getText().toString()));
                nameValuePair.add(new BasicNameValuePair("timeEnd", endTime.getText().toString()));
                nameValuePair.add(new BasicNameValuePair("capacity", capacity.getText().toString()));
                nameValuePair.add(new BasicNameValuePair("comments", comments.getText().toString()));


                //Encoding POST data
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
                } catch (UnsupportedEncodingException e) {
                    // log exception
                    e.printStackTrace();
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
                        e.printStackTrace();
                    }

                    if (data.compareTo("success")==0){
                        Intent intent = new Intent(share_ride_post.this, my_rides.class);

                        intent.putExtra("login", login);

                        startActivity(intent);
                    }
                    else {
                        TextView error = (TextView) findViewById(R.id.error);

                        error.setText("An error has occurred");
                    }


                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
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
        Toast.makeText(getApplicationContext(), "ca", Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
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

    private void showDate(int year, int month, int day) {
        dateView.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }
}
