package com.example.chuti.longrange;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * Created by chuti on 3/26/2017.
 */
public class Distance extends MainActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener  {
    private GoogleApiClient mGoogleApiClient;
    private TextView mLatitudeTextView;
    private TextView mLongitudeTextView;
    public EditText mLatitudeEditText;
    public EditText mLongitudeEditText;
    private Button mButton;
    public Double longitudeValCurrent;
    public Double latitudeValCurrent;
    public Double longitudeValDes;
    public Double latitudeValDes;
    public Double distance;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.distance_page);
        requestLocation();

        mLatitudeTextView = (TextView) findViewById(R.id.Latitude);
        mLongitudeTextView = (TextView) findViewById(R.id.Longitude);
        mLatitudeEditText = (EditText) findViewById(R.id.LatitudeEdit);
        mLongitudeEditText = (EditText) findViewById(R.id.LongitudeEdit);
        mButton = (Button)findViewById(R.id.button3);
        mButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        try {
                            getCoordinates();
                            printDebug();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        // Call Next page
                        try {
                            callNextActivity();

                        } catch (Exception e) {
                            showErrorMessage("An error occured, please try again later");
                        }
                    }
                });


    }
    public void requestLocation(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onConnected(Bundle bundle) {
        Location lastLocation;
        //if (ActivityCompat.checkSelfPermission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
            }
        }
        lastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);
        if(lastLocation !=null) {
            mLatitudeTextView.setText(String.valueOf(
                    lastLocation.getLatitude()));
            mLongitudeTextView.setText(String.valueOf(
                    lastLocation.getLongitude()));

        }
        else {
            showErrorMessage("Cannot detect location");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Log.i("Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Log.i(TAG, "Connection failed: error code = " + connectionResult.getErrorCode());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    public double meterDistanceBetweenPoints(double lat_a, double lng_a, double lat_b, double lng_b) {
        double pk = (double) (180.0/Math.PI);

        double a1 = Math.toRadians(lat_a);
        double a2 = Math.toRadians(lng_a);
        double b1 = Math.toRadians(lat_b);
        double b2 = Math.toRadians(lng_b);
        double dq= (lat_b-lat_a) /pk;
        double dl = (lng_b-lng_a) / pk;
        double a = Math.sin(dq / 2)* Math.sin(dq / 2)+Math.cos(a1)*Math.cos(b1)*Math.sin(dl / 2)*Math.sin(dl / 2);
        double c = 2*Math.atan2(Math.sqrt(a),Math.sqrt(1-a));
        return 6371e3*c;  // m
    }

    public void printDebug(){
        Log.v("Distance : m ", distance.toString());
        Log.v("latitudeValCurrent", latitudeValCurrent.toString());
        Log.v("longitudeValCurrent", longitudeValCurrent.toString());
        Log.v("latitudeValDes", latitudeValDes.toString());
        Log.v("longitudeValDes", longitudeValDes.toString());
    }

    public void getCoordinates(){
        latitudeValCurrent = Double.parseDouble(mLatitudeTextView.getText().toString());
        longitudeValCurrent = Double.parseDouble(mLongitudeTextView.getText().toString());
        latitudeValDes = Double.parseDouble(mLatitudeEditText.getText().toString());
        longitudeValDes = Double.parseDouble(mLongitudeEditText.getText().toString());
        distance = Double.parseDouble(String.format("%.4f",meterDistanceBetweenPoints(latitudeValCurrent, longitudeValCurrent, latitudeValDes, longitudeValDes))); // 2 decimalD//
    }

    public void callNextActivity(){

        if(latitudeValDes != null && longitudeValDes != null) {
           //for pass data to next activitity
            Intent intent = new Intent(Distance.this, Barometer.class);
            //String eiei = "test";
            intent.putExtra("distanceValue",distance.toString());
            startActivity(intent);

            //startActivity(new Intent(Distance.this, Barometer.class));
        }
        else{
            showErrorMessage("Enter destination");
        }
    }

    public void showErrorMessage(CharSequence text){

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

}
