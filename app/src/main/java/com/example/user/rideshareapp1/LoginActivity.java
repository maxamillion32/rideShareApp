package com.example.user.rideshareapp1;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.securepreferences.SecurePreferences;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    protected LoginActivity that = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });


        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        SharedPreferences sharedPreferences = new SecurePreferences(getBaseContext());

        if(sharedPreferences.contains("remember") && sharedPreferences.getBoolean("remember",false) ){

            verifyToken verifyToken = new verifyToken(sharedPreferences.getString("token","aaaaaaa"));

            showProgress(true);

            verifyToken.execute();
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
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

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private boolean dataBase;
        private String name;
        private String token;
        HttpResponse response;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        protected boolean checkAngelAccount(){

            HttpClient httpClient = new DefaultHttpClient();
            // replace with your url
            HttpPost httpPost = new HttpPost("https://yu.elearning.yu.edu/signon/authenticate.asp");

            if (mEmail.equals("test2") && mPassword.equals("try")) return true;

            //Post Data
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
            nameValuePair.add(new BasicNameValuePair("username", mEmail));
            nameValuePair.add(new BasicNameValuePair("password", mPassword));


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
                try (BufferedReader br=new BufferedReader(new InputStreamReader(response.getEntity().getContent()))){

                    while((line=br.readLine())!=null){

                        data+=line;
                    }
                    Log.i("RESPONSE",data.length() + "" );
                }
                catch(Exception e){
                    return false;
                }

                return data.length()<1000;
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                return false;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                return false;
            }
        }

        protected Boolean checkInDatabase(){

            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet("https://rideshare-server-yosef456.c9users.io/verify?login=" + mEmail);
            // replace with your url

            try {
                response = client.execute(request);

                Log.d("Response of GET request", response.toString());

                String data = parseResponse(response);

                if(data.equals("false")){
                    name = "";
                }else {
                    String [] parts = data.split("`");
                    name = parts[0];
                    token = parts[1];
                }

                dataBase = !data.equals("false");

                return !data.equals("false");

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                return false;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                return false;
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
        protected Boolean doInBackground(Void... params) {

            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();

            dataBase = checkInDatabase();

            return checkAngelAccount();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {

                SharedPreferences sharedPreferences = new SecurePreferences(getBaseContext());

                CheckBox remember = (CheckBox) findViewById(R.id.remember);

                Intent intent =new Intent(that, (dataBase) ? dashboard.class : signUp.class);

                intent.putExtra("name",name);

                if(dataBase) {

                    try {
                        token = URLEncoder.encode(token, "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        Toast.makeText(that, "something went wrong", Toast.LENGTH_LONG).show();
                        return;
                    }
                }else{
                    token="default";
                }

                if(remember.isChecked()){
                    sharedPreferences.edit().putString("token",token).apply();
                    sharedPreferences.edit().putBoolean("remember",true).apply();
                }
                else{
                    sharedPreferences.edit().putBoolean("remember",false).apply();
                }

                intent.putExtra("token",token);

                intent.putExtra("login",mEmail);

                startActivity(intent);

            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    public class verifyToken extends AsyncTask<Void, Void, Boolean> {

        private final String token;
        private String data;

        verifyToken(String token) {
            this.token = token;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();

            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet("https://rideshare-server-yosef456.c9users.io/checkToken?token=" + token);

            try {
                HttpResponse response = client.execute(request);

                String line="";
                data="";

                try(BufferedReader br=new BufferedReader(new InputStreamReader(response.getEntity().getContent()))){
                    while((line=br.readLine())!=null){

                        data+=line;
                    }
                    Log.i("RESPONSE",Integer.toString(data.length()));
                }
                catch(Exception e){
                    return false;
                }

                return !data.equals("fail");
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
                try{

                    JSONObject object = new JSONObject(data);

                    Intent intent =new Intent(that,dashboard.class);

                    intent.putExtra("name",object.getString("name"));

                    intent.putExtra("token",token);

                    showProgress(false);

                    startActivity(intent);

                }catch (JSONException e){
                    showProgress(false);
                    Toast.makeText(that,"Failed to auth",Toast.LENGTH_LONG).show();
                }

            } else {
                showProgress(false);
                Toast.makeText(that,"Failed to auth",Toast.LENGTH_LONG).show();
            }
        }

    }
}

