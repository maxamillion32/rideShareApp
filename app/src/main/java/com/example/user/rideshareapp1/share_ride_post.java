package com.example.user.rideshareapp1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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


public class share_ride_post extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_ride_post);

        final EditText origin = (EditText) findViewById(R.id.origin_view);
        final EditText dest = (EditText) findViewById(R.id.dest_view);
        final EditText startTime = (EditText) findViewById(R.id.startTime_view);
        final EditText endTime = (EditText) findViewById(R.id.endTime_view);
        final EditText capacity = (EditText) findViewById(R.id.capacity_field);
        final EditText comments = (EditText) findViewById(R.id.comments_field);

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
                nameValuePair.add(new BasicNameValuePair("origin", origin.getText().toString()));
                nameValuePair.add(new BasicNameValuePair("dest", dest.getText().toString()));
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_share_ride_post, menu);
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
