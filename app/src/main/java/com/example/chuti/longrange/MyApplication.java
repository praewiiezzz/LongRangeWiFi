package com.example.chuti.longrange;

import android.app.Application;

/**
 * Created by chuti on 4/30/2017.
 */
public class MyApplication extends Application {


    private double latitudeValCurrent;
    private double longitudeValCurrent;
    private double latitudeValDes;
    private double longitudeValDes;
    private double distance;
    private double height;
    private double calibrateVal = 0;
    private double realHeading;

    //CurrentLocation
    public double getlatitudeValCurrent() {
        return latitudeValCurrent;
    }

    public void setLatitudeValCurrent(double latitudeValCurrent) {
        this.latitudeValCurrent = latitudeValCurrent;
    }

    public double getlongitudeValCurrent() {
        return longitudeValCurrent;
    }

    public void setLongitudeValCurrent(double longitudeValCurrent) {
        this.longitudeValCurrent = longitudeValCurrent;
    }

    //DestinationLocation
    public double getlatitudeValDes() {
        return latitudeValDes;
    }

    public void setLatitudeValDes(double latitudeValDes) {
        this.latitudeValDes = latitudeValDes;
    }

    public double getlongitudeValDes() {
        return longitudeValDes;
    }

    public void setLongitudeValDes(double longitudeValDes) {
        this.longitudeValDes= longitudeValDes;
    }


    //Distance calculated from latitude and longitude
    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    //Height calculated from pressure between source and destination
    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    //CalibrateVal
    public double getCalibrateVal() {
        return calibrateVal;
    }

    public void setCalibrateVal(double calibrateVal) {
        this.calibrateVal= calibrateVal;
    }

    //CurrentHeading
    public double getCurrentHeading() {
        return realHeading;
    }

    public void setCurrentHeading(double realHeading) {
        this.realHeading= realHeading;
    }

}