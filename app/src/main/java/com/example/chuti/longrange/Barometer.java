package com.example.chuti.longrange;

/**
 * Created by chuti on 3/27/2017.
 */
import android.app.Activity;
import android.app.Service;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class Barometer extends Activity implements SensorEventListener {

    private TextView pressView;
    public EditText pressureEd;
    public Double pressure;
    public Double pressureDes;
    private int num = 0;
    private Button mButton;
    public double height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barometer_page);
        pressView = (TextView) findViewById(R.id.pressTxt);
        pressureEd = (EditText) findViewById(R.id.pressureEdit);

        mButton = (Button)findViewById(R.id.buttonNext);

        mButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        try {
                            pressureDes = Double.parseDouble(pressureEd.getText().toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        // Call Next page
                        try {
                            if (pressureDes != 0 && pressureEd != null) {
                                height = Double.parseDouble(String.format("%.2f", altpress(pressure, pressureDes)));
                                Log.v("pressureDes", pressureDes.toString());
                                Log.v("Height", String.valueOf(height));
                                passingValueAndCallNextPage();
                            } else {
                                showErrorMessage("Enter destination pressure");
                            }
                        } catch (Exception e) {
                            showErrorMessage("An error occured, please try again later.");

                        }
                    }
                });

        // Look for pressure sensor
        SensorManager snsMgr = (SensorManager) getSystemService(Service.SENSOR_SERVICE);
        Sensor pS = snsMgr.getDefaultSensor(Sensor.TYPE_PRESSURE);
        snsMgr.registerListener(this, pS, SensorManager.SENSOR_DELAY_UI);


    }



    @Override
    protected void onResume() {

        super.onResume();
        SensorManager snsMgr = (SensorManager) getSystemService(Service.SENSOR_SERVICE);
        Sensor pS = snsMgr.getDefaultSensor(Sensor.TYPE_PRESSURE);
        snsMgr.registerListener(this, pS, SensorManager.SENSOR_DELAY_UI);
    }



    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {


    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        pressView.setText("" + values[0]);
        if(num==0){
            pressure = (double)values[0];
            num=1;
            Log.v("Pressure", pressure.toString());
        }

    }
    private double altpress(double pressure, double pressureDes){
        double feetTometer = 0.3048;
        double pstd = 1013.25;
        double altpress;
        double altpressDes;

        altpress =  (1 - Math.pow((pressure/pstd), 0.190284)) * 145366.45*feetTometer;
        altpressDes =  (1 - Math.pow((pressureDes/pstd), 0.190284)) * 145366.45*feetTometer;
        return Math.abs(altpress-altpressDes);
    }

    public void showErrorMessage(CharSequence text){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void passingValueAndCallNextPage(){
        //Passing value from Distance.java
        double distance = Double.parseDouble(getIntent().getStringExtra("distanceValue"));
        double latitudeValCurrent = Double.parseDouble(getIntent().getStringExtra("latitudeValCurrent"));
        double longitudeValCurrent = Double.parseDouble(getIntent().getStringExtra("longitudeValCurrent"));
        double latitudeValDes= Double.parseDouble(getIntent().getStringExtra("latitudeValDes"));
        double longitudeValDes = Double.parseDouble(getIntent().getStringExtra("longitudeValDes"));
        Intent intent = new Intent(Barometer.this, Calibrate.class);
        //String eiei = "test";
        intent.putExtra("distanceVal",String.valueOf(distance));
        intent.putExtra("height",String.valueOf(height));
        intent.putExtra("latitudeValCurrent", String.valueOf(latitudeValCurrent));
        intent.putExtra("longitudeValCurrent", String.valueOf(longitudeValCurrent));
        intent.putExtra("latitudeValDes", String.valueOf(latitudeValDes));
        intent.putExtra("longitudeValDes", String.valueOf(longitudeValDes));
        startActivity(intent);


        //////////// degub
        Log.v("Distance2 : m ", String.valueOf(distance));
        Log.v("latitudeValCurrent2", String.valueOf(latitudeValCurrent));
        Log.v("longitudeValCurrent2", String.valueOf(longitudeValCurrent));
        Log.v("latitudeValDes2", String.valueOf(latitudeValDes));
        Log.v("longitudeValDes2", String.valueOf(longitudeValDes));
    }

}