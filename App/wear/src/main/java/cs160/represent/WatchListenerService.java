package cs160.represent;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class WatchListenerService extends WearableListenerService implements
        DataApi.DataListener {

    private static final String LOG_TAG = "WatchListenerService";
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d("Watch Listener", "=================== Data Changed ==================== ");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
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

                if (item.getUri().getPath().equals("/images")) {
                    Log.d(LOG_TAG, "got images");
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(item);
                    unpackPhoto(dataMapItem.getDataMap());
                } else if (item.getUri().getPath().equals("/reps")) {
                    Log.d(LOG_TAG, "got reps");
                    //get datamap, convert to intent
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();

                    Intent toWatchEntry = new Intent(this, WearEntryActivity.class);

                    toWatchEntry.putExtras(dataMap.toBundle());
                    toWatchEntry.putExtras(dataMap.toBundle());
                    toWatchEntry.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    startActivity(toWatchEntry);
                }

            } else if (event.getType() == DataEvent.TYPE_DELETED) {

            }
        }
    }

    private void unpackPhoto(DataMap dataMap) {
        int size = dataMap.getInt("size");
        for (int i = 0; i < size; i++) {
            String repId = dataMap.getString("repId|" + Integer.toString(i));
            if (repId == null) {
                Log.d(LOG_TAG, "photoAsset is null for: " + Integer.toString(i));
            }

            Asset photoAsset = dataMap.getAsset("photo|" + repId);
            if (photoAsset == null) {
                Log.d(LOG_TAG, "photoAsset is null for: " + repId);
            }

            Bitmap photoBitmap = loadBitmapFromAsset(photoAsset);

            WearEntryActivity.photoMap.put(repId, photoBitmap);
        }
    }

    private Bitmap loadBitmapFromAsset(Asset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }
        ConnectionResult result =
                mGoogleApiClient.blockingConnect(30, TimeUnit.MILLISECONDS);
        if (!result.isSuccess()) {
            return null;
        }
        // convert asset into a file descriptor and block until it's ready
        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
                mGoogleApiClient, asset).await().getInputStream();
        mGoogleApiClient.disconnect();

        if (assetInputStream == null) {
            Log.w(LOG_TAG, "Requested an unknown Asset.");
            return null;
        }
        // decode the stream into a bitmap
        return BitmapFactory.decodeStream(assetInputStream);
    }

}