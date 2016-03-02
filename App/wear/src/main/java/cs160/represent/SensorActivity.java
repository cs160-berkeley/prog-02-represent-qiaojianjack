package cs160.represent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

public class SensorActivity extends Activity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private static int ACCELEROMETER_THRESHOLD = 1500;

    private float xAccelPrev, yAccelPrev, zAccelPrev;
    private float xAccel, yAccel, zAccel;
    private boolean started = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public final void onSensorChanged(SensorEvent event)
    {
        // Use values from event.values array
        /*
        final float alpha = 0.8f;
        float[] gravity = "9.81, 9.81, 9.81";

        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        linearAcceleration[0] = event.values[0] - gravity[0];
        linearAcceleration[1] = event.values[1] - gravity[1];
        linearAcceleration[2] = event.values[2] - gravity[2];
       */
        float[] linearAcceleration = new float[3];
        linearAcceleration[0] = event.values[0];
        linearAcceleration[1] = event.values[1];
        linearAcceleration[2] = event.values[2];

        if (!started) {
            xAccelPrev = event.values[0];
            yAccelPrev = event.values[1];
            zAccelPrev = event.values[2];
            started = true;
        } else {
            xAccelPrev = xAccel;
            yAccelPrev = yAccel;
            zAccelPrev = zAccel;
        }
        xAccel = event.values[0];
        yAccel = event.values[1];
        zAccel = event.values[2];


        Log.d("SensorActivity", Float.toString(event.values[0]));
        Log.d("SensorActivity", Float.toString(event.values[1]));
        Log.d("SensorActivity", Float.toString(event.values[2]));

        if (Math.abs(xAccel - xAccelPrev) >= ACCELEROMETER_THRESHOLD ||
             Math.abs(yAccel - yAccelPrev) >= ACCELEROMETER_THRESHOLD||
                Math.abs(zAccel - zAccelPrev) >= ACCELEROMETER_THRESHOLD) {
            Intent toShake = new Intent(this, ShakeActivity.class);
            startActivity(toShake);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
