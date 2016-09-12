package com.example.user.rideshareapp1;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.securepreferences.SecurePreferences;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class search_results extends ActionBarActivity {

    String searchString;

    View searchResultsView;
    View searchResultsProgress;

    ArrayList<Ride> rides;
    ListView list;

    rideAdapter adapter;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK && data.getExtras().getBoolean("delete")){
            showProgress(true);

            rides= new ArrayList<>();

            adapter = new rideAdapter(search_results.this,rides);

            list.setAdapter(adapter);

            searchRides search = new searchRides(searchString,rides,list,adapter);

            search.execute((Void) null);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        rides = new ArrayList<>();

        searchString = getIntent().getStringExtra("search");

        searchResultsView = findViewById(R.id.searchResultsView);
        searchResultsProgress = findViewById(R.id.searchResult_progress);

        list = (ListView) findViewById(R.id.searchResultsList);

        adapter = new rideAdapter(search_results.this,rides);

        list.setAdapter(adapter);

        searchRides search = new searchRides(searchString,rides,list,adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {

                Intent intent = new Intent(search_results.this, ride_details.class);

                Ride picked = rides.get(position);

                intent.putExtra("name",getIntent().getExtras().getString("name"));

                //SharedPreferences sharedPreferences = new SecurePreferences(getBaseContext());

                //if(!sharedPreferences.contains("remember") || !sharedPreferences.getBoolean("remember",false) )
                intent.putExtra("token",getIntent().getExtras().getString("token"));

                intent.putExtra("infoSearch", picked.toString());

                startActivityForResult(intent, 2);

            }
        });

        showProgress(true);

        search.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_results, menu);
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

            searchResultsView.setVisibility(show ? View.GONE : View.VISIBLE);
            searchResultsView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    searchResultsView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            searchResultsProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            searchResultsProgress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    searchResultsProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            searchResultsProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            searchResultsView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    class searchRides extends AsyncTask<Void, Void, Boolean> {

        String origin;
        String dest;
        String date;

        ArrayList<Ride> myRides;
        ListView list;
        rideAdapter adapter;

        public searchRides(String search, ArrayList<Ride> myRides,ListView list, rideAdapter adapter){
            String [] parts = search.split("_");

            this.origin = parts[0];
            this.dest = parts[1];
            this.date = parts[2];

            this.myRides = myRides;
            this.list = list;
            this.adapter = adapter;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();

            HttpClient client = new DefaultHttpClient();

            String url = "https://rideshare-server-yosef456.c9users.io/search?";

            List<NameValuePair> urlParam = new LinkedList<NameValuePair>();

            String urlToken;

           // SharedPreferences sharedPreferences = new SecurePreferences(getBaseContext());

//            if(sharedPreferences.contains("remember") && sharedPreferences.getBoolean("remember",false) )
//                urlToken = sharedPreferences.getString("token","aaaaaaaaaaaaaa");
//            else
                urlToken = getIntent().getExtras().getString("token");

            urlParam.add(new BasicNameValuePair("origin", origin));
            urlParam.add(new BasicNameValuePair("dest", dest ));
            urlParam.add(new BasicNameValuePair("date", date));
            urlParam.add(new BasicNameValuePair("token", urlToken));

            String paramString = URLEncodedUtils.format(urlParam, "utf-8");

            HttpGet request = new HttpGet(url + paramString);
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
                            singleRide.getInt("capacity"), singleRide.getInt("spotstaken"),"non",
                            singleRide.getString("comments")));

                    Log.i("RESPONSE" ,"ride: " + singleRide );
                }

                Log.i("RESPONSE",data.length() + "" );
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
                showProgress(false);
            }
            else{
                TextView error = (TextView) findViewById(R.id.searchResultError);
                error.setText("An error has occured");
            }

        }

        @Override
        protected void onCancelled() {

        }

    }
}
