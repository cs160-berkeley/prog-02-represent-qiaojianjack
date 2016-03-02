package cs160.represent;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WearEntryActivity extends SensorActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear_entry);
        final Intent fromRepList = getIntent();
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        final Typeface sansSerif = Typeface.createFromAsset(getAssets(), "fonts/SourceSansPro-Regular.ttf");
        final Typeface serif = Typeface.createFromAsset(getAssets(), "fonts/Merriweather-Regular.ttf");
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {

            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                Button curr_rep = (Button) stub.findViewById(R.id.curr_rep);
                Button election_result = (Button) stub.findViewById(R.id.election_result);
                TextView vLocation = (TextView) stub.findViewById(R.id.location_watch);

                String loc = "";
                if (fromRepList.getExtras() != null) {
                    loc = fromRepList.getExtras().getString("location");
                }
                final String location = loc;

                vLocation.setText(location);
                vLocation.setTypeface(sansSerif);
                election_result.setTypeface(serif);
                curr_rep.setTypeface(serif);

                curr_rep.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent toRepInfoWatch = new Intent(WearEntryActivity.this, RepInfoWatchActivity.class);
                        Log.d("WearEntryActivity", Integer.toString(fromRepList.getExtras().getInt("size")));
                        toRepInfoWatch.putExtras(fromRepList.getExtras());
                        toRepInfoWatch.putExtra("location", location);
                        WearEntryActivity.this.startActivity(toRepInfoWatch);
                    }
                });


                election_result.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent toElection = new Intent(WearEntryActivity.this, ElectionActivity.class);
                        toElection.putExtra("location", location);
                        WearEntryActivity.this.startActivity(toElection);
                    }
                });


            }
        });
    }
}
