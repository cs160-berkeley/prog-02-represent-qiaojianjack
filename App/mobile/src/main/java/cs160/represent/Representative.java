package cs160.represent;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.google.android.gms.wearable.Asset;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jianqiao on 2/27/16.
 */
public class Representative {

    public static HashMap<String, Representative> repMap;
    public static String location;
    protected String repId;
    protected String title;
    protected String firstName, lastName;
    protected String party;
    protected int photoId;
    protected String email, website;
    protected String tweetHandle, tweet;
    protected String termEndDate;
    protected ArrayList<String> committees;
    protected ArrayList<Map.Entry<String, String>> recentBills;
    protected ArrayList<String> billList;
    protected String imgUrl;
    protected android.graphics.Bitmap photoBitmap;


    public Representative() {

    }

    public void pipeToIntent(Intent intent) {
        Bundle bundle = new Bundle();
        bundle.putString("repId", repId);
        bundle.putString("title", title);
        bundle.putString("firstName", firstName);
        bundle.putString("lastName", lastName);
        bundle.putString("party", party);
        bundle.putString("email", email);
        bundle.putString("website", website);
        bundle.putInt("photoId", photoId);
        bundle.putString("termEndDate", termEndDate);
        bundle.putStringArrayList("committees", committees);
        ArrayList<String> billList = new ArrayList<>();
        for (int i = 0; i < recentBills.size(); i++) {
            Map.Entry<String, String> tuple = recentBills.get(i);
            billList.add(tuple.getKey() + "\n" + tuple.getValue());
        }
        bundle.putStringArrayList("recentBills", billList);
        intent.putExtras(bundle);
    }

    public Bundle getBundleForWatch() {
        Bundle bundle = new Bundle();
        bundle.putString("repId", repId);
        if (title.equals("Senator")) {
            bundle.putString("title", "Sen.");
        } else {
            bundle.putString("title", "Rep.");
        }
        bundle.putString("firstName", firstName);
        bundle.putString("lastName", lastName);
        bundle.putString("party", party);
        return bundle;
    }


}
