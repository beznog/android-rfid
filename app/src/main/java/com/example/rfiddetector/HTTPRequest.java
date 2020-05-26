package com.example.rfiddetector;

import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import org.json.JSONException;
import org.json.JSONObject;


public class HTTPRequest {

    private Context mContext;

    public String hostUrl = "http://18.224.232.10:8000/scan/";
    public Integer hostPort = 8000;

    public HTTPRequest (Context context) {
        this.mContext = context;
    }

    public void sendPostRequest (int method, String url, int port, JSONObject parameters) {

        RequestQueue queue = Volley.newRequestQueue(this.mContext);
        try {
            JsonObjectRequest request = new JsonObjectRequest(
                method,
                url,
                parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String responseToShow = response.toString();
                            //Notification.showPopUpWindow(mContext, responseToShow);
                        }
                        catch (Exception e) {
                            Notification.showPopUpWindow(mContext, "Request error: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    //Create an error listener to handle errors appropriately.
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        Log.d("Request error: ",  e.getMessage());
                        //Notification.showPopUpWindow(mContext, "Request error: " + e.getMessage());
                    }
                }
            );
            queue.add(request);
        }
        catch (Exception e) {
            Notification.showPopUpWindow(mContext, "Request error: " + e.getMessage());
        }
    }
}