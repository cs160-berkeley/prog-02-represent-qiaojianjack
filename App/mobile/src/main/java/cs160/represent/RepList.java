package cs160.represent;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

public class RepList extends Activity {

    private static final String LOG_TAG = "RepList";

    private ApiHandler apiHandler;

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "S4HGAxMgmEXpjZW9pDFGu8slS";
    private static final String TWITTER_SECRET = "Qm9WiUTfcEnIxytI0lJUlzu2hJJ27OjUiZxMpOoXSG1yNA08vv";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        TwitterApiClient twitterClient = TwitterCore.getInstance().getApiClient();

        apiHandler = new ApiHandler(getApplicationContext(), twitterClient);

        final Typeface sansSerif = Typeface.createFromAsset(getAssets(), "fonts/SourceSansPro-Regular.ttf");
        final Typeface serif = Typeface.createFromAsset(getAssets(), "fonts/Merriweather-Regular.ttf");

        Intent fromEntry = getIntent();
        final Resources res = getResources();

        Log.d(LOG_TAG, fromEntry.getExtras().toString());
        final Bundle params = fromEntry.getExtras();


        final LinearLayoutManager repListLayoutManager = new LinearLayoutManager(this);

        Log.d(LOG_TAG, "Get location!");
        apiHandler.getLocation(params, new RepCallback() {
            @Override
            public void onGetLocationSuccess(String locationString) {
                Representative.location = locationString;
                apiHandler.getRepList(params, new RepCallback() {
                    @Override
                    public void onGetLocationSuccess(String locationString) {

                    }

                    @Override
                    public void onGetRepListSuccess() {
                        Log.d(LOG_TAG, "Replist populated!");

                        //get replist
                        ArrayList<Representative> reps = new ArrayList<Representative>(Representative.repMap.values());

                        apiHandler.getLocation(params, new RepCallback() {
                            @Override
                            public void onGetLocationSuccess(String locationString) {
                                Representative.location = locationString;
                            }

                            @Override
                            public void onGetRepListSuccess() {

                            }
                        });

                        Intent toWatch = new Intent(getBaseContext(), PhoneToWatchService.class);

                        Bundle repBundle = new Bundle();
                        repBundle.putInt("size", reps.size());
                        for (int i = 0; i < reps.size(); i++) {
                            repBundle.putBundle("bundle" + Integer.toString(i), reps.get(i).getBundleForWatch());
                        }
                        repBundle.putBundle("electionData", apiHandler.getElectionData(Representative.location));

                        toWatch.putExtras(repBundle);
                        toWatch.putExtra("location", Representative.location);
                        startService(toWatch);



                        setContentView(R.layout.activity_rep_list);
                        final TextView location = (TextView) findViewById(R.id.location);
                        TextView pageTitle = (TextView) findViewById(R.id.rep_list_page_title);
                        location.setTypeface(sansSerif);
                        pageTitle.setTypeface(serif);


                        //start to set view
                        location.setText(Representative.location);
                        String locationString = location.getText().toString();

                        RecyclerView repList = (RecyclerView) findViewById(R.id.reps);

                        // use this setting to improve performance if you know that changes
                        // in content do not change the layout size of the RecyclerView
                        repList.setHasFixedSize(true);

                        // use a linear layout manager
                        repListLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        repList.setLayoutManager(repListLayoutManager);

                        // specify an adapter (see also next example)
                        repList.setAdapter(new RepListAdapter(reps, locationString, getApplicationContext()));


                        // Send data to watch

                    }
                });

            }

            @Override
            public void onGetRepListSuccess() {

            }
        });


