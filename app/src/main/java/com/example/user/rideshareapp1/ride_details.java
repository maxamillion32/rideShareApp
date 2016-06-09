package com.example.user.rideshareapp1;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class ride_details extends Activity {

    final static int MY_RIDE = 1;
    final static int EXPLORE = 2;
    final static int JOINED_RIDE = 3;
    final static int PENDING_RIDE = 4;

    View rideDetailView;
    View rideDetailProgress;

    int login;
    int mode;
    Ride ride;

    Button delete;
    Button cancel;
    Button join;
    Button chat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_details);

        String[] details;

        rideDetailView = findViewById(R.id.rideDetails);
        rideDetailProgress = findViewById(R.id.ride_action_progress);

        delete = (Button) findViewById(R.id.DELETE_RIDE);
        cancel = (Button) findViewById(R.id.CANCEL_RIDE);
        join = (Button) findViewById(R.id.JOIN_RIDE);
        chat = (Button) findViewById(R.id.details_chat);

        if (getIntent().hasExtra("infoSearch")) {
            details = getIntent().getStringExtra("infoSearch").split("_");
        }
        else{
            details = getIntent().getStringExtra("infoMyRide").split("_");
        }

        login = getIntent().getExtras().getInt("login");

        ride = new Ride(Integer.parseInt(details[0]), Integer.parseInt(details[1]),
                details[2], details[3],
                details[4], details[5], details[6],
                details[7], details[8], details[9], Integer.parseInt(details[10]), Integer.parseInt(details[11]), details[12]);

        if (ride.getDriver() == login){

            cancel.setVisibility(View.GONE);
            join.setVisibility(View.GONE);
            mode = MY_RIDE;
        }
        else if(isJoined(ride.getId())){

            delete.setVisibility(View.GONE);
            join.setVisibility(View.GONE);
            mode = JOINED_RIDE;
        }
        else if(isPending(ride.getId())){
            delete.setVisibility(View.GONE);
            join.setVisibility(View.GONE);
            cancel.setVisibility(View.GONE);
            chat.setVisibility(View.GONE);
            mode = PENDING_RIDE;

            ((TextView)findViewById(R.id.details_pending)).setText("Ride Pending. Please wait for driver to approve.");
        }
        else {
            delete.setVisibility(View.GONE);
            cancel.setVisibility(View.GONE);
            chat.setVisibility(View.GONE);
            mode = EXPLORE;
        }

        TextView name = (TextView) findViewById(R.id.details_name);
        TextView capacity = (TextView) findViewById(R.id.details_capacity);
        TextView type = (TextView) findViewById(R.id.details_type);

        name.setText("Name:" + ride.getName());
        capacity.setText("Spots left:" +ride.getCapacity());
        type.setText("Type:" + ride.getType());

        TextView origin = (TextView) findViewById(R.id.details_origin);
        TextView dest = (TextView) findViewById(R.id.details_dest);

        origin.setText("Origin:" + ride.getOrigin());
        dest.setText("Dest:" + ride.getDest());

        TextView date = (TextView) findViewById(R.id.details_date);
        TextView timeStart = (TextView) findViewById(R.id.details_timeStart);
        TextView timeEnd = (TextView) findViewById(R.id.details_timeEnd);

        date.setText(ride.getDate());
        timeStart.setText( "Time:" + ride.getTimeStart()+ "-");
        timeEnd.setText(ride.getTimeEnd());

        TextView comments = (TextView) findViewById(R.id.details_comments);

        comments.setText(ride.getComments());

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgress(true);
                deleteRide deleteRide = new deleteRide(ride.getId());

                deleteRide.execute();
            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgress(true);
                joinRide joinRide = new joinRide(ride.getId());

                joinRide.execute();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    public boolean isJoined(int id){
        return false;
    }

    public boolean isPending (int id){
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ride_details, menu);
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

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            rideDetailView.setVisibility(show ? View.GONE : View.VISIBLE);
            rideDetailView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    rideDetailView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            rideDetailProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            rideDetailProgress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    rideDetailProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            rideDetailProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            rideDetailView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    class deleteRide extends AsyncTask<Void, Void, Boolean> {

        int ride;

        public deleteRide(int ride){
            this.ride = ride;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();

            HttpClient client = new DefaultHttpClient();

            HttpGet request = new HttpGet("https://rideshare-server-yosef456.c9users.io/deleteride?id=" + ride);
            // replace with your url

            HttpResponse response;
            try {
                response = client.execute(request);

                Log.d("Response of GET request", response.toString());

                return parseResponseForRides(response);

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                return false;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                return false;
            }
        }

        public boolean parseResponseForRides( HttpResponse response){

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
               return false;
            }

            return data.equals("true");

        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success){
                showProgress(false);
                Toast.makeText(ride_details.this, "Ride deleted",
                        Toast.LENGTH_LONG).show();

                Intent intent = new Intent();

                intent.putExtra("delete",true);

                setResult(RESULT_OK,intent);

                finish();
            }
            else{
                TextView error = (TextView) findViewById(R.id.rideDetailError);
                error.setText("An error has occurred");
                showProgress(false);
            }

        }

        @Override
        protected void onCancelled() {

        }

    }

    class joinRide extends AsyncTask<Void, Void, Boolean> {

        int ride;

        public joinRide(int ride) {
            this.ride = ride;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            if (android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();

            HttpClient client = new DefaultHttpClient();

            HttpGet request = new HttpGet("https://rideshare-server-yosef456.c9users.io/pending?ride_id=" + ride + "&pass_id=" + login);
            // replace with your url

            HttpResponse response;
            try {
                response = client.execute(request);

                Log.d("Response of GET request", response.toString());

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

            if (success) {
                showProgress(false);
                Toast.makeText(ride_details.this, "Ride joined",
                        Toast.LENGTH_LONG).show();

//                Intent intent = new Intent();
//
//                intent.putExtra("delete", false);
//
//                setResult(RESULT_OK, intent);

                delete.setVisibility(View.GONE);
                join.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
                chat.setVisibility(View.GONE);
                mode = PENDING_RIDE;

                ((TextView)findViewById(R.id.details_pending)).setText("Ride Pending. Please wait for driver to approve.");

                showProgress(false);

            } else {
                TextView error = (TextView) findViewById(R.id.rideDetailError);
                error.setText("An error has occurred");
                showProgress(false);
            }

        }

        }
    }
