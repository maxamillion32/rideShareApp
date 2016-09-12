package com.example.user.rideshareapp1;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class CharRoom extends Activity {

    ListView list;

    ChatAdapter ada;

    ArrayList<ChatItem> items;

    Button send;

    EditText content;

    int rideID;

    Activity that = this;

    File file;

    Handler handler = new Handler();

    Boolean stop = false;

    private final Runnable m_Runnable = new Runnable()
    {
        public void run()
        {
            readFromFile(file);
            //Toast.makeText(that,"Loaded",Toast.LENGTH_SHORT).show();
            if(!stop)
                handler.postDelayed(m_Runnable,1000);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_char_room);

        list = (ListView) findViewById(R.id.chat);

        items = new ArrayList<>();

        rideID = getIntent().getIntExtra("rideId",0);

        File sdcard = getBaseContext().getFilesDir();

        file = new File(sdcard,"chat_"+ rideID + ".txt");

        content = (EditText) findViewById(R.id.content);

        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                Toast.makeText(that,"Failed to create file",Toast.LENGTH_LONG).show();
                finish();
            }
        }

        ada = new ChatAdapter(this,items);

        list.setAdapter(ada);

        send = (Button) findViewById(R.id.chat_send);

        m_Runnable.run();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(content.getText().toString().trim().equals(""))
                    return;

               // SharedPreferences sharedPreferences = new SecurePreferences(getBaseContext());

                String newEntry =  getIntent().getExtras().getString("name") + ":" + content.getText().toString() + "`";

                try(FileOutputStream fOut = new FileOutputStream(file,true)) {
                    OutputStreamWriter osw = new OutputStreamWriter(fOut);
                    osw.write(newEntry);
                    osw.flush();
                    osw.close();
                } catch (IOException e) {
                    Toast.makeText(that,"Failed to append to file",Toast.LENGTH_LONG).show();
                    finish();
                }

                sendToServer server = new sendToServer(rideID, content.getText().toString());

                content.setText("");

                server.execute();

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        handler.removeCallbacksAndMessages(m_Runnable);

        handler.removeCallbacksAndMessages(null);

        stop = true;

    }

    @Override
    protected void onStop() {
        super.onStop();

        handler.removeCallbacksAndMessages(m_Runnable);

        handler.removeCallbacksAndMessages(null);

        stop = true;

    }

    protected void readFromFile(File file){

        items.clear();

        StringBuilder text = new StringBuilder();

        try (FileReader reader = new FileReader(file); BufferedReader br = new BufferedReader(reader)) {
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
            }
        } catch (IOException e) {
            Toast.makeText(this, "Chat failed to load", Toast.LENGTH_LONG).show();
            finish();
        }

        String chat = text.toString();

        String [] parts = chat.split("`");

        //Makes sense because the names don't have a :
        if(!chat.equals(""))
            for (String part: parts){
                items.add(new ChatItem(part.substring(0,part.indexOf(':')),part.substring(part.indexOf(':')+1)));
            }

        ada.notifyDataSetChanged();
    }



    public class sendToServer extends AsyncTask<Void, Void, Boolean> {

        private final int rideID;
        private final String content;

        sendToServer(int rideID, String content) {
            this.rideID = rideID;
            this.content = content;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();

            HttpClient httpClient = new DefaultHttpClient();
            // replace with your url
            HttpPost httpPost = new HttpPost("https://rideshare-server-yosef456.c9users.io/chat");

            String urlToken;

            urlToken = getIntent().getExtras().getString("token");

            //Post Data
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
            nameValuePair.add(new BasicNameValuePair("token", urlToken));
            nameValuePair.add(new BasicNameValuePair("ride_id", Integer.toString(rideID)));
            nameValuePair.add(new BasicNameValuePair("message", content));
            nameValuePair.add(new BasicNameValuePair("name", getIntent().getExtras().getString("name")));

            //Encoding POST data
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            } catch (UnsupportedEncodingException e) {
                return false;
            }

            try {
                HttpResponse response = httpClient.execute(httpPost);

                String line="";
                String data="";

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
                Toast.makeText(that,"Posted to server",Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(that,"Posting failed",Toast.LENGTH_LONG).show();
            }
        }

    }

}
