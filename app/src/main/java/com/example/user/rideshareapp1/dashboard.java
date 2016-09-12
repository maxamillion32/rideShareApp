package com.example.user.rideshareapp1;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.securepreferences.SecurePreferences;


public class dashboard extends Activity {

    String token = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Button share = (Button) findViewById(R.id.share_button);
        Button myRides = (Button) findViewById(R.id.myrides_button);
        Button search = (Button) findViewById(R.id.search_button);
        Button approve = (Button) findViewById(R.id.dashboard_approve);
        Button logOut = (Button) findViewById(R.id.log_out);

        TextView welcome = (TextView) findViewById(R.id.welcome);

        //SharedPreferences sharedPreferences = new SecurePreferences(getBaseContext());

        //if(!sharedPreferences.contains("remember") || !sharedPreferences.getBoolean("remember",false) )
        token = getIntent().getExtras().getString("token");

        welcome.setText("Welcome, " + getIntent().getExtras().getString("name"));

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(dashboard.this,share_ride_post.class);

                if(token!=null)
                    intent.putExtra("token",token);

                intent.putExtra("name",getIntent().getExtras().getString("name"));

                startActivity(intent);
            }
        });

        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(dashboard.this,approve_ride.class);

                if(token!=null)
                    intent.putExtra("token",token);

                intent.putExtra("name",getIntent().getExtras().getString("name"));
                startActivity(intent);
            }
        });

        myRides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(dashboard.this,my_rides.class);

                if(token!=null)
                    intent.putExtra("token",token);

                intent.putExtra("name",getIntent().getExtras().getString("name"));
                startActivity(intent);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(dashboard.this,search_form.class);

                if(token!=null)
                    intent.putExtra("token",token);

                intent.putExtra("name",getIntent().getExtras().getString("name"));
                startActivity(intent);
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sharedPreferences = new SecurePreferences(getBaseContext());

                sharedPreferences.edit().clear().apply();

                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
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
