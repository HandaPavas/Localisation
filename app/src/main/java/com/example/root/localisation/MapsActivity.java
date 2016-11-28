package com.example.root.localisation;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Geocoder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.client.core.Context;
import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseError;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    final MapsActivity context = this;
    private Firebase mFirebase;
    double latitude,longitude;
    String userId;
    Button btnShowLocation;
    String chemist;
    GPSTracker gps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Firebase.setAndroidContext(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFirebase = new Firebase("https://localisation-b533c.firebaseio.com/Locations");

        btnShowLocation = (Button) findViewById(R.id.btnShowLocation);

        btnShowLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View view) {

                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.prompts, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);


                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.description);


                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {

                                       chemist=userInput.getText().toString();
                                        try {
                                            onSearch(view);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });


                AlertDialog alertDialog = alertDialogBuilder.create();


                alertDialog.show();


            }
        });

    }

    public void onSearch(View view) throws IOException {
        gps = new GPSTracker(MapsActivity.this);

        System.out.print(gps.cangetlocation);
        if(gps.canGetLocation()){

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();


            //  Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            LatLng currentlocation = new LatLng(latitude, longitude);
            Geocoder geocoder;
            List<android.location.Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());

            addresses = geocoder.getFromLocation(latitude,longitude,1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            double lat=latitude;
            double lng=longitude;
            userId = mFirebase.push().getKey();
            LocationDetails location = new LocationDetails(chemist,address,city,lat,lng);
            mFirebase.child(userId).setValue(location);

            if(mMap != null){
                Log.i("onSearch:","mMap not null");
                mMap.addMarker(new MarkerOptions().position(currentlocation).title(chemist).snippet(address+"\n"+city));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(currentlocation));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            }
            else
                Log.i("onSearch:","mMap null");
        }else{

            gps.showSettingsAlert();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap= googleMap;
        LatLng focus = new LatLng(22.9734, 78.6569);
        mFirebase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                LocationDetails viewlocation = dataSnapshot.getValue(LocationDetails.class);
                LatLng location = new LatLng(viewlocation.lati, viewlocation.longi);
                mMap.addMarker(new MarkerOptions().position(location).title(viewlocation.description).snippet(viewlocation.address+
                        "\n"+viewlocation.city));
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }


        });
        mMap.moveCamera(CameraUpdateFactory.newLatLng(focus));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(5));
    }


}
