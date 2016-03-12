package cs160.represent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * Created by joleary and noon on 2/19/16 at very late in the night. (early in the morning?)
 */
public class PhoneListenerService extends WearableListenerService {

//   WearableListenerServices don't need an iBinder or an onStartCommand: they just need an onMessageReceieved.
    private final static String LOG_TAG = "PhoneListenerService";
    private final String LOAD_INFO = "load_info";
    private final String RANDOM_LOCATION = "random_location";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("PhoneListener", "in PhoneListenerService, got: " + messageEvent.getPath());
        if( messageEvent.getPath().equalsIgnoreCase(LOAD_INFO) ) {
            String repId = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            Log.d("PhoneListener", "received data: " + messageEvent.getPath());
            Log.d("PhoneListener", "received data: " + repId);

            Intent toRepInfo = new Intent(this, RepInfo.class);
            toRepInfo.putExtra("repId", repId);
            toRepInfo.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(toRepInfo);

        } else if (messageEvent.getPath().equalsIgnoreCase(RANDOM_LOCATION)) {
            randomizeLocation();
        } else {
            super.onMessageReceived( messageEvent );
        }

    }


    public void randomizeLocation() {
        if (ApiHandler.electionJson == null) {
            String json = null;
            try {
                InputStream is = this.getAssets().open("election-county-2012.json");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                json = new String(buffer, "UTF-8");
                ApiHandler.electionJson = new JSONArray(json);
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Random rand = new Random();

        int randomInt = rand.nextInt(ApiHandler.electionJson.length());

        try {
            JSONObject electionData = ApiHandler.electionJson.getJSONObject(randomInt);
            String county = electionData.getString("county-name");
            String state = electionData.getString("state-postal");

            ApiHandler apiHandler = new ApiHandler(this, null);
            apiHandler.getLagLongFromCounty(county, state, new RandomLocationCallback() {
                @Override
                public void callback(double lat, double lon) {
                    startRepList(lat, lon);
                }
            });
        } catch (JSONException e) {
            Log.d(LOG_TAG, "randomizeLocation: " + e.getMessage());
        }

    }


    public void startRepList(double lat, double lon) {
        Intent toRepList = new Intent(this, RepList.class);
        Bundle bundle = new Bundle();
        bundle.putString("location_type", this.getResources().getString(R.string.location_type_latlong));
        bundle.putDouble("latitude", lat);
        bundle.putDouble("longitude", lon);
        toRepList.putExtras(bundle);
        toRepList.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(toRepList);
    }

    public interface RandomLocationCallback {
        void callback(double lat, double lon);
    }

}
