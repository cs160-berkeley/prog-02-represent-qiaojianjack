package cs160.represent;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by joleary on 2/19/16.
 */
public class PhoneToWatchService extends Service {

    private GoogleApiClient mApiClient;

    @Override
    public void onCreate() {
        Log.d("PhoneToWatch", "################################################ On Create");
        super.onCreate();
        //initialize the googleAPIClient for message passing
        mApiClient = new GoogleApiClient.Builder( this )
                .addApi( Wearable.API )
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                    }
                })
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mApiClient.disconnect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final Bundle reps = intent.getExtras();
        final String location = intent.getStringExtra("location");
        Log.d("PhoneToWatch", "started");

        new Thread(new Runnable() {
            @Override
            public void run() {
                //first, connect to the apiclient
                mApiClient.connect();

                PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/reps");
                PutDataRequest putDataRequest = putDataMapRequest.asPutDataRequest();

                DataMap data = DataMap.fromBundle(reps);
                data.putLong("time", new Date().getTime());
                data.putString("location", location);

                putDataRequest.setData(data.toByteArray());

                putDataRequest.setUrgent();
                Log.d("PhoneToWatch", reps.toString());
                PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(mApiClient, putDataRequest);


                PutDataMapRequest imageDataMapRequest = PutDataMapRequest.create("/images");
                DataMap imageDataMap = imageDataMapRequest.getDataMap();
                imageDataMap.putInt("size", reps.getInt("size"));
                imageDataMap.putLong("time", new Date().getTime());

                ApiHandler.insertPhotos(imageDataMap, reps);
                PutDataRequest imageRequest = imageDataMapRequest.asPutDataRequest();
                PendingResult<DataApi.DataItemResult> pendingImageResult = Wearable.DataApi
                        .putDataItem(mApiClient, imageRequest);



            }

        }).start();

        return START_STICKY;
    }

    @Override //remember, all services need to implement an IBiner
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendMessage( final String path, final String text ) {
        //one way to send message: start a new thread and call .await()
        //see watchtophoneservice for another way to send a message
        new Thread( new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( mApiClient ).await();
                for(Node node : nodes.getNodes()) {
                    //we find 'nodes', which are nearby bluetooth devices (aka emulators)
                    //send a message for each of these nodes (just one, for an emulator)
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mApiClient, node.getId(), path, text.getBytes() ).await();
                    //4 arguments: api client, the node ID, the path (for the listener to parse),
                    //and the message itself (you need to convert it to bytes.)
                }
            }
        }).start();
    }

}
