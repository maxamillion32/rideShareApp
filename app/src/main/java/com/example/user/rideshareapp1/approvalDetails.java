package com.example.user.rideshareapp1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

public class approvalDetails extends ActionBarActivity {

    TextView name;
    TextView error;
    TextView dest;

    Button approve;
    Button reject;

    int login;

    Approve app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_details);

        name = (TextView) findViewById(R.id.approval_name);
        dest = (TextView) findViewById(R.id.approve_details);
        error = (TextView) findViewById(R.id.approve_details_error);

        approve = (Button) findViewById(R.id.approve);
        reject = (Button) findViewById(R.id.reject);

        app = Approve.fromString(getIntent().getExtras().getString("infoApprove"));

        name.setText(app.getName());
        dest.setText(app.getDest());

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        login = sharedPreferences.getInt("login", 0);

        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApproveRequest approveRequest = new ApproveRequest(true);

                approveRequest.execute();
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApproveRequest approveRequest = new ApproveRequest(false);

                approveRequest.execute();
            }
        });
    }

    class ApproveRequest extends AsyncTask<Void, Void, Boolean> {

        Boolean approved;

        public ApproveRequest(Boolean approved) {
            super();

            this.approved = approved;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            if (android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();

            HttpClient client = new DefaultHttpClient();

            HttpGet request = new HttpGet("https://rideshare-server-yosef456.c9users.io/approve?status=" + approved.toString()
                                    + "&ride_id=" + app.getRideId() + "&pass_id=" + app.getPassId() );
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
        //TODO UPDATE THE LIST OF APPROVES AFTER APPROVING A RIDE

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {

                String app = (approved) ? "approve" : "rejected";

                Toast.makeText(getApplicationContext(),"Request " + app, Toast.LENGTH_LONG).show();

                Intent intent = new Intent();

                intent.putExtra("approve",true);

                setResult(RESULT_OK, intent);

                finish();
            } else {

                error.setText("An error has occurred");
            }

        }

    }

}
