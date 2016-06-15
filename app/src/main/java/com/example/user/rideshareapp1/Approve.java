package com.example.user.rideshareapp1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by User on 6/9/2016.
 */
public class Approve {

    private int passId;
    private String name;
    private String dest;
    private int rideId;

    public Approve( String name,String dest,int rideId, int passId){
        this.passId = passId;
        this.name = name;
        this.dest = dest;
        this.rideId = rideId;
    }

    public int getRideId(){ return this.rideId; }
    public int getPassId() { return this.passId; }
    public String getName() { return this.name; }
    public String getDest() { return this.dest; }

    public static void getFromString(JSONArray arr, ArrayList approve) throws JSONException{

        approve.clear();

        for (int i = 0; i < arr.length(); i++) {

            JSONObject o = arr.getJSONObject(i);

            approve.add(new Approve(o.getString("name"),o.getString("dest"),o.getInt("rideid"),o.getInt("passid")));
        }

    }

    public String toString(){
        return this.name + "_" + this.dest + "_" + this.passId + "_" + this.rideId;
    }

    public static Approve fromString(String str){

        String [] part = str.split("_");

        return new Approve(part[0],part[1],Integer.parseInt(part[3]),Integer.parseInt(part[2]));
    }
}
