package cs160.represent;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;

/**
 * Created by joleary and noon on 2/19/16 at very late in the night. (early in the morning?)
 */
public class PhoneListenerService extends WearableListenerService {

//   WearableListenerServices don't need an iBinder or an onStartCommand: they just need an onMessageReceieved.
    private final String LOAD_INFO = "load_info";
    private final String RANDOM_LOCATION = "random_location";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("PhoneListener", "in PhoneListenerService, got: " + messageEvent.getPath());
        if( messageEvent.getPath().equalsIgnoreCase(LOAD_INFO) ) {
            String repIdString  = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            Log.d("PhoneListener", "received data: " + messageEvent.getPath());
            Log.d("PhoneListener", "received data: " + repIdString);

            int repId = Integer.valueOf(repIdString);
            Intent toRepInfo = new Intent(this, RepInfo.class);
            toRepInfo.putExtra("repId", repId);
            toRepInfo.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(toRepInfo);

        } else if (messageEvent.getPath().equalsIgnoreCase(RANDOM_LOCATION)) {
            String zipcode = EntryActivity.randomizeLocation();
            Intent toRepList = new Intent(this, RepList.class);
            toRepList.putExtra("zipcode", zipcode);
            toRepList.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(toRepList);
        } else {
            super.onMessageReceived( messageEvent );
        }

    }
}
