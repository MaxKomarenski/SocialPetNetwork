package com.hollybits.socialpetnetwork.models;

/**
 * Created by Victor on 18.08.2018.
 */

public class Coordinates {

    private double latitude;
    private double longitude;

    private byte attitude;

    public byte getAttitude() {
        return attitude;
    }

    public void setAttitude(byte attitude) {
        this.attitude = attitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
