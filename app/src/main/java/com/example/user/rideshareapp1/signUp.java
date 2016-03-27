package com.example.user.rideshareapp1;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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


public class signUp extends ActionBarActivity {

    private String login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

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
                    signUpClass signUp = new signUpClass(name.getText().toString(),email.getText().toString(),login);

                    signUp.execute();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
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


    class signUpClass extends AsyncTask<Void, Void, Boolean> {

        private String name;
        private String email;
        private String login;
        private String id;

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

            nameValuePair.add(new BasicNameValuePair("login", login));
            nameValuePair.add(new BasicNameValuePair("email", email));
            nameValuePair.add(new BasicNameValuePair("name",name));

            //Encoding POST data
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            } catch (UnsupportedEncodingException e) {
                // log exception
                return false;
            }

            try {
                HttpResponse response = httpClient.execute(httpPost);

                id=parseResponse(response);

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
                Intent intent = new Intent(signUp.this, dashboard.class);

                intent.putExtra("login", Integer.parseInt(id));

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
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
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

        }

    }
}
