package com.example.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateFormat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class GetMapService extends JobService {

    JobParameters params;
    LocationManager locationManager;
    Location lastLocation;

    @Override
    public boolean onStartJob(JobParameters params) {
        this.params = params;
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        asyncTask();


        return true;
    }

    private void asyncTask() {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {

                    Handler handler = new Handler(Looper.getMainLooper());

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                    ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            } else {
                                Log.d("locationnLat", "location.getLatitude"+"" );

                                LocationListener locationListenerGPS = new LocationListener() {
                                    @Override
                                    public void onLocationChanged(@NonNull Location location) {

                                        if (location != null) {
                                            lastLocation = location;
                                            Log.d("locationnLat", location.getLatitude()+"" );
                                            Log.d("locationnLon", location.getLongitude()+"" );
                                            PublicData.location=location;

                                        }
                                    }

                                    @Override
                                    public void onStatusChanged(String provider, int status, Bundle extras) {

                                    }

                                    @Override
                                    public void onProviderEnabled(@NonNull String provider) {

                                    }

                                    @Override
                                    public void onProviderDisabled(@NonNull String provider) {

                                    }
                                };

                                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                        2000,
                                        10, locationListenerGPS);
                            }
                        }
                    }, 1000 );

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        };
        asyncTask.execute();
    }


    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
    public double location(){
        return lastLocation.getLatitude();
    }
}