        /*
        String locationString = location.getText().toString();

        //TODO: get replist
        ArrayList<Representative> reps = new ArrayList<Representative>(repMap.values());

        Intent toWatch = new Intent(getBaseContext(), PhoneToWatchService.class);
        Bundle repBundle = new Bundle();
        repBundle.putInt("size", reps.size());
        for (int i = 0; i < reps.size(); i++) {
            repBundle.putBundle("bundle" + Integer.toString(i), reps.get(i).getBundleForWatch());
        }
        toWatch.putExtras(repBundle);
        //TODO: change how location is sent
        toWatch.putExtra("location", locationString);
        startService(toWatch);

        RecyclerView repList = (RecyclerView) findViewById(R.id.reps);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        repList.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager repListLayoutManager = new LinearLayoutManager(this);
        repListLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        repList.setLayoutManager(repListLayoutManager);

        // specify an adapter (see also next example)
        repList.setAdapter(new RepListAdapter(reps, location.getText().toString(), getApplicationContext()));
        */

    }





    /**
     * Right now, only give fake reps.
     */

    protected void getFakeReps(String zipcode) {
        if (zipcode.equals("94720")) {
            Representative dianne = new Representative();
            dianne.repId = "1";
            dianne.firstName = "Dianne";
            dianne.lastName = "Feinstein";
            dianne.title = "Senator";
            dianne.party = "Democratic";
            dianne.email = "sampleemail@us.gov";
            dianne.website = "http://www.feinstein.senate.gov/public/";
            dianne.photoId = R.drawable.dianne;
            dianne.tweetHandle = "@somebody";
            dianne.tweet = "Today we reflect on the contributions and leadership of U.S. presidents throughout our nation’s great history. Happy #PresidentsDay.";
            dianne.termEndDate = "01/03/2016";
            dianne.committees = new ArrayList<String>(Arrays.asList("Senate Select Committee on Intelligence",
                    "Senate Committee on Appropriations"));
            dianne.recentBills =  new ArrayList<Map.Entry<String,String>>(Arrays.asList(
                    new AbstractMap.SimpleEntry<String, String>("02/11/2016",
                    "S. 2552: A bill to amend section 875(c) of title 18, United States Code, to ..."),
                    new AbstractMap.SimpleEntry<String, String>("02/10/2016",
                            "S. 2533: A bill to provide short-term water supplies to drought-stricken California ..."),
                    new AbstractMap.SimpleEntry<String, String>("05/18/2016",
                            "S. 2678: A bill to provide short-term water supplies to drought-stricken California ...")
            ));

            Representative jeff = new Representative();
            jeff.repId = "2";
            jeff.firstName = "Jeff";
            jeff.lastName = "Dunhan";
            jeff.title = "Representative";
            jeff.party = "Republican";
            jeff.email = "sampleemail@us.gov";
            jeff.website = "http://dunhan.house.gov";
            jeff.photoId = R.drawable.jeff;
            jeff.tweetHandle = "@somebody2";
            jeff.tweet = "Today we reflect on the contributions and leadership of U.S. presidents throughout our nation’s great history. Happy #PresidentsDay.";
            jeff.termEndDate = "02/08/2016";
            jeff.committees = new ArrayList<String>(Arrays.asList("Senate Select Committee on Intelligence",
                    "Senate Committee on Appropriations"));
            jeff.recentBills = new ArrayList<Map.Entry<String,String>>(Arrays.asList(
                    new AbstractMap.SimpleEntry<String, String>("02/11/2016",
                    "S. 2552: A bill to amend section 875(c) of title 18, United States Code, to ..."),
                    new AbstractMap.SimpleEntry<String, String>("02/10/2016",
                            "S. 2533: A bill to provide short-term water supplies to drought-stricken California ..."),
                    new AbstractMap.SimpleEntry<String, String>("05/18/2016",
                            "S. 2678: A bill to provide short-term water supplies to drought-stricken California ...")
            ));

            Representative.repMap = new HashMap<String, Representative>();
            Representative.repMap.put(dianne.repId, dianne);
            Representative.repMap.put(jeff.repId, jeff);
        } else {
            Representative jack = new Representative();
            jack.repId = "3";
            jack.firstName = "Jack";
            jack.lastName = "Sparrow";
            jack.title = "Senator";
            jack.party = "Independent";
            jack.email = "sampleemail3@us.gov";
            jack.website = "http://samplewebsite3.test.gov";
            jack.photoId = R.drawable.jeff;
            jack.tweetHandle = "@somebody3";
            jack.tweet = "Today we reflect on the contributions and leadership of U.S. presidents throughout our nation’s great history. Happy #PresidentsDay.";
            jack.termEndDate = "03/15/2016";
            jack.committees = new ArrayList<String>(Arrays.asList("Senate Select Committee on Intelligence",
                    "Senate Committee on Sailing"));
            jack.recentBills = new ArrayList<Map.Entry<String,String>>(Arrays.asList(
                    new AbstractMap.SimpleEntry<String, String>("02/11/2016",
                    "S. 2552: A bill to amend section 875(c) of title 18, United States Code, to ..."),
                    new AbstractMap.SimpleEntry<String, String>("02/10/2016",
                            "S. 2533: A bill to provide short-term water supplies to drought-stricken California ..."),
                    new AbstractMap.SimpleEntry<String, String>("05/18/2016",
                            "S. 2678: A bill to provide short-term water supplies to drought-stricken California ...")
            ));

            Representative amy = new Representative();
            amy.repId = "4";
            amy.firstName = "Amy";
            amy.lastName = "NotSchumer";
            amy.title = "Representative";
            amy.party = "Republican";
            amy.email = "sampleemail4@us.gov";
            amy.website = "http://somewebsite.sample.gov";
            amy.photoId = R.drawable.dianne;
            amy.tweetHandle = "@somebody4";
            amy.tweet = "Today we reflect on the contributions and leadership of U.S. presidents throughout our nation’s great history. Happy #PresidentsDay.";
            amy.termEndDate = "04/01/2016";
            amy.committees = new ArrayList<String>(Arrays.asList("Senate Select Committee on Humorous Affairs"
                    ));
            amy.recentBills = new ArrayList<Map.Entry<String,String>>(Arrays.asList(
                    new AbstractMap.SimpleEntry<String, String>("02/11/2016",
                    "S. 2552: A bill to amend section 875(c) of title 18, United States Code, to ..."),
                    new AbstractMap.SimpleEntry<String, String>("02/10/2016",
                            "S. 2533: A bill to provide short-term water supplies to drought-stricken California ..."),
                    new AbstractMap.SimpleEntry<String, String>("05/18/2016",
                            "S. 2678: A bill to provide short-term water supplies to drought-stricken California ...")
            ));

            Representative.repMap = new HashMap<String, Representative>();
            Representative.repMap.put(jack.repId, jack) ;
            Representative.repMap.put(amy.repId, amy);
        }
    }

    public interface RepCallback {
        void onGetLocationSuccess(String locationString);
        void onGetRepListSuccess();
    }

}
