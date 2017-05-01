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
    // device sensor manager
    private SensorManager mSensorManager;

    TextView tvHeading;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.heading_page);
        getCalibrateValue();

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
                            callNextPage();
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
    public void onSensorChanged(SensorEvent event)
    {
        // get the angle around the z-axis rotated
        float degree = Math.round(event.values[0]);
        degree = calibrateDegree(degree);
        realHeading = degree;
        ((MyApplication) this.getApplication()).setCurrentHeading(realHeading);
        tvHeading.setText("Heading: " + Float.toString(degree) + " degrees");
        //Log.v("CurrentHead", String.valueOf(String.valueOf(((MyApplication) this.getApplication()).getCurrentHeading())));

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }

    public void callNextPage()
    {
        Intent intent = new Intent(Heading.this, RotateHorizontal.class);
        startActivity(intent);
   }

    public void showErrorMessage(CharSequence text){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void getCalibrateValue()
    {
        calibrate = ((MyApplication) this.getApplication()).getCalibrateVal();
        Log.v("Calibrate value fc", String.valueOf(String.valueOf(((MyApplication) this.getApplication()).getCalibrateVal())));
    }

    public float calibrateDegree(float degree)
    {

        if(degree < 360)
        {
            System.out.print("degree "+degree+"calibrate"+calibrate);
            Log.v("degree before", String.valueOf(degree));
            Log.v("calibrate val", String.valueOf(calibrate));
            degree = degree +(360-(float)calibrate);
            Log.v("degree after",String.valueOf(degree));
            degree %=360;
            Log.v("degree mod",String.valueOf(degree));
        }
        else if( degree == 360)
        {
            degree = degree-(float)calibrate;
        }
        return degree;
    }

    public void printDebug(){
        Log.v("Calibrate2n", String.valueOf(((MyApplication) this.getApplication()).getCalibrateVal()));
        Log.v("Heading2n", String.valueOf(((MyApplication) this.getApplication()).getCurrentHeading()));
        Log.v("Distance2n : m ", String.valueOf(((MyApplication) this.getApplication()).getDistance()));
        Log.v("latitudeValCurrent2n", String.valueOf(((MyApplication) this.getApplication()).getlatitudeValCurrent()));
        Log.v("longitudeValCurrent2n", String.valueOf(((MyApplication) this.getApplication()).getlongitudeValCurrent()));
        Log.v("latitudeValDes2n", String.valueOf(((MyApplication) this.getApplication()).getlatitudeValDes()));
        Log.v("longitudeValDes2n", String.valueOf(((MyApplication) this.getApplication()).getlongitudeValDes()));
    }
}