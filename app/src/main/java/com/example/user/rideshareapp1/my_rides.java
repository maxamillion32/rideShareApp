package com.example.user.rideshareapp1;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class my_rides extends Activity {


    ArrayList<Ride> rides= new ArrayList<Ride>();
    ListView list;
    ProgressBar bar;
    String login;
    rideAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_rides);

        list = (ListView) findViewById(R.id.myRides);
        bar = (ProgressBar) findViewById(R.id.progressBar);

        login = getIntent().getStringExtra("login");

        //rides.add(new Ride(1, "Tseitkin", "YU", "LGA", "3/6/16 12:00", "3/6/16 01:00", 5, "you pay!"));

        //rides.add(new Ride(1, "Tseitkin", "LGA", "YU", "3/6/16 03:00", "3/6/16 04:00", 6, "no comments"));

        adapter = new rideAdapter(my_rides.this,rides);

        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {

                Intent intent = new Intent(my_rides.this, ride_details.class);

                Ride picked = rides.get(position);

                intent.putExtra("info", picked.toString());

                startActivity(intent);

            }
        });

        getMyRides getStuff = new getMyRides(login,rides,list,adapter,bar);

        if (!isOnline()){
            TextView error = (TextView) findViewById(R.id.error);

            bar.setVisibility(View.GONE);

            error.setText("No internet connection/ server not running");
        }
        else
            getStuff.execute((Void) null);
    }

    public boolean isOnline() {

        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 https://rideshare-server-yosef456.c9users.io/");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
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
}

class getMyRides extends AsyncTask<Void, Void, Boolean> {
    String login;
    ArrayList<Ride> myRides;
    ListView list;
    rideAdapter adapter;
    ProgressBar bar;

    public getMyRides(String login, ArrayList<Ride> myRides, ListView list,rideAdapter adapter,ProgressBar bar){
        this.login=login;

        this.myRides= myRides;

        this.list=list;

        this.adapter = adapter;

        this.bar = bar;
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        //android.os.Debug.waitForDebugger();

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet("https://rideshare-server-yosef456.c9users.io/showAll?id=" + login);
        // replace with your url

        HttpResponse response;
        try {
            response = client.execute(request);

            Log.d("Response of GET request", response.toString());

            parseResponseForRides(response);

            return true;

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return false;
    }

    public void parseResponseForRides( HttpResponse response){

        String line="";
        String data="";
        try{
            BufferedReader br=new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            while((line=br.readLine())!=null){

                data+=line;
            }
            Log.i("RESPONSE",data.length() + "" );
        }
        catch(Exception e){
            e.printStackTrace();
        }

        String [] rideString = data.split("\n");

        for (int i=0;i<rideString.length;i++){
            String singleRide = rideString[i];

            String [] details = singleRide.split(" ");

            /**myRides.add(new Ride(Integer.parseInt(details[0]),details[1],
                    details[2],details[3],details[4] + details[5] +details[6] + details[7] + details[8] + details[9] +details[10],
                    details[11] + details[12] +details[13] + details[14] +details[15] + details[16] + details[17] ,
                    Integer.parseInt(details[6]),details[7]));**/

            Log.i("RESPONSE" ,"ride: " + singleRide );
        }

    }

    @Override
    protected void onPostExecute(final Boolean success) {

        if (success){
            adapter.notifyDataSetChanged();
            bar.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onCancelled() {

    }

}
