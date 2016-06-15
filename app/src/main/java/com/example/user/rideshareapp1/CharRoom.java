package com.example.user.rideshareapp1;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CharRoom extends Activity {

    ListView list;

    ChatAdapter ada;

    ArrayList<ChatItem> items;

    Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_char_room);

        list = (ListView) findViewById(R.id.chat);

        int rideID = getIntent().getIntExtra("rideId",0);

        File sdcard = getBaseContext().getFilesDir();
        //TODO create file if doesn't exist
        File file = new File(sdcard,"chat_"+ rideID + ".txt");

        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
            }
            br.close();
        }
        catch (IOException e) {
            Toast.makeText(this,"Chat failed to load",Toast.LENGTH_LONG).show();
            finish();
        }

        String chat = text.toString();

        String [] parts = chat.split("`");

        for (String part: parts){
            items.add(new ChatItem(part.substring(0,part.indexOf(':')),part.substring(part.indexOf(':'))));
        }



        ada = new ChatAdapter(this,items);

        list.setAdapter(ada);

        send = (Button) findViewById(R.id.chat_send);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO add it to items, notify adapter on change, update the file, send to server
            }
        });
    }

}
