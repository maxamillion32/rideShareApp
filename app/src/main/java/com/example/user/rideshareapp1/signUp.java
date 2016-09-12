package com.example.user.rideshareapp1;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.securepreferences.SecurePreferences;

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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class signUp extends Activity {

    private String login;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private Activity that = this;

    private static final String TAG = "signup";

    private TextView mInformationTextView;

    private View mLoginFormView;
    private View mProgressView;

    private String nameText;
    private String emailText;

    private signUpClass signUp;

    private boolean isReceiverRegistered;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        isReceiverRegistered=false;

        final TextView name = (TextView) findViewById(R.id.signup_name);
        final TextView email = (TextView) findViewById(R.id.signup_email);

        Button submit = (Button) findViewById(R.id.signup_submit);

        login = getIntent().getStringExtra("login");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().equals("")){
                    name.setError("Can't do empty");
                }
                else if (email.getText().toString().equals("")){
                    email.setError("Can't do empty");
                }
                else{
                    nameText = name.getText().toString();

                    emailText = email.getText().toString();

                    signUp = new signUpClass(nameText,emailText,login);
                    showProgress(true);
                    // mRegistrationProgressBar = (ProgressBar) findViewById(R.id.registrationProgressBar);

                    mInformationTextView = (TextView)findViewById(R.id.signup_error);
                    mRegistrationBroadcastReceiver = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            SharedPreferences sharedPreferences =
                                    PreferenceManager.getDefaultSharedPreferences(context);
                            boolean sentToken = sharedPreferences
                                    .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                            if (sentToken) {
                                mInformationTextView.setText("It worked!");

                                Intent i = new Intent(that,MyGcmListenerService.class);

                                //i.setAction("com.google.android.c2dm.intent.RECEIVE");

                                startService(i);

                                signUp.execute();
                            } else {
                                mInformationTextView.setText("It didn't!");
                            }
                        }
                    };

                    // Registering BroadcastReceiver
                    registerReceiver();

                    if (checkPlayServices()) {
                        // Start IntentService to register this application with GCM.
                        Intent intent = new Intent(that, RegistrationIntentService.class);
                        startService(intent);
                    }

                }
            }
        });
    }

    private void registerReceiver(){
        if(!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }
    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
        return true;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.

        mLoginFormView =  findViewById(R.id.signup_view);
        mProgressView =  findViewById(R.id.signup_progress);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
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


    class signUpClass extends AsyncTask<Void, Void, Boolean> {

        private String name;
        private String email;
        private String login;
        private String id;
        private String token;

        public signUpClass(String name, String email, String login){
            this.name = name;
            this.email = email;
            this.login = login;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            return makeNewAccount();
        }

        protected Boolean makeNewAccount(){

            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();

            HttpClient httpClient = new DefaultHttpClient();
            // replace with your url
            HttpPost httpPost = new HttpPost("https://rideshare-server-yosef456.c9users.io/newaccount");


            //Post Data
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(7);

            SharedPreferences sharedPreferences = new SecurePreferences(getBaseContext());

            String phoneToken = sharedPreferences.getString("token", "fail");

            nameValuePair.add(new BasicNameValuePair("login", login));
            nameValuePair.add(new BasicNameValuePair("email", email));
            nameValuePair.add(new BasicNameValuePair("name",name));
            nameValuePair.add(new BasicNameValuePair("token",phoneToken));

            //Encoding POST data
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            } catch (UnsupportedEncodingException e) {
                // log exception
                return false;
            }

            try {
                HttpResponse response = httpClient.execute(httpPost);

                token =parseResponse(response);

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

                showProgress(false);

                Intent intent = new Intent(signUp.this, dashboard.class);

                SharedPreferences sharedPreferences = new SecurePreferences(getBaseContext());

                try {
                    token = URLEncoder.encode(token, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    Toast.makeText(that,"something went wrong" ,Toast.LENGTH_LONG);
                    return;
                }

                if(sharedPreferences.getBoolean("remember",false))
                    sharedPreferences.edit().putString("token",token).apply();

                intent.putExtra("token", token);

                intent.putExtra("name", name);

                startActivity(intent);
            }
            else {
                TextView error = (TextView) findViewById(R.id.signup_error);

                error.setText("An error has occurred");
            }

        }

        public String parseResponse( HttpResponse response) {

            String line = "";
            String data = "";
            try(BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {

                while ((line = br.readLine()) != null) {

                    data += line;
                }
                Log.i("RESPONSE", data.length() + "");
            } catch (Exception e) {
                e.printStackTrace();
            }

            return data;
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }

    }
}
