package cs160.represent;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ShakeActivity extends Activity {

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake);
        Typeface sansSerif = Typeface.createFromAsset(getAssets(), "fonts/SourceSansPro-Regular.ttf");
        TextView vText = (TextView) findViewById(R.id.location_random_watch);
        vText.setTypeface(sansSerif);



        final ProgressBar bar = (ProgressBar) findViewById(R.id.progress_bar);
        //TODO: make progress bar working
        Thread delayThread = new Thread() {
            public void run() {
                bar.setProgress(30);
                try {
                    Thread.sleep(1000);
                } catch(InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                bar.setProgress(50);
                try {
                    Thread.sleep(1000);
                } catch(InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                bar.setProgress(100);
                Intent shakeToPhone = new Intent(ShakeActivity.this, WatchToPhoneService.class);
                shakeToPhone.putExtra("command", "random_location");
                startService(shakeToPhone);
            }
        };
        delayThread.start();

    }
}
