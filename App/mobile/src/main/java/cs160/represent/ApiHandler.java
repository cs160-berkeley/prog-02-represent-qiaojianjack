package cs160.represent;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataMap;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jianqiao on 3/6/16.
 */
public class ApiHandler {

    private static String LOG_TAG = "ApiHandler";
    private RequestQueue queue;
    private Context mContext;
    private String apiKey;

    private TwitterApiClient twitterApiClient;

    public static JSONArray electionJson = null;



    public ApiHandler(Context context, TwitterApiClient twitterClient) {

        mContext = context;
        queue = Volley.newRequestQueue(context);
        twitterApiClient = twitterClient;
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            apiKey = bundle.getString("com.google.android.geo.API_KEY");
            Log.d("LOG_TAG", apiKey);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(LOG_TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, "Failed to load meta-data, NullPointer: " + e.getMessage());
        }
    }


    public void getLocation(Bundle params, RepList.RepCallback callback) {
        Resources res = mContext.getResources();
        if (params.getString("location_type").equals(res.getString(R.string.location_type_zipcode))) {
            String zipcode = params.getString("zipcode");
            getLocationFromZipcode(zipcode, callback);
        } else if (params.getString("location_type").equals(res.getString(R.string.location_type_latlong))) {
            double latitude = params.getDouble("latitude", Double.NaN);
            double longitude = params.getDouble("longitude", Double.NaN);
            getLocationFromLatLong(latitude, longitude, callback);
        }
    }

    //TODO: write function to get latlong from county name
    public void getLagLongFromCounty(String county, String state, final PhoneListenerService.RandomLocationCallback callback) {

        String url = "https://maps.googleapis.com/maps/api/geocode/json?" +
                "address=" + county + ",+" + state +
                "&key=" + apiKey;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(LOG_TAG, response.toString());
                            JSONObject result = response.getJSONArray("results").getJSONObject(0);
                            JSONObject location = result.getJSONObject("geometry").getJSONObject("location");

                            double latitude = location.getDouble("lat");
                            double longitude = location.getDouble("lng");
                            //TODO: call callback
                            callback.callback(latitude, longitude);
                        } catch (JSONException e) {
                            Log.d(LOG_TAG, "here getLatLongFromCounty: " + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.d(LOG_TAG, "Error request!!");
                Log.d(LOG_TAG, error.getMessage());
            }
        });

        queue.add(request);
    }

    protected void getLocationFromLatLong(double latitude, double longitude, final RepList.RepCallback callback) {
        String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" +
                Double.toString(latitude) + "," + Double.toString(longitude) +
                "&key=" + apiKey
                + "&result_type=administrative_area_level_2|administrative_area_level_1";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    String county = processLocationJson(response);
                    callback.onGetLocationSuccess(county);
                }
            }, new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO Auto-generated method stub
                    Log.d(LOG_TAG, "Error request!!");
                    Log.d(LOG_TAG, error.getMessage());
                }
        });

        queue.add(request);

    }

    protected void getLocationFromZipcode(String zipcode, final RepList.RepCallback callback) {
        String url = "https://maps.googleapis.com/maps/api/geocode/json?" +
                "address=" + zipcode +
                "&key=" + apiKey
                + "&result_type=administrative_area_level_2";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    String county = processLocationJsonForZipcode(response);
                    Log.d(LOG_TAG, county);
                    callback.onGetLocationSuccess(county);
                }
            }, new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO Auto-generated method stub
                    Log.d(LOG_TAG, "Error request!!");
                    Log.d(LOG_TAG, error.getMessage());
                }
        });

        queue.add(request);

    }


    private String processLocationJson(JSONObject json) {
        try {
            JSONArray addresses = json.getJSONArray("results");
            for (int i = 0; i < addresses.length(); i++) {
                JSONObject address = addresses.getJSONObject(i);
                JSONArray addressTypes = address.getJSONArray("types");
                if (addressTypes.toString().contains("\"administrative_area_level_2\"")) {
                    String[] countyComponent = address.getString("formatted_address").split(", ");
                    String countyName = countyComponent[0].replace(" County", "");
                    String stateAbbr = countyComponent[1];
                    return countyName + ", " + stateAbbr;
                }
            }
            Log.d(LOG_TAG, "Error: parse location json failed: " + json.toString());
        } catch (JSONException e) {
            Log.d(LOG_TAG, e.getMessage());
        }
        return null;
    }

    private String processLocationJsonForZipcode(JSONObject json) {
        try {
            JSONArray components = json.getJSONArray("results").getJSONObject(0)
                    .getJSONArray("address_components");

            String countyName = "";
            String stateAbbr = "";
            for (int i = 0; i < components.length(); i++) {
                JSONObject comp = components.getJSONObject(i);
                JSONArray addressTypes = comp.getJSONArray("types");

                if (addressTypes.toString().contains("\"administrative_area_level_2\"")) {
                    String countyComponent = comp.getString("short_name");
                    countyName = countyComponent.replace(" County", "");
                } else if (addressTypes.toString().contains("\"administrative_area_level_1\""))
                    stateAbbr = comp.getString("short_name");
            }
            if (countyName.isEmpty() || stateAbbr.isEmpty()) {
                Log.d(LOG_TAG, "Error: parse location json with zipcode failed: " + json.toString());
                return null;
            } else {
                return countyName + ", " + stateAbbr;
            }
        } catch (JSONException e) {
            Log.d(LOG_TAG, e.getMessage());
        }
        return null;
    }



    public void getRepList(Bundle params, final RepList.RepCallback callback) {
        Resources res = mContext.getResources();
        String apiParams = "";
        if (params.getString("location_type").equals(res.getString(R.string.location_type_zipcode))) {
            String zipcode = params.getString("zipcode");
            apiParams = "zip="+zipcode;

        } else if (params.getString("location_type").equals(res.getString(R.string.location_type_latlong))) {
            double latitude = params.getDouble("latitude", Double.NaN);
            double longitude = params.getDouble("longitude", Double.NaN);
            apiParams = "latitude=" + Double.toString(latitude) + "&longitude=" + Double.toString(longitude);
        }

        String url = "http://congress.api.sunlightfoundation.com/legislators/locate?" + apiParams +
                "&apikey=0364797da4264095b57960738468b2ce";

        Log.d(LOG_TAG, "getRepList: ready to construct request");

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Log.d(LOG_TAG, "getRepList: list get response");
                        JSONArray results = response.getJSONArray("results");
                        int numResults = response.getInt("count");
                        Representative.repMap = new HashMap<String, Representative>();
                        for (int i = 0; i < numResults; i++) {
                            Representative r = assembleRepresentative(results.getJSONObject(i));
                            Representative.repMap.put(r.repId, r);
                        }
                        Log.d(LOG_TAG, "Reps created! now load committee and bill");

                        Object[] repIdstemp = Representative.repMap.keySet().toArray();
                        String[] repIds = new String[repIdstemp.length];
                        for (int i = 0; i < repIdstemp.length; i++) {
                            repIds[i] = (String) repIdstemp[i];
                        }
                        fillCommitteeAndBill(repIds, 0, callback);
                    } catch (JSONException e) {
                        Log.d("LOG_TAG", "getRepList: " + e.getMessage());
                    }
                }
            }, new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(LOG_TAG, "Error request!!");
                    Log.d(LOG_TAG, error.getMessage());
                }
        });

        queue.add(request);
        Log.d(LOG_TAG, "request for representatives put in the queue");
    }

    private Representative assembleRepresentative(JSONObject result) {
        Representative rep = new Representative();
        try {
            //TODO: change repId to string
            rep.repId = result.getString("bioguide_id");
            String title = result.getString("title");
            if (title.equals("Sen")) {
                rep.title = "Senator";
            } else if (title.equals("Rep")) {
                rep.title = "Representative";
            }
            rep.firstName = result.getString("first_name");
            rep.lastName = result.getString("last_name");
            String party = result.getString("party");
            if (party.equals("D")) {
                rep.party = "Democratic";
            } else if (party.equals("R")) {
                rep.party = "Republican";
            } else {
                rep.party = "Independent";
            }
            rep.email = result.getString("oc_email");
            rep.website = result.getString("website");
            rep.termEndDate = result.getString("term_end");
            rep.tweetHandle = '@' + result.getString("twitter_id");
        } catch (JSONException e) {
            Log.d(LOG_TAG, e.getMessage());
        }
        return rep;
    }


    //TODO: add Twitter support
    //TODO: add committee info and bill info

    private void fillCommitteeAndBill(final String[] repIds, final int ind, final RepList.RepCallback callback) {
        final String repId = repIds[ind];
        String committeeUrl = "http://congress.api.sunlightfoundation.com/committees?member_ids=" + repId +
                "&apikey=0364797da4264095b57960738468b2ce";
        final String billUrl = "http://congress.api.sunlightfoundation.com/bills?sponsor_id=" + repId +
                "&apikey=0364797da4264095b57960738468b2ce";

        JsonObjectRequest committeeRequest = new JsonObjectRequest(Request.Method.GET, committeeUrl, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    //TODO: process replist from api
                    try {
                        JSONArray results = response.getJSONArray("results");
                        int numResults = response.getJSONObject("page").getInt("count");
                        final Representative r = Representative.repMap.get(repId);
                        ArrayList<String> committees = new ArrayList<String>();
                        for (int i = 0; i < numResults; i++) {
                            JSONObject committee = results.getJSONObject(i);
                            committees.add(committee.getString("name"));
                        }
                        r.committees = committees;

                        //construct bill request
                        JsonObjectRequest billRequest = new JsonObjectRequest(Request.Method.GET, billUrl, null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            JSONArray results = response.getJSONArray("results");
                                            int numResults = response.getJSONObject("page").getInt("count");
                                            ArrayList<Map.Entry<String, String>> bills = new ArrayList<Map.Entry<String, String>>();
                                            for (int i = 0; i < numResults; i++) {
                                                JSONObject bill = results.getJSONObject(i);
                                                bills.add(
                                                        new AbstractMap.SimpleEntry<String, String>(bill.getString("introduced_on"),
                                                                bill.getString("official_title")));
                                            }
                                            r.recentBills = bills;
                                            if (ind + 1 < repIds.length) {
                                                fillCommitteeAndBill(repIds, ind + 1, callback);
                                            } else {
                                                Log.d("LOG_TAG", "Time to call Twitter");
                                                fillTwitterInfo(Representative.repMap, repIds, 0, callback);

                                            }
                                        } catch (JSONException e) {
                                            Log.d("LOG_TAG", "getRepList: " + e.getMessage());
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(LOG_TAG, "Error request!!");
                                Log.d(LOG_TAG, error.getMessage());
                            }
                        });
                        queue.add(billRequest);


                    } catch (JSONException e) {
                        Log.d("LOG_TAG", "getRepList: " + e.getMessage());
                    }

                }
            }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(LOG_TAG, "Error request!!");
                        Log.d(LOG_TAG, error.getMessage());
                    }
            });
        queue.add(committeeRequest);
    }

    private void fillTwitterInfo(final HashMap<String, Representative> repMap,
            final String[] repIds, final int ind, final RepList.RepCallback callback) {

        StatusesService statusesService = twitterApiClient.getStatusesService();
        String repId = repIds[ind];
        final Representative r = repMap.get(repId);
        String twitterHandle = r.tweetHandle.substring(1);

        statusesService.userTimeline(null, twitterHandle, 1, null, null, null, true, null, true,
                new Callback<List<Tweet>>() {
                    @Override
                    public void success(Result<List<Tweet>> result) {
                        Tweet tweet = result.data.get(0);
                        r.tweet = tweet.text;
                        r.imgUrl = tweet.user.profileImageUrl.replace("normal", "bigger");
                        Picasso.with(mContext).load(r.imgUrl).into(
                                new Target() {
                                    @Override
                                    public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                                        //Set it in the ImageView
                                        r.photoBitmap = bitmap;
                                        if (ind + 1 < repIds.length) {
                                            fillTwitterInfo(repMap, repIds, ind + 1, callback);
                                        } else {
                                            //call callback here to go back to RepList
                                            callback.onGetRepListSuccess();
                                        }
                                    }

                                    @Override
                                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                                    }

                                    @Override
                                    public void onBitmapFailed(Drawable errorDrawable) {

                                    }
                                }
                        );

                    }

                    @Override
                    public void failure(TwitterException e) {
                        Log.d(LOG_TAG, "fillTwitterInfo: " + e.getMessage());
                    }
                });

    }

    public Bundle getElectionData(String locationString) {
        if (ApiHandler.electionJson == null) {
            loadJSONFromAsset();
        }
        String[] locComponents = locationString.split(", ");
        String state = locComponents[1];
        String county = locComponents[0];
        Log.d(LOG_TAG, "getElectionData: county: " + county + ", state: " + state);
        try {
            for (int i = 0; i < ApiHandler.electionJson.length(); i++) {
                JSONObject countyData = ApiHandler.electionJson.getJSONObject(i);
                if (countyData.getString("county-name").equals(county)
                        && countyData.getString("state-postal").equals(state)) {
                    Bundle electionBundle = new Bundle();
                    electionBundle.putString("obama", countyData.getString("obama-percentage"));
                    electionBundle.putString("romney", countyData.getString("romney-percentage"));
                    return electionBundle;
                }
            }
            Log.d(LOG_TAG, "getElectionData: didn't find the location, return null now");
            return null;
        } catch (JSONException e) {
            Log.d(LOG_TAG, "getElectionData: " + e.getMessage());
            return null;
        }
    }

    public void loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = mContext.getAssets().open("election-county-2012.json");
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

    public static void insertPhotos(DataMap data, Bundle reps) {
        int size = reps.getInt("size");
        for (int i = 0; i < size; i++) {
            Bundle rep = reps.getBundle("bundle" + Integer.toString(i));
            String repId = rep.getString("repId");
            if (Representative.repMap.get(repId).photoBitmap == null) {
                Log.d(LOG_TAG, "insertPhotos for " + repId + ", no photos");
            }
            Asset photo = ApiHandler.createAssetFromBitmap(Representative.repMap.get(repId).photoBitmap);
            data.putString("repId|" + Integer.toString(i), repId);
            data.putAsset("photo|" + repId, photo);
        }
    }

    private static Asset createAssetFromBitmap(Bitmap bitmap) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        return Asset.createFromBytes(byteStream.toByteArray());
    }




}
