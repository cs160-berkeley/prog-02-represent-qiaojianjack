package cs160.represent;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by jianqiao on 2/27/16.
 */
public class RepListAdapter extends RecyclerView.Adapter<RepListViewHolder> {

    private List<Representative> repList;
    private Context context;
    private String location;

    public RepListAdapter(List<Representative> repList, String location, Context context) {
        this.repList = repList;
        this.context = context;
        this.location = location;
    }

    @Override
    public int getItemCount() {
        return repList.size();
    }

    @Override
    public void onBindViewHolder(RepListViewHolder repListViewHolder, int i) {
        final Representative rep = repList.get(i);
        repListViewHolder.vFirstName.setText(rep.firstName);
        repListViewHolder.vLastName.setText(rep.lastName);
        repListViewHolder.vEmail.setText(rep.email);
        repListViewHolder.vWebsite.setText(rep.website);
        repListViewHolder.vTitle.setText(rep.title);
        if (rep.party.equals("Democratic")) {
            repListViewHolder.vParty.setImageResource(R.drawable.dparty);
        } else if (rep.party.equals("Republican")) {
            repListViewHolder.vParty.setImageResource(R.drawable.rparty);
        } else {
            repListViewHolder.vParty.setImageResource(R.drawable.iparty);
        }
        Picasso.with(context).load(rep.imgUrl).into(repListViewHolder.vPhoto);
        //repListViewHolder.vPhoto.setImageResource(rep.photoId);
        if (rep.tweetHandle.equals("@null")) {
            repListViewHolder.vTweetHandle.setText("");
            repListViewHolder.vTweet.setText("No twitter data has found for this representative.");
            repListViewHolder.vTweet.setTextColor(context.getResources().getColor(R.color.grey));
        } else {
            repListViewHolder.vTweetHandle.setText(rep.tweetHandle);
            repListViewHolder.vTweet.setText(rep.tweet);
        }
        repListViewHolder.moreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toRepInfoView = new Intent(v.getContext(), RepInfo.class);
                toRepInfoView.putExtra("repId", rep.repId);
                toRepInfoView.putExtra("location", location);
                v.getContext().startActivity(toRepInfoView);
            }
        });

    }

    @Override
    public RepListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.rep_list_card, viewGroup, false);

        return new RepListViewHolder(itemView);
    }
}
