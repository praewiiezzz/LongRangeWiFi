package com.example.chuti.longrange;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

// heading can rotate
public class Heading extends Activity implements SensorEventListener {

    // record the compass picture angle turned
    private double calibrate = 0;
    private double realHeading = 0;
    private double distance = 0;
    private double height = 0;
    // device sensor manager
    private SensorManager mSensorManager;

    TextView tvHeading;
    Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.heading_page);

        receiveValue();

        // TextView that will tell the user what degree is he heading
        tvHeading = (TextView) findViewById(R.id.tvHeading);
        mButton = (Button)findViewById(R.id.okButton);

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
        float degree = Math.round(event.values[0]);
        degree = calibrateDegree(degree);
        realHeading = degree;
        tvHeading.setText("Heading: " + Float.toString(degree) + " degrees");

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }

    public void passingValueAndCallNextPage(){
        //Passing value from Distance.java
        double latitudeValCurrent = Double.parseDouble(getIntent().getStringExtra("latitudeValCurrent"));
        double longitudeValCurrent = Double.parseDouble(getIntent().getStringExtra("longitudeValCurrent"));
        double latitudeValDes= Double.parseDouble(getIntent().getStringExtra("latitudeValDes"));
        double longitudeValDes = Double.parseDouble(getIntent().getStringExtra("longitudeValDes"));

        Intent intent = new Intent(Heading.this, RotateHorizontal.class);
        intent.putExtra("CurrentHeading",String.valueOf(realHeading)); // currentDegree = -degree want to send degree
        intent.putExtra("CalibrateVal", String.valueOf(calibrate));
        intent.putExtra("distanceVal",String.valueOf(distance));
        intent.putExtra("height",String.valueOf(height));
        intent.putExtra("latitudeValCurrent", String.valueOf(latitudeValCurrent));
        intent.putExtra("longitudeValCurrent", String.valueOf(longitudeValCurrent));
        intent.putExtra("latitudeValDes", String.valueOf(latitudeValDes));
        intent.putExtra("longitudeValDes", String.valueOf(longitudeValDes));
        System.out.println("CurrentHeading " + realHeading);
        startActivity(intent);

        //////////// degub
        Log.v("Distance4 : m ", String.valueOf(distance));
        Log.v("latitudeValCurrent4", String.valueOf(latitudeValCurrent));
        Log.v("longitudeValCurrent4", String.valueOf(longitudeValCurrent));
        Log.v("latitudeValDes4", String.valueOf(latitudeValDes));
        Log.v("longitudeValDes4", String.valueOf(longitudeValDes));
    }
    public void showErrorMessage(CharSequence text){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void receiveValue()
    {
        calibrate = Double.valueOf(getIntent().getStringExtra("CalibrateVal"));

        distance = Double.valueOf(getIntent().getStringExtra("distanceVal"));
        height = Double.valueOf(getIntent().getStringExtra("height"));
        Log.v("Calibrate value fc", String.valueOf(calibrate));
    }

    public float calibrateDegree(float degree)
    {
        if(degree < 360)
        {
            degree = degree +(360-(float)calibrate);
            degree %=360;
        }
        else if( degree == 360)
        {
            degree = degree-(float)calibrate;
        }
        return degree;
    }
}