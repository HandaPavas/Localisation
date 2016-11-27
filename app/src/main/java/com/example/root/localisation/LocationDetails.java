package com.example.root.localisation;

/**
 * Created by root on 11/28/16.
 */
public class LocationDetails {
    public String address;
    public String city;
    double lati;
    double longi;


    public LocationDetails() {
    }

    public LocationDetails(String address, String city, double latitude, double longitude) {
        this.address = address;
        this.city = city;
        this.lati = latitude;
        this.longi = longitude;
    }
}
