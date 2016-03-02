package cs160.represent;

import android.content.Intent;
import android.os.Bundle;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jianqiao on 2/27/16.
 */
public class Representative {

    public static HashMap<Integer, Representative> repMap;
    protected int repId;
    protected String title;
    protected String firstName, lastName;
    protected String party;
    protected int photoId;
    protected String email, website;
    protected String tweetHandle, tweet;
    protected String termEndDate;
    protected ArrayList<String> committees;
    protected ArrayList<String> recentBills;

    public Representative() {

    }

    public void pipeToIntent(Intent intent) {
        Bundle bundle = new Bundle();
        bundle.putInt("repId", repId);
        bundle.putString("title", title);
        bundle.putString("firstName", firstName);
        bundle.putString("lastName", lastName);
        bundle.putString("party", party);
        bundle.putString("email", email);
        bundle.putString("website", website);
        bundle.putInt("photoId", photoId);
        bundle.putString("termEndDate", termEndDate);
        bundle.putStringArrayList("committees", committees);
        bundle.putStringArrayList("recentBills", recentBills);
        intent.putExtras(bundle);
    }

    public Bundle getBundleForWatch() {
        Bundle bundle = new Bundle();
        bundle.putInt("repId", repId);
        if (title.equals("Senator")) {
            bundle.putString("title", "Sen.");
        } else {
            bundle.putString("title", "Rep.");
        }
        bundle.putString("firstName", firstName);
        bundle.putString("lastName", lastName);
        bundle.putString("party", party);
        bundle.putInt("photoId", photoId);
        return bundle;
    }

}
