package com.jepaynedev.townmap;

import android.app.DownloadManager;
import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by James Payne on 7/22/2016.
 * jepayne1138@gmail.com
 */
public class ApiRequestManager {

    public static enum ResponseType {
        GET_CATCHES
    };

    public interface ApiRequestListener {
        public void onApiRequestRecieved(JSONArray jsonArray, ResponseType responseType);
    }

    private ApiRequestListener apiRequestListener;
    private final int CACHE_SIZE = 1024 * 1024;
    private RequestQueue requestQueue;
    private Cache cache;
    private Network network;
    private Context context;

    public ApiRequestManager(Context context, ApiRequestListener apiRequestListener) {
        this.context = context;
        this.apiRequestListener = apiRequestListener;
        cache = new DiskBasedCache(context.getCacheDir(), CACHE_SIZE);
        network = new BasicNetwork(new HurlStack());

        requestQueue = new RequestQueue(cache, network);
        requestQueue.start();
    }

    public void getCatches() {
        String url = String.valueOf(R.string.api_url) + "/catches";
        JsonArrayRequest  jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, new JSONArray(),
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            apiRequestListener.onApiRequestRecieved(
                                    response, ResponseType.GET_CATCHES);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
        });
        requestQueue.add(jsonArrayRequest);
    }
}
