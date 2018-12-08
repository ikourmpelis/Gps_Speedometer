package com.example.giannis.ergasia1;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements LocationListener {
    TextView textView1;
    TextView longView ;
    TextView latView ;
    TextView limitView ;
    TextView locView;




    private final long createdMillis = System.currentTimeMillis();
    private CustomGauge gauge1;
    LocationManager locationManager;
    ArrayList<Double> Longs = new ArrayList<>();
    ArrayList<Double> Lats = new ArrayList<>();
    ArrayList<Double> Intervals = new ArrayList<>();
    int speed = 0;
    public boolean waiting1 = false;
    public boolean waiting2 = false;
    public boolean waiting3 = false;
    public String cityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView1 = (TextView) findViewById(R.id.textView1);
        latView = (TextView) findViewById(R.id.latView);
        longView =(TextView) findViewById(R.id.longView);
        locView = (TextView) findViewById(R.id.locView);
        limitView = (TextView) findViewById(R.id.limitView);
        gauge1 = findViewById(R.id.gauge1);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // set firebase persistance enabled if there is no network
       if(isNetworkAvailable() == false){FirebaseDatabase.getInstance().setPersistenceEnabled(true);}else {FirebaseDatabase.getInstance().setPersistenceEnabled(false);}

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
        limitView.setText(String.valueOf(sp.getInt("limit1", 30)+","+sp.getInt("limit2", 60)+","+sp.getInt("limit3", 90)));



    }
    @Override
    public void onResume(){
        super.onResume();
        SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
        limitView.setText(String.valueOf(sp.getInt("limit1", 30)+","+sp.getInt("limit2", 60)+","+sp.getInt("limit3", 90)));



    }

    public void gpson(View view) {
        //enable location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, this);
            // locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,this);
        }
    }

    public void gpsoff(View view) {
        //disable location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        } else {
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onLocationChanged(final Location location) {
        SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
        //get setted limits
        final int limit1 = sp.getInt("limit1", 30);
        final int limit2 = sp.getInt("limit2", 60);
        final int limit3 = sp.getInt("limit3", 90);
        //add time interval portions , longitudes , and latitudes to lists for each location change
        Intervals.add(getAgeInSeconds());
        Longs.add(location.getLongitude());
        Lats.add(location.getLatitude());
        longView.setText(String.valueOf(location.getLongitude()));
        latView.setText(String.valueOf(location.getLatitude()));
        DecimalFormat df = new DecimalFormat("###.##");
        //calculate current speed
        if (Longs.size() >= 2 && Lats.size() >= 2) {
            for (int j = 0; j < Lats.size() - 1; j++) {
                double d = calculateDistance(Lats.get(j),Longs.get(j),Lats.get(j+1),Longs.get(j+1))/ (Intervals.get(j + 1) - Intervals.get(j)) * 3600;
                speed = (int) d;
                gauge1.setValue(speed);
            }

        }
        //get current location
        Thread locationThread = new Thread() {
            @Override
            public void run() {
                try {
                    cityName = getCityName(location.getLatitude(), location.getLongitude());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        locationThread.start();


//get low risk violation
        Thread thread1 = new Thread() {
            @Override
            public void run() {
                try {
                    if (!waiting1) {
                        System.out.println("Runned");
                        waiting1 = true;
                        String loc = getCityName(location.getLatitude(), location.getLongitude());
                         newReference(speed,loc,"low risk");
                        sleep(10000);
                        waiting1 = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };


//get medium risk violation
        Thread thread2 = new Thread() {
            @Override
            public void run() {
                try {
                    if (!waiting2) {
                        System.out.println("Runned");
                        waiting2 = true;
                        String loc = getCityName(location.getLatitude(), location.getLongitude());
                        // newReference(speed, loc);
                        newReference(speed,loc,"medium risk");
                        sleep(10000);
                        waiting2 = false;
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        //get high risk violation
        Thread thread3 = new Thread() {
            @Override
            public void run() {
                try {
                    if (!waiting3) {
                        System.out.println("Runned");
                        waiting3 = true;
                        String loc = getCityName(location.getLatitude(), location.getLongitude());
                        // newReference(speed, loc);
                       newReference(speed,loc,"high risk");
                        sleep(10000);
                        waiting3 = false;
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

//violation conditions
        locView.setText(cityName);
        if (speed > limit1 && speed < limit2) {
            thread1.start();
        }
        if (speed > limit2 && speed < limit3) {
            thread2.start();
        }
        if (speed > limit3 && speed < 260) {
            thread3.start();
        }
        if (speed > 260) {
            textView1.setText("Recalibrating.. ");
        } else {

            textView1.setText(String.valueOf(speed) + " km/h ");

        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
//calculate distance between location changes method
    private static double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double distanceInKm = (6371000 * c) / 1000;
        return distanceInKm;
    }
    //calculate time passed between location changes method
    public double getAgeInSeconds() {
        long nowMillis = System.currentTimeMillis();
        return (double) ((nowMillis - this.createdMillis) / 1000);
    }

// open violations activity
    public void openViolations(View view) {
        Intent myIntent = new Intent(MainActivity.this, SeeViolationsActivity.class);

        MainActivity.this.startActivity(myIntent);


    }
//open settings activity
    public void openSettings(View view) {
        Intent myIntent = new Intent(MainActivity.this, SettingsActivity.class);
        MainActivity.this.startActivity(myIntent);

    }
    // save violation to database method
public void newReference( int speed, String loc ,String risk){
    Date currentTime = Calendar.getInstance().getTime();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    mDatabase.child("Speed Violation").push().setValue(speed + " km/h " + " at " + currentTime + System.lineSeparator()+ "on " + loc +System.lineSeparator()+risk);

}
// find adress method
    public String getCityName(double currentLat, double currentLong) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(currentLat, currentLong, 1);
            if (addresses != null && addresses.size() > 0) {
                String address = addresses.get(0).getAddressLine(0);
                cityName =address +","+ addresses.get(0).getLocality();
            }else {cityName = "failed to locate " ;}
        } catch (IOException e) {
            textView1.setText("ERROR");
        }


        return cityName;
    }

// check if network is available method
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
