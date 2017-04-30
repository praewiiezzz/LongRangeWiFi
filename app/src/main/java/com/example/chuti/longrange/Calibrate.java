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
        mButton = (Button)findViewById(R.id.okButton);
        // initialize your android device sensor capabilities
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        mButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        // Call Next page
                        try {
                            shareCurrentDegree();
                            Log.v("Degree",String.valueOf(degree));
                            CallNextPage();
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
        //TODO create compass
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }

    public void CallNextPage(){
        Intent intent = new Intent(Calibrate.this, Heading.class);
        startActivity(intent);
    }

    public void shareCurrentDegree()
    {
        ((MyApplication) this.getApplication()).setCalibrateVal(degree);
        Log.v("degree Calibrate",String.valueOf(degree));
    }
    public void showErrorMessage(CharSequence text){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

}