package com.example.root.localisation;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by root on 11/27/16.
 */
public class GPSTracker extends Service implements LocationListener {

    private final Context mContext;

    boolean isgpsenabled = false;
    boolean isnetworkenabled = false;
    boolean cangetlocation = false;

    Location location;
    double latitude;
    double longitude;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;//10 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    protected LocationManager locationManager;

    public GPSTracker(Context context) {
        this.mContext = context;
        getLocation();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);


            isgpsenabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);


            isnetworkenabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isgpsenabled && !isnetworkenabled) {

                Log.i("Network", "Disabled");

            } else {
                this.cangetlocation = true;
                Log.i("Network", "enabled");

                if (isnetworkenabled) {
                    Log.i("Network", "enabled");
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        Log.i("Network", "Inside Location Manager: ");


                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            Log.i("Network", "Inside Location : ");
                            latitude = location.getLatitude();
                            System.out.print(latitude);
                            longitude = location.getLongitude();
                            System.out.print(longitude);
                        }


                    }
                }

                if (isgpsenabled) {
                    // Toast.makeText(getApplicationContext(),"Inside Network Enabled by Gps",Toast.LENGTH_SHORT).show();
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {

                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                System.out.print(latitude);
                                longitude = location.getLongitude();
                                System.out.print(longitude);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }


    public double getLatitude() {
        if (location != null) {

            latitude = location.getLatitude();
        }



        return latitude;
    }


    public boolean canGetLocation() {
        return this.cangetlocation;
    }


    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);


        alertDialog.setTitle("GPS is settings");


        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");


        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });


        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        alertDialog.show();
    }

    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    @Override
    public void onLocationChanged(Location loc) {

        // Toast.makeText(getApplicationContext(), location.toString(), Toast.LENGTH_LONG).show();
        if(location!=null) {
            latitude =  location.getLatitude();
            longitude = location.getLongitude();
        }


    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
