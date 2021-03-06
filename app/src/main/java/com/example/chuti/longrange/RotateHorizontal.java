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
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chuti.longrange.Calibrate;
import com.example.chuti.longrange.R;

/**
 * Created by chuti on 4/23/2017.
 */
public class RotateHorizontal extends Activity implements SensorEventListener {

    // define the display assembly compass picture
    private ImageView done;
    private double calibrate;
    private double heading;
    private double angle;
    private double total;
    private ImageView image;
    private Button mButton;
    private double distance = 0;

    double latitudeValCurrent = 0;
    double longitudeValCurrent = 0;
    double latitudeValDes= 0;
    double longitudeValDes = 0;
    // record the compass picture angle turned
    private float currentDegree = 0f;

    // device sensor manager
    private SensorManager mSensorManager;

    TextView tvHeading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.horizontal_page);
        getValue();

        image = (ImageView) findViewById(R.id.rotateImage);
        done = (ImageView)findViewById(R.id.done);
        mButton = (Button)findViewById(R.id.nextButton);

        mButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        try {
                            callNextPage();
                        } catch (Exception e) {
                            showErrorMessage("An error occured, please try again later.");
                        }
                    }
                });

        // TextView that will tell the user what degree is he heading
        tvHeading = (TextView) findViewById(R.id.Heading);

        // initialize your android device sensor capabilities
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        calculateAngle();
    }

    public double calculateTheta(double[] positionA,double[] positionB)
    {
        double difX = positionB[0] - positionA[0];
        double difY = positionB[1] - positionA[1];
        double rotAng = Math.toDegrees(Math.atan2(difX, difY));
        System.out.println(rotAng);
        return rotAng;
    }

    public void calculateAngle()
    {
        double[] positionA = {latitudeValCurrent,longitudeValCurrent};
        double[] positionB = {latitudeValDes,longitudeValDes};
        double theta = calculateTheta(positionA, positionB);
        angle = calDegree(positionA[0],positionB[0],positionA[1],positionB[1],theta);
        if(angle >= 0) {
            ((TextView) findViewById(R.id.angleSuggest)).setText("Rotate the Antenna " + String.format("%.1f", angle)+"°\nclockwise");
        }
        else
            ((TextView) findViewById(R.id.angleSuggest)).setText("Rotate the Antenna " + String.format("%.1f", angle)+"°\ncounter-clockwise");

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
        degree %=360;

        showTotalAngle();
        showMarkVector(degree);
        showCompassAnimation(degree);

    }

    public void showTotalAngle()
    {
        total = heading+angle;
        total%=360;
        ((TextView) findViewById(R.id.totalAngle)).setText("Target angle : " + String.format("%.1f", total) + "°");
    }

    public void showMarkVector(float degree)
    {
        if(degree == Math.ceil(total) || degree == Math.floor(total) )
            done.setVisibility(View.VISIBLE);
        else
            done.setVisibility(View.INVISIBLE);

        tvHeading.setText(Integer.toString((int)degree) + "°");
    }

    public void showCompassAnimation(float degree)
    {
        // create a rotation animation (reverse turn degree degrees)
        RotateAnimation ra = new RotateAnimation(-currentDegree, degree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        // how long the animation will take place
        ra.setDuration(210);
        // set the animation after the end of the reservation status
        ra.setFillAfter(true);
        // Start the animation
        image.startAnimation(ra);
        currentDegree = -degree;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }

    public void getValue()
    {
        calibrate =((MyApplication) this.getApplication()).getCalibrateVal();
        heading = ((MyApplication) this.getApplication()).getCurrentHeading();
        distance = ((MyApplication) this.getApplication()).getDistance();
        latitudeValCurrent = ((MyApplication) this.getApplication()).getlatitudeValCurrent();
        longitudeValCurrent = ((MyApplication) this.getApplication()).getlongitudeValCurrent();
        latitudeValDes= ((MyApplication) this.getApplication()).getlatitudeValDes();
        longitudeValDes = ((MyApplication) this.getApplication()).getlongitudeValDes();
    }

    public double calDegree(double latA, double latB,double lonA,double lonB,double theta)
    {
        double an = heading;  // angle from a to North
        System.out.println("an"+an);
        double a = 0; // angle from a to right direction
        double pi = 180;
        System.out.println("Theta: "+theta);
        if(latA > latB && lonA > lonB)
        {
            System.out.println("a left bot, b right top");
            a = (-an + pi + pi/2 - theta);
        }
        else if(latA > latB && lonA < lonB)
        {	System.out.println("a left bot, b right top");
            a = (-an + pi/2 + theta);
        }
        else if(latA < latB && lonA > lonB)
        {
            System.out.println("a right bot, b left top");
            a = (-an + pi + pi/2 + theta);
        }
        else if(latA < latB && lonA < lonB)
        {	System.out.println("a right top, b left bot");
            a = (-an + pi/2 - theta);
        }

        // - is counter-clockwise, + is clockwise
        a = a%360;
        System.out.println("a " + (a));
        return a;
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

    public void showErrorMessage(CharSequence text){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void callNextPage(){
        Intent intent = new Intent(RotateHorizontal.this, Rotation.class);
        startActivity(intent);
    }

    public void printDebug()
    {
        Log.v("Distance5 : m ", String.valueOf(distance));
        Log.v("latitudeValCurrent5", String.valueOf(latitudeValCurrent));
        Log.v("longitudeValCurrent5", String.valueOf(longitudeValCurrent));
        Log.v("latitudeValDes5", String.valueOf(latitudeValDes));
        Log.v("longitudeValDes5", String.valueOf(longitudeValDes));
        Log.v("Calibrate value 2", String.valueOf(calibrate));
        Log.v("oldHeading 2", String.valueOf(heading));
    }

}
