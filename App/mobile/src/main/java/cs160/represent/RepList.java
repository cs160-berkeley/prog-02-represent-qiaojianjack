package cs160.represent;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class RepList extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rep_list);
        Typeface sansSerif = Typeface.createFromAsset(getAssets(), "fonts/SourceSansPro-Regular.ttf");
        Typeface serif = Typeface.createFromAsset(getAssets(), "fonts/Merriweather-Regular.ttf");

        Intent fromEntry = getIntent();

        TextView location = (TextView) findViewById(R.id.location);
        TextView pageTitle = (TextView) findViewById(R.id.rep_list_page_title);
        location.setTypeface(sansSerif);
        pageTitle.setTypeface(serif);

        String zipcode = fromEntry.getStringExtra("zipcode");
        if (zipcode.equals("94709")) {
            location.setText("Marin, CA");
        }
        getReps(zipcode);

        ArrayList<Representative> reps = new ArrayList<Representative>(Representative.repMap.values());

        Intent toWatch = new Intent(getBaseContext(), PhoneToWatchService.class);
        Bundle repBundle = new Bundle();
        repBundle.putInt("size", reps.size());
        for (int i = 0; i < reps.size(); i++) {
            repBundle.putBundle("bundle" + Integer.toString(i), reps.get(i).getBundleForWatch());
        }
        toWatch.putExtras(repBundle);
        //TODO: change how location is sent
        toWatch.putExtra("location", location.getText());
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

    }


    /**
     * Right now, only give fake reps.
     */
    protected void getReps(String zipcode) {
        if (zipcode.equals("94720")) {
            Representative dianne = new Representative();
            dianne.repId = 1;
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
            dianne.recentBills = new ArrayList<String>(Arrays.asList(
                    "02/11/2016\nS. 2552: A bill to amend section 875(c) of title 18, United States Code, to ...",
                    "02/10/2016\nS. 2533: A bill to provide short-term water supplies to drought-stricken California ..."
            ));

            Representative jeff = new Representative();
            jeff.repId = 2;
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
            jeff.recentBills = new ArrayList<String>(Arrays.asList(
                    "02/11/2016\nS. 2552: A bill to amend section 875(c) of title 18, United States Code, to ...",
                    "02/10/2016\nS. 2533: A bill to provide short-term water supplies to drought-stricken California ..."
            ));

            Representative.repMap = new HashMap<Integer, Representative>();
            Representative.repMap.put(dianne.repId, dianne);
            Representative.repMap.put(jeff.repId, jeff);
        } else {
            Representative jack = new Representative();
            jack.repId = 3;
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
            jack.recentBills = new ArrayList<String>(Arrays.asList(
                    "02/11/2016\nS. 2552: A bill to amend section 875(c) of title 18, United States Code, to ...",
                    "02/10/2016\nS. 2533: A bill to provide short-term water supplies to drought-stricken California ..."
            ));

            Representative amy = new Representative();
            amy.repId = 4;
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
            amy.recentBills = new ArrayList<String>(Arrays.asList(
                    "02/11/2016\nS. 2552: A bill to amend section 875(c) of title 18, United States Code, to ...",
                    "02/10/2016\nS. 2533: A bill to provide short-term water supplies to drought-stricken California ...",
                    "05/18/2016\nS. 2678: A bill to provide short-term water supplies to drought-stricken California ..."
            ));

            Representative.repMap = new HashMap<Integer, Representative>();
            Representative.repMap.put(jack.repId, jack) ;
            Representative.repMap.put(amy.repId, amy);
        }
    }

}
