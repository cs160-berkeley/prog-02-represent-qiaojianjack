package cs160.represent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.wearable.view.CircledImageView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;


public class RepFragment extends Fragment {

    protected TextView vTitle;
    protected TextView vName;
    protected ImageView vParty;
    protected cs160.represent.CircledImageView vPhoto;
    protected Bundle rep;

    public RepFragment() {
        // Required empty public constructor
    }

    public static RepFragment newInstance(Bundle rep) {
        RepFragment fragment = new RepFragment();
        fragment.setArguments(rep);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rep = this.getArguments();
        if (rep != null) {
            Log.d("RepFragment OnCreate", rep.toString());
        } else {
            Log.d("RepFragment OnCreate", "NO REP");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("RepFragment", "onCreateView");
        View v = inflater.inflate(R.layout.fragment_rep, container, false);
        Typeface sansSerifLight = Typeface.createFromAsset(v.getContext().getAssets(), "fonts/SourceSansPro-Light.ttf");
        Typeface serifIt = Typeface.createFromAsset(v.getContext().getAssets(), "fonts/Merriweather-Italic.ttf");
        Typeface serif = Typeface.createFromAsset(v.getContext().getAssets(), "fonts/Merriweather-Regular.ttf");

        vTitle = (TextView) v.findViewById(R.id.title_watch);
        vName = (TextView) v.findViewById(R.id.name_watch);
        vParty = (ImageView) v.findViewById(R.id.party_watch);
        vPhoto = (cs160.represent.CircledImageView) v.findViewById(R.id.photo_watch);

        vTitle.setTypeface(serifIt);
        vName.setTypeface(serif);

        if (rep != null){
            setBundle(rep);
        }

        Button vButton = (Button) v.findViewById(R.id.rep_to_phone);
        vButton.setTypeface(sansSerifLight);

        vButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toPhone = new Intent(RepFragment.this.getActivity(), WatchToPhoneService.class);
                toPhone.putExtra("command", "load_info");
                toPhone.putExtra("rep_id", rep.getString("repId"));
                getActivity().startService(toPhone);

            }
        });

        return v;

    }


    public void setBundle(Bundle rep) {
        vTitle.setText(rep.getString("title"));
        vName.setText(rep.getString("firstName") + ' ' + rep.getString("lastName"));

        if (rep.getString("party").equals("Democratic")) {
            vParty.setImageResource(R.drawable.dparty);
        } else if (rep.getString("party").equals("Republican")) {
            vParty.setImageResource(R.drawable.rparty);
        } else {
            vParty.setImageResource(R.drawable.iparty);
        }

        //TODO: fix photo choose pipeline
        String repId = rep.getString("repId");
        vPhoto.setImageBitmap(WearEntryActivity.photoMap.get(repId));
    }

}
