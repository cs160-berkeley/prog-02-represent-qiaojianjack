package cs160.represent;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class RepListViewHolder extends RecyclerView.ViewHolder {
    protected TextView vTitle;
    protected TextView vFirstName, vLastName;
    protected ImageView vParty;
    protected ImageView vPhoto;
    protected TextView vEmail, vWebsite;
    protected TextView vTweetHandle, vTweet;
    protected Button moreInfo;


    public RepListViewHolder(View v) {
        super(v);
        Typeface sansSerif = Typeface.createFromAsset(v.getContext().getAssets(), "fonts/SourceSansPro-Regular.ttf");
        Typeface sansSerifLight = Typeface.createFromAsset(v.getContext().getAssets(), "fonts/SourceSansPro-Light.ttf");
        Typeface sansSerifIt = Typeface.createFromAsset(v.getContext().getAssets(), "fonts/SourceSansPro-It.ttf");
        Typeface serifBold = Typeface.createFromAsset(v.getContext().getAssets(), "fonts/Merriweather-Bold.ttf");
        Typeface serifIt = Typeface.createFromAsset(v.getContext().getAssets(), "fonts/Merriweather-Italic.ttf");

        vTitle = (TextView) v.findViewById(R.id.title);
        vFirstName = (TextView) v.findViewById(R.id.first_name);
        vLastName = (TextView) v.findViewById(R.id.last_name);
        vParty = (ImageView) v.findViewById(R.id.party);
        vPhoto = (ImageView) v.findViewById(R.id.photo);
        vEmail = (TextView) v.findViewById(R.id.email);
        vWebsite = (TextView) v.findViewById(R.id.website);
        vTweetHandle = (TextView) v.findViewById(R.id.twitter_account);
        vTweet = (TextView) v.findViewById(R.id.twitter_content);

        moreInfo = (Button) v.findViewById(R.id.more_info);

        vTitle.setTypeface(serifIt);
        vFirstName.setTypeface(serifBold);
        vLastName.setTypeface(serifBold);
        vEmail.setTypeface(sansSerifIt);
        vWebsite.setTypeface(sansSerifIt);
        vTweetHandle.setTypeface(sansSerif);
        vTweet.setTypeface(sansSerif);
        moreInfo.setTypeface(sansSerifLight);
    }
}
