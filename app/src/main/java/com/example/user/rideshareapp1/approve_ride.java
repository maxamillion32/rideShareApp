package com.example.user.rideshareapp1;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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



public class approve_ride extends ActionBarActivity {

    View rideApproveView;
    View rideApproveProgress;

    ArrayList approve;
    ListView list;

    approveAdapter adapter;

    Activity that = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_ride);

        rideApproveView = findViewById(R.id.rideApprove);
        rideApproveProgress = findViewById(R.id.ride_approve_progress);

        list = (ListView) findViewById(R.id.approvalList);

        approve = new ArrayList<>();

        adapter = new approveAdapter(approve_ride.this,approve);

        list.setAdapter(adapter);

        showProgress(true);

        ApproveList approveList = new ApproveList();

        approveList.execute();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(approve_ride.this, approvalDetails.class);

                Approve picked = (Approve) approve.get(i);

//                SharedPreferences sharedPreferences = new SecurePreferences(getBaseContext());
//
//                if(!sharedPreferences.contains("remember") || !sharedPreferences.getBoolean("remember",false) )
                intent.putExtra("token",getIntent().getExtras().getString("token"));

                intent.putExtra("name", getIntent().getExtras().getString("name"));

                intent.putExtra("infoApprove", picked.toString());

                startActivityForResult(intent,2);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK && data.getExtras().getBoolean("approve")) {

            approve = new ArrayList<>();

            adapter = new approveAdapter(approve_ride.this,approve);

            list.setAdapter(adapter);

            ApproveList approveList = new ApproveList();

            approveList.execute((Void) null);
        }
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

            rideApproveView.setVisibility(show ? View.GONE : View.VISIBLE);
            rideApproveView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    rideApproveView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            rideApproveProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            rideApproveProgress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    rideApproveProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            rideApproveProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            rideApproveView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    class ApproveList extends AsyncTask<Void, Void, Boolean> {

        public ApproveList() {
            super();
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            if (android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();

            HttpClient client = new DefaultHttpClient();

            String token;

//            SharedPreferences sharedPreferences = new SecurePreferences(getBaseContext());
//
//            if(sharedPreferences.contains("remember") && sharedPreferences.getBoolean("remember",false) )
//                token = sharedPreferences.getString("token","aaaaaaaaaaaaaa");
//            else
            token = getIntent().getExtras().getString("token");

            HttpGet request = new HttpGet("https://rideshare-server-yosef456.c9users.io/getpending?token=" + token);
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
            try(BufferedReader br=new BufferedReader(new InputStreamReader(response.getEntity().getContent()))){
                while((line=br.readLine())!=null){

                    data+=line;
                }
                Log.i("RESPONSE", data.length() + "");

                JSONArray  arr = new JSONArray(data);

                Approve.getFromString(arr,approve);

                return true;
            }
            catch(Exception e){
                return false;
            }

        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {
                showProgress(false);

                adapter.notifyDataSetChanged();

            } else {

                showProgress(false);

                Toast.makeText(that, "An error has occurred",
                        Toast.LENGTH_LONG).show();
            }

        }

    }

}
