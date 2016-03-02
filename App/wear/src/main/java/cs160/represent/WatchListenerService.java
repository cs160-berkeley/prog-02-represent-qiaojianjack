package cs160.represent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class WatchListenerService extends WearableListenerService implements
        DataApi.DataListener {

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d("Watch Listener", "=================== Data Changed ==================== ");

        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();

        ConnectionResult connectionResult =
                mGoogleApiClient.blockingConnect(30, TimeUnit.SECONDS);

        if (!connectionResult.isSuccess()) {
            Log.e("WatchListenerService", "Failed to connect to GoogleApiClient.");
            return;
        }

        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                DataItem item = event.getDataItem();
                Log.d("WatchListener", "Got data");
                if (item.getUri().getPath().equals("/reps")) {
                    //get datamap, convert to intent
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    Intent toWatchEntry = new Intent(this, WearEntryActivity.class);

                    toWatchEntry.putExtras(dataMap.toBundle());
                    toWatchEntry.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(toWatchEntry);
                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {

            }
        }
    }


}