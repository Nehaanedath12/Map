package com.example.map;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;

    SupportMapFragment supportMapFragment;
    GoogleMap gMap;
    FusedLocationProviderClient client;
    TextView textView_loc;
    SwipeRefreshLayout refreshLayout;
    boolean flag=true;
    Marker marker2;
    private Polyline polyline;
    Marker marker;
    private List<LatLng> polylinePoints;
    String result;




    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView_loc=findViewById(R.id.text_loc);
        refreshLayout=findViewById(R.id.refresh_layout);
        polylinePoints=new ArrayList<>();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        client = LocationServices.getFusedLocationProviderClient(this);



        isLocationEnabled();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        }else {
           ScheduleJob scheduleJob=new ScheduleJob();
            scheduleJob.GetMapService(this);




            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                    Location location = PublicData.location;

                    refreshLayout.setRefreshing(false);

                    if (location != null) {
                        Double lat = location.getLatitude();
                        Double longi = location.getLongitude();
                        textView_loc.setText("latitude : " + lat + "  " + "longitude : " + longi);
                        Log.d("locationnnM", location.getLongitude() + "");

                        Geocoder geocoder = new Geocoder(getApplicationContext());
                        try {
                            List<Address> addresses =
                                    geocoder.getFromLocation(lat, longi, 1);
                             result = addresses.get(0).getLocality() + ":";
                            result += addresses.get(0).getCountryName();

                            supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(GoogleMap googleMap) {
                                    gMap = googleMap;
//                                    gMap.setMapType(gMap.MAP_TYPE_SATELLITE);

                                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                                    if (flag) {
                                        marker2 = gMap.addMarker(new MarkerOptions().position(latLng).title(result).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
                                    }



                                    if (marker != null) {
                                        marker.remove();

                                        marker = gMap.addMarker(new MarkerOptions().position(latLng).title(result).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                                        gMap.setMaxZoomPreference(20);
                                        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18.0f));

                                    } else {
                                        marker = gMap.addMarker(new MarkerOptions().position(latLng).title(result).icon(BitmapDescriptorFactory.defaultMarker(120.0f)));
                                        gMap.setMaxZoomPreference(20);
                                        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18.0f));
                                    }

                                    polylinePoints.add(latLng);


                                    if (polyline != null) {
                                        polyline.setPoints(polylinePoints);

                                    } else {
                                        polyline = gMap.addPolyline(new PolylineOptions().addAll(polylinePoints).color(Color.MAGENTA).jointType(JointType.ROUND).width(3.0f));
                                    }

                                    flag = false;

//                                    MarkerOptions options = new MarkerOptions().position(latLng);
//                                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
//                                    googleMap.addMarker(options);
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

//                        refreshLayout.setRefreshing(false);
                    } else {
                        Toast.makeText(MainActivity.this, "wait!!", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            }
        }




    @Override
    protected void onResume() {
        super.onResume();
        isLocationEnabled();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "allow permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void isLocationEnabled() {

        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
            alertDialog.setTitle("Enable Location");
            alertDialog.setMessage("Your locations setting is not enabled. Please enabled it in settings menu.");
            alertDialog.setPositiveButton("Location Settings", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            AlertDialog alert=alertDialog.create();
            alert.show();
        }
        else{
//            AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
//            alertDialog.setTitle("Confirm Location");
//            alertDialog.setMessage("Location is enabled");
//            alertDialog.setNegativeButton("Back",new DialogInterface.OnClickListener(){
//                public void onClick(DialogInterface dialog, int which){
//                    dialog.cancel();
//                }
//            });
//            AlertDialog alert=alertDialog.create();
//            alert.show();
            Toast.makeText(this, "location enabled", Toast.LENGTH_SHORT).show();
        }
    }
}