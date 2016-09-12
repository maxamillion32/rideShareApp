package com.example.user.rideshareapp1;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.securepreferences.SecurePreferences;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class my_rides extends Activity {


    ArrayList<Ride> rides;
    ListView list;
    ProgressBar bar;
    rideAdapter adapter;

    Activity that = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_rides);

        rides= new ArrayList<>();

        list = (ListView) findViewById(R.id.myRides);
        bar = (ProgressBar) findViewById(R.id.progressBar);

        adapter = new rideAdapter(my_rides.this,rides);

        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {

                Intent intent = new Intent(my_rides.this, ride_details.class);

                Ride picked = rides.get(position);

                //SharedPreferences sharedPreferences = new SecurePreferences(getBaseContext());

                //if(!sharedPreferences.contains("remember") || !sharedPreferences.getBoolean("remember",false) )
                intent.putExtra("token",getIntent().getExtras().getString("token"));

                intent.putExtra("name", getIntent().getExtras().getString("name"));

                intent.putExtra("infoMyRide", picked.toString());

                startActivityForResult(intent, 2);

            }
        });

        getMyRides getStuff = new getMyRides(rides,list,adapter,bar);

        getStuff.execute((Void) null);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        bar.setVisibility(View.VISIBLE);

        rides= new ArrayList<>();

        adapter = new rideAdapter(my_rides.this,rides);

        list.setAdapter(adapter);

        getMyRides getStuff = new getMyRides(rides,list,adapter,bar);

        getStuff.execute((Void) null);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK && data.getExtras().getBoolean("delete")) {

            bar.setVisibility(View.VISIBLE);

            rides = new ArrayList<>();

            adapter = new rideAdapter(my_rides.this,rides);

            list.setAdapter(adapter);

            getMyRides getStuff = new getMyRides(rides, list, adapter, bar);

            getStuff.execute((Void) null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_rides, menu);
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

    class getMyRides extends AsyncTask<Void, Void, Boolean> {

        ArrayList<Ride> myRides;
        ListView list;
        rideAdapter adapter;
        ProgressBar bar;

        public getMyRides( ArrayList<Ride> myRides, ListView list,rideAdapter adapter,ProgressBar bar){

            this.myRides= myRides;

            this.list=list;

            this.adapter = adapter;

            this.bar = bar;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();

            String urlToken;

            //SharedPreferences sharedPreferences = new SecurePreferences(getBaseContext());

//            if(sharedPreferences.contains("remember") && sharedPreferences.getBoolean("remember",false) )
//                urlToken = sharedPreferences.getString("token","aaaaaaaaaaaaaa");
//            else
            urlToken = getIntent().getExtras().getString("token");

            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet("https://rideshare-server-yosef456.c9users.io/showAll?token=" + urlToken);
            // replace with your url

            HttpResponse response;
            try {
                response = client.execute(request);

                Log.d("Response of GET request", response.toString());

                parseResponseForRides(response);

                return true;

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                return false;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                return false;
            }
        }

        public void parseResponseForRides( HttpResponse response){

            String line="";
            String data="";
            try(BufferedReader br=new BufferedReader(new InputStreamReader(response.getEntity().getContent()))){

                while((line=br.readLine())!=null){

                    data+=line;
                }
                JSONArray  arr = new JSONArray(data);

                for (int i=0;i<arr.length();i++){
                    JSONObject singleRide = arr.getJSONObject(i);

                    myRides.add(new Ride(singleRide.getInt("id"),singleRide.getInt("driver_id"),
                            singleRide.getString("name"),singleRide.getString("email"),
                            singleRide.getString("type"),singleRide.getString("origin"),singleRide.getString("dest"),
                            singleRide.getString("date") , singleRide.getString("timestart"),singleRide.getString("timeend"),
                            singleRide.getInt("capacity"), singleRide.getInt("spotstaken"),
                            singleRide.getString("status"),singleRide.getString("comments")));

                    Log.i("RESPONSE" ,"ride: " + singleRide );
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }

            if (data.length()==0)
                return;



        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success){
                adapter.notifyDataSetChanged();
                bar.setVisibility(View.GONE);
            }
            else{
                bar.setVisibility(View.GONE);

                Toast.makeText(that,"An error has occurred",Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onCancelled() {

        }

    }
}

