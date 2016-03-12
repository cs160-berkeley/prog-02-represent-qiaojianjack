package cs160.represent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.ecommerce.Product;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Map;

public class RepInfo extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rep_info);
        Intent fromRepList = getIntent();
        Resources res = getResources();
        LayoutInflater inflater = (LayoutInflater)this.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);

        String locString = Representative.location;

        Typeface sansSerif = Typeface.createFromAsset(getAssets(), "fonts/SourceSansPro-Regular.ttf");
        Typeface sansSerifLight = Typeface.createFromAsset(getAssets(), "fonts/SourceSansPro-Light.ttf");
        Typeface sansSerifIt = Typeface.createFromAsset(getAssets(), "fonts/SourceSansPro-It.ttf");
        Typeface serifBold = Typeface.createFromAsset(getAssets(), "fonts/Merriweather-Bold.ttf");
        Typeface serifIt = Typeface.createFromAsset(getAssets(), "fonts/Merriweather-Italic.ttf");
        Typeface serif = Typeface.createFromAsset(getAssets(), "fonts/Merriweather-Regular.ttf");

        TextView location = (TextView) findViewById(R.id.location_rep_info);
        TextView pageTitle = (TextView) findViewById(R.id.rep_info_page_title);
        location.setTypeface(sansSerif);
        location.setText(locString);
        pageTitle.setTypeface(serif);

        TextView vTitle = (TextView) findViewById(R.id.title_rep_info);
        TextView vFirstName = (TextView) findViewById(R.id.first_name_rep_info);
        TextView vLastName = (TextView) findViewById(R.id.last_name_rep_info);
        ImageView vParty = (ImageView) findViewById(R.id.party_rep_info);
        ImageView vPhoto = (ImageView) findViewById(R.id.photo_rep_info);
        TextView vEmail = (TextView) findViewById(R.id.email_rep_info);
        TextView vWebsite = (TextView) findViewById(R.id.website_rep_info);
        TextView vTermEndDate = (TextView) findViewById(R.id.term_end_date);
        TextView vCommittees = (TextView) findViewById(R.id.committees);
        TextView vTermEndDateHeader = (TextView) findViewById(R.id.term_end_date_header);
        TextView vCommitteesHeader = (TextView) findViewById(R.id.committees_header);
        TextView vRecentBillsHeader = (TextView) findViewById(R.id.recent_bills_header);

        vTitle.setTypeface(serifIt);
        vFirstName.setTypeface(serifBold);
        vLastName.setTypeface(serifBold);
        vEmail.setTypeface(sansSerifIt);
        vWebsite.setTypeface(sansSerifIt);
        vTermEndDate.setTypeface(sansSerif);
        vCommittees.setTypeface(serif);
        vTermEndDateHeader.setTypeface(sansSerifLight);
        vCommitteesHeader.setTypeface(sansSerifLight);
        vRecentBillsHeader.setTypeface(sansSerifLight);

        Button back = (Button) findViewById(R.id.back);
        back.setTypeface(sansSerifLight);

        String repId = fromRepList.getStringExtra("repId");

        Representative rep = Representative.repMap.get(repId);

        vTitle.setText(rep.title);
        vFirstName.setText(rep.firstName);
        vLastName.setText(rep.lastName);
        String party = rep.party;
        if (party.equals("Democratic")) {
            vParty.setImageResource(R.drawable.dparty);
        } else if (party.equals("Republican")) {
            vParty.setImageResource(R.drawable.rparty);
        } else {
            vParty.setImageResource(R.drawable.iparty);
        }

        Picasso.with(this).load(rep.imgUrl).into(vPhoto);
        vEmail.setText(rep.email);
        vWebsite.setText(rep.website);
        vTermEndDate.setText(rep.termEndDate);
        if (rep.committees.size() == 0) {
            vCommittees.setText("None");
        } else {
            vCommittees.setText(TextUtils.join(";\n", rep.committees));
        }

        ArrayList<String> bills = new ArrayList<>();
        for (int i = 0; i < rep.recentBills.size(); i++) {
            Map.Entry<String, String> tuple = rep.recentBills.get(i);
            bills.add(tuple.getKey() + "\n" + tuple.getValue());
        }
        LinearLayout recentBills = (LinearLayout) findViewById(R.id.recent_bills);
        BillAdapter billAdapter = new BillAdapter(this, 0, bills);

        for (int i = 0; i < billAdapter.getCount(); i++) {
            View v = billAdapter.getView(i, null, recentBills);
            recentBills.addView(v);
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
