package cs160.represent;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joleary and noon on 2/19/16 at very late in the night. (early in the morning?)
 */
public class WatchToPhoneService extends Service {

    private GoogleApiClient mWatchApiClient;

    private final String LOAD_INFO = "load_info";
    private final String RANDOM_LOCATION = "random_location";

    private String toSend;
    private String payload = "";

    @Override
    public void onCreate() {
        super.onCreate();
        //initialize the googleAPIClient for message passing
        mWatchApiClient = new GoogleApiClient.Builder( this )
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
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("WatchToPhone", "started");
        String command = intent.getStringExtra("command");
        toSend = command;
        if (command.equals(LOAD_INFO)) {
            String repId = intent.getStringExtra("rep_id");
            payload = repId;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                //first, connect to the apiclient
                mWatchApiClient.connect();
                //now that you're connected, send a massage with the cat name
                sendMessage(toSend, payload);
                Log.d("WatchToPhone", "message sent");

            }
        }).start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWatchApiClient.disconnect();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendMessage(final String path, final String text ) {
        new Thread( new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( mWatchApiClient ).await();
                for (Node node : nodes.getNodes()) {
                    Log.d("WatchToPhone", "sendMessage: " + path + ", " + text);
                    Wearable.MessageApi.sendMessage(
                        mWatchApiClient, node.getId(), path, text.getBytes());
                }

            }
        }).start();
    }

}
