package com.example.user.rideshareapp1;

import java.io.IOException;

/**
 * Created by User on 3/4/2016.
 */
public class Ride {

    int id;
    String userID;
    String origin;
    String dest;
    String start;
    String end;
    String taxiType;
    Boolean menOnly;
    Boolean womenOnly;
    int capacity;
    String comments;

    public Ride(int id, String UserID, String origin, String dest, String start, String end, int capacity,String comments){
        this.id=id;
        this.userID=UserID;
        this.origin=origin;
        this.dest=dest;
        this.start=start;
        this.end=end;
        this.capacity=capacity;
        this.comments=comments;
    }

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
    public String getDriver(){
        return this.userID;
    }

    public String toString()
    {
        return this.origin + " " + this.dest + " " + this.start + " " + this.end + " " + Integer.toString(this.capacity) + " " + this.comments;
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




