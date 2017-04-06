package com.example.chuti.longrange;

/**
 * Created by chuti on 3/27/2017.
 */
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.widget.Toast;


import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Timer;
import java.util.TimerTask;

public class Rotation extends Activity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mRotationSensor;
    private static final int SENSOR_DELAY = 500 * 1000; // 500ms
    private static final int FROM_RADS_TO_DEGS = -57;
    private double distance2;
    private Button mButton;
    private float pitch;
    private float roll;
    private float azimuth;
    private int pitchSetZero;
    private float setZero = 0;
    private TextView textView;
    private double distance;
    private double height;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rotation_page);
        mButton = (Button)findViewById(R.id.buttonTest);
        textView = (TextView) findViewById(R.id.TextView01);

        receiveValuefromBarometer();

        //run readWebpage every seconds
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override public void run() {
                readWebpage();

            }
        }, 0, 1000);

        // read Sensor
        try {
            mSensorManager = (SensorManager) getSystemService(Activity.SENSOR_SERVICE);
            mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            mSensorManager.registerListener(this, mRotationSensor, SENSOR_DELAY);
        } catch (Exception e) {
            Toast.makeText(this, "Hardware compatibility issue", Toast.LENGTH_LONG).show();
        }

        mButton.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {

                        try{
                            setZero = - pitch;
                            startActivity(new Intent(Rotation.this, Test.class));
                        }
                        catch(Exception e){
                           showErrorMessage("Error");
                        }
                    }
                });
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == mRotationSensor) {
            if (event.values.length > 4) {
                float[] truncatedRotationVector = new float[4];
                System.arraycopy(event.values, 0, truncatedRotationVector, 0, 4);
                update(truncatedRotationVector);
            } else {
                update(event.values);

            }
        }
    }

    private void update(float[] vectors) {
        float[] rotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(rotationMatrix, vectors);
        int worldAxisX = SensorManager.AXIS_X;
        int worldAxisZ = SensorManager.AXIS_Z;
        float[] adjustedRotationMatrix = new float[9];
        SensorManager.remapCoordinateSystem(rotationMatrix, worldAxisX, worldAxisZ, adjustedRotationMatrix);
        float[] orientation = new float[3];
        SensorManager.getOrientation(adjustedRotationMatrix, orientation);
        pitch = orientation[1] * FROM_RADS_TO_DEGS-2+90; // -2= calibate
        pitchSetZero = (int)Math.ceil(pitch+setZero);
        roll = orientation[2] * FROM_RADS_TO_DEGS;
        azimuth = orientation[0] * FROM_RADS_TO_DEGS;
        ((TextView)findViewById(R.id.pitch)).setText("Angle: " + pitchSetZero);
        //((TextView)findViewById(R.id.roll)).setText("Roll: "+roll+ "  Azimuth"+azimuth);


    }

    // Load Web page
    private class DownloadWebPageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            for (String url : urls) {

                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);
                httpGet.setHeader("Cookie", "AIROS_SESSIONID=2ca526d0b45c06278ef40cbf9520ea56; path=/; domain=192.168.1.20; Expires=Tue Jan 19 2038 03:14:07 GMT+0700 (SE Asia Standard Time);");
                try {

                    HttpResponse execute = client.execute(httpGet);
                    InputStream content = execute.getEntity().getContent();

                    BufferedReader buffer = new BufferedReader(
                            new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {

            try {
                JSONObject jsonObj = new JSONObject(Html.fromHtml(result).toString());
                System.out.println(jsonObj.getJSONObject("wireless").get("signal"));
                //System.out.println(jsonObj.getJSONObject("wireless").get("rssi"));
                //System.out.println(jsonObj.getJSONObject("wireless").get("noisef"));
                String signal = jsonObj.getJSONObject("wireless").get("signal").toString();
                String rssi = jsonObj.getJSONObject("wireless").get("rssi").toString();

                textView.setText("Signal : "+signal +" dBm");
            } catch (JSONException e) {
                e.printStackTrace();
                textView.setText("Can't connecting to server \nPlease try again later");
            }
        }
    }

    public void readWebpage () {
        DownloadWebPageTask task = new DownloadWebPageTask();
        task.execute("http://192.168.1.20/status.cgi");

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
        Log.v("distance",String.valueOf(distance));
        Log.v("height", String.valueOf(height));
        calculateAngle();
    }

    public void calculateAngle(){
        double angle = Math.toDegrees(Math.atan2(height, distance));
        Log.v("Angle",String.valueOf(angle));
        ((TextView)findViewById(R.id.angle)).setText("มุมของจาน : " + String.format("%.1f",angle));
    }

}