package com.example.chuti.longrange;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

// heading can rotate
public class Calibrate extends Activity implements SensorEventListener {

    // record the compass picture angle turned
    private float currentDegree = 0f;
    private float degree = 0f;
    private double distance = 0;
    private double height = 0;

    // device sensor manager
    private SensorManager mSensorManager;



    TextView tvHeading;
    Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calibrate_page);

        // TextView that will tell the user what degree is he heading
        tvHeading = (TextView) findViewById(R.id.tvHeading);
        receiveValuefromBarometer();
        mButton = (Button)findViewById(R.id.okButton);
        // receiveValue();
        // initialize your android device sensor capabilities
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        mButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        // Call Next page
                        try {
                            passingValueAndCallNextPage();
                        } catch (Exception e) {
                            showErrorMessage("An error occured, please try again later.");

                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // for the system's orientation sensor registered listeners
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // get the angle around the z-axis rotated
        degree = Math.round(event.values[0]);
        if( degree == 360)
            degree = 0;


        tvHeading.setText("Heading: " + Float.toString(degree) + " degrees");

        currentDegree = -degree;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }

    public void passingValueAndCallNextPage(){
        //Passing value from Distance.java

        String latitudeValCurrent = getIntent().getStringExtra("latitudeValCurrent");
        String longitudeValCurrent = getIntent().getStringExtra("longitudeValCurrent");
        String latitudeValDes= getIntent().getStringExtra("latitudeValDes");
        String longitudeValDes = getIntent().getStringExtra("longitudeValDes");

        Intent intent = new Intent(Calibrate.this, Heading.class);
        intent.putExtra("CalibrateVal", String.valueOf(degree)); //want to sent -degree
        intent.putExtra("distanceVal",String.valueOf(distance));
        intent.putExtra("height",String.valueOf(height));
        intent.putExtra("latitudeValCurrent", latitudeValCurrent.toString());
        intent.putExtra("longitudeValCurrent", longitudeValCurrent.toString());
        intent.putExtra("latitudeValDes", latitudeValDes.toString());
        intent.putExtra("longitudeValDes", longitudeValDes.toString());
        startActivity(intent);
    }
    public void showErrorMessage(CharSequence text){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }


    public void receiveValuefromBarometer(){
        distance = Double.valueOf(getIntent().getStringExtra("distanceVal"));
        height = Double.valueOf(getIntent().getStringExtra("height"));
        //Log.v("distance", String.valueOf(distance));
        //Log.v("height", String.valueOf(height));
        //calculateAngle();
    }
}