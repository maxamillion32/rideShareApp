package com.example.user.rideshareapp1;

import java.io.IOException;

/**
 * Created by User on 3/4/2016.
 */
public class Ride {

    private int id;
    private int userID;
    private String name;
    private String email;
    private String origin;
    private String dest;
    private String date;
    private String start;
    private String end;
    private String type;
    private int spotTaken;
    private int capacity;
    private String status;
    private String comments;


    public Ride(int id, int UserID,String name,String email,String type ,String origin
            ,String dest, String date,String start, String end, int capacity,int spotsTaken,String status,String comments){
        this.id=id;
        this.userID=UserID;
        this.name = name;
        this.email=email;
        this.type = type;
        this.origin=origin;
        this.dest=dest;
        this.date = date;
        this.start=start;
        this.end=end;
        this.capacity=capacity;
        this.spotTaken = spotsTaken;
        this.status = status;
        this.comments=comments;
    }

    public int getId(){ return this.id;}
    public String getOrigin(){
        return this.origin;
    }
    public String getDest(){
        return this.dest;
    }
    public String getTimeStart(){
        return this.start;
    }
    public String getTimeEnd(){
        return this.end;
    }
    public int getDriver(){
        return this.userID;
    }
    public int getSpotTaken() { return this.spotTaken;}

    public String getDate(){
        return this.date;
    }

    public int getCapacity(){
        return this.capacity;
    }

    public String getComments(){
        return this.comments;
    }

    public String getName(){ return this.name;}

    public  String getEmail(){ return this.email;}

    public String getType(){ return type;}

    public String getStatus(){ return status;}

    public String toString()
    {
        return this.id +"_" +this.userID +"_" + this.name +"_" + this.email+"_"+ this.type+"_"+this.origin + "_" + this.dest +"_"+
            this.date + "_" + this.start + "_" + this.end + "_" + Integer.toString(this.capacity) +
                "_" +this.spotTaken+ "_"+ this.status + "_" + this.comments;
    }

    public static boolean isOnline() {

        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 https://rideshare-server-yosef456.c9users.io/");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }
}




