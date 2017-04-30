package com.example.chuti.longrange;

import android.app.Application;

/**
 * Created by chuti on 4/30/2017.
 */
public class MyApplication extends Application {


    /*
      intent.putExtra("distanceValue",distance.toString());
            intent.putExtra("latitudeValCurrent", latitudeValCurrent.toString());
            intent.putExtra("longitudeValCurrent", longitudeValCurrent.toString());
            intent.putExtra("latitudeValDes", latitudeValDes.toString());
            intent.putExtra("longitudeValDes", longitudeValDes.toString());

    */
    private double latitudeValCurrent;
    private double distance;

    public double getlatitudeValCurrent() {
        return latitudeValCurrent;
    }

    public void setLatitudeValCurrent(double latitudeValCurrent) {
        this.latitudeValCurrent = latitudeValCurrent;
    }


    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}