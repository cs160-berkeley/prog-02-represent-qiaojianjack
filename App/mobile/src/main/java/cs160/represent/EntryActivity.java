package cs160.represent;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import io.fabric.sdk.android.Fabric;


public class EntryActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "S4HGAxMgmEXpjZW9pDFGu8slS";
    private static final String TWITTER_SECRET = "Qm9WiUTfcEnIxytI0lJUlzu2hJJ27OjUiZxMpOoXSG1yNA08vv";


    private GoogleApiClient mGoogleApiClient;
    private static String LOG_TAG = "EntryActivity";
    private double latitude;
    private double longitude;

    private TwitterLoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        //TwitterAuthConfig authConfig =  new TwitterAuthConfig("consumerKey", "consumerSecret");
        //Fabric.with(this, new TwitterCore(authConfig));
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        setContentView(R.layout.activity_entry);

        Typeface sansSerifLight = Typeface.createFromAsset(getAssets(), "fonts/SourceSansPro-Light.ttf");

        final Resources res = getResources();

        final EditText zipcode_input = (EditText) findViewById(R.id.zipcode_input);
        zipcode_input.setTypeface(sansSerifLight);
        zipcode_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    if (!event.isShiftPressed()) {
                        // the user is done typing.
                        Intent toRepListView = new Intent(EntryActivity.this, RepList.class);
                        String zipcode = zipcode_input.getText().toString(); // consume.
                        Bundle bundle = new Bundle();
                        bundle.putString("location_type", res.getString(R.string.location_type_zipcode));
                        bundle.putString("zipcode", zipcode);
                        toRepListView.putExtras(bundle);
                        startActivity(toRepListView);
                        return true;
                    }
                }
                return false;
            }
        });


        Button locationButton = (Button) findViewById(R.id.location_button);
        locationButton.setTypeface(sansSerifLight);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //detect location here
                Intent toRepListView = new Intent(EntryActivity.this, RepList.class);
                Bundle bundle = new Bundle();
                bundle.putString("location_type", res.getString(R.string.location_type_latlong));
                bundle.putDouble("latitude", latitude);
                bundle.putDouble("longitude", longitude);
                toRepListView.putExtras(bundle);
                startActivity(toRepListView);
            }
        });


        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // The TwitterSession is also available through:
                // Twitter.getInstance().core.getSessionManager().getActiveSession()
                TwitterSession session = result.data;
                // TODO: Remove toast and use the TwitterSession's userID
                // with your app's user model
                String msg = "@" + session.getUserName() + " logged in! (#" + session.getUserId() + ")";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(TwitterException exception) {
                Log.d("TwitterKit", "Login with Twitter failure", exception);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Make sure that the loginButton hears the result from any
        // Activity that it triggered.
        loginButton.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(LOG_TAG, "Connected");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED) {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
            Log.d(LOG_TAG, "location: " + Double.toString(latitude) + " " + Double.toString(longitude));
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d(LOG_TAG, "Connection Failed");
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        Log.d(LOG_TAG, "Connection Suspended");
    }


}
