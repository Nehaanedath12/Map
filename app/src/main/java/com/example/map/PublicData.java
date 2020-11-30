package com.example.map;

import android.location.Location;

public class PublicData {

    public static Location location;
    public static int count;

    public static void clearLocation(){
        location=null;
        count=0;
    }

}
