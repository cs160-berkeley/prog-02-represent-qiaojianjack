package cs160.represent;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.MotionEventCompat;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ElectionActivity extends SensorActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_election);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                Resources res = stub.getResources();
                PieChart pie = (PieChart) stub.findViewById(R.id.pie);
                Typeface sansSerif = Typeface.createFromAsset(getAssets(), "fonts/SourceSansPro-Regular.ttf");
                Typeface serif = Typeface.createFromAsset(getAssets(), "fonts/Merriweather-Regular.ttf");


                TextView vLocation = (TextView) findViewById(R.id.location_watch);
                TextView vObama = (TextView) findViewById(R.id.obama_name);
                TextView vRomney = (TextView) findViewById(R.id.romney_name);
                TextView vObamaPercent = (TextView) findViewById(R.id.obama_percent);
                TextView vRomneyPercent = (TextView) findViewById(R.id.romney_percent);

                vLocation.setTypeface(sansSerif);
                vObama.setTypeface(serif);
                vRomney.setTypeface(serif);
                vObamaPercent.setTypeface(sansSerif);
                vRomneyPercent.setTypeface(sansSerif);

                Intent fromWatchEntry = getIntent();
                String locString = fromWatchEntry.getStringExtra("location");
                vLocation.setText(locString);

                String obamaPercent = fromWatchEntry.getStringExtra("obama");
                String romneyPercent = fromWatchEntry.getStringExtra("romney");

                /*
                //TODO: correctly set percentage
                if (locString.equalsIgnoreCase("Alameda, CA")) {
                    obamaPercent = 60f;
                    romneyPercent = 40f;
                } else if (locString.equalsIgnoreCase("Marin, CA")) {
                    obamaPercent = 73.8f;
                    romneyPercent = 26.2f;
                }
                */

                vObamaPercent.setText(obamaPercent+"%");
                vRomneyPercent.setText(romneyPercent+"%");

                pie.addItem("Obama", Float.valueOf(obamaPercent), res.getColor(R.color.background));
                pie.addItem("Romney", Float.valueOf(romneyPercent), res.getColor(R.color.red));
            }

        });

    }

}
