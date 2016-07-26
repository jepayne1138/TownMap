package com.jepaynedev.townmap;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jepaynedev.townmap.database.DatabaseAdapter;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import static com.jepaynedev.townmap.ApiRequestManager.ResponseType.GET_CATCHES;

/**
 * Created by James Payne on 7/25/2016.
 * jepayne1138@gmail.com
 */
public class AsyncLoadMarkers extends AsyncTask<Void, Void, Void> implements
        ApiRequestManager.ApiRequestListener {

    private static final String LOG_TAG = "AsyncLoadMarkers";

    public interface AsyncMarkersResponse {
        void onFinish(List<MarkerOptions> markers);
    }

    private Context context;
    private AsyncMarkersResponse listener;
    private List<MarkerOptions> markers = new ArrayList<>();
    private Hashtable<Integer, String> creatureNames;
    private ApiRequestManager apiRequestManager;


    public AsyncLoadMarkers(Context context, AsyncMarkersResponse listener) {
        this.context = context;
        this.listener = listener;

        // Get database adapter and build map of creature ids to names
        DatabaseAdapter databaseAdapter = new DatabaseAdapter(context).createDatabase().open();
        creatureNames = databaseAdapter.getCreatureNameMap();

        // Set up api manager
        apiRequestManager = new ApiRequestManager(context, this);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.d(LOG_TAG, "doInBackground");

        // Create backend API manager
        apiRequestManager.getCatches();

        return null;
    }

    @Override
    public void onApiRequestReceived(
            JSONArray jsonArray, ApiRequestManager.ResponseType responseType) {
        Log.d(LOG_TAG, "onApiRequestReceived");
        switch (responseType) {
            case GET_CATCHES:
                for (int index=0; index<jsonArray.length(); index++) {
                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(index);
                        addCreatureMarker(
                                jsonObject.getInt("creatureId"),
                                jsonObject.getDouble("latitude"),
                                jsonObject.getDouble("longitude"));

                    } catch (JSONException error) {
                        // Bad api call
                        error.printStackTrace();
                    }
                }
                break;
            default:
                throw new IllegalArgumentException(
                        "No handler for response: " + responseType.toString());
        }
    }

    @Override
    public void postApiRequest(ApiRequestManager.ResponseType responseType) {
        Log.d(LOG_TAG, "postApiRequest");
        if (responseType == GET_CATCHES) {
            listener.onFinish(markers);
        }
    }

    private void addCreatureMarker(int markerId, double latitude, double longitude) {
        Log.d(LOG_TAG, "addCreatureMarker");
        String title = creatureNames.get(markerId);
        LatLng latLng = new LatLng(latitude, longitude);
        markers.add(new MarkerOptions()
                .position(latLng)
                .title(title)
                .icon(BitmapDescriptorFactory.fromResource(context.getResources().getIdentifier(
                        "creature" + markerId, "drawable", "com.jepaynedev.townmap")))
                .anchor(0.5f, 0.5f)
        );
    }

}
