package com.example.toshiba1.flickersearch.Controller;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

/**
 * Created by toshiba1 on 12/18/2015.
 */
public class CustomJsonGeRequest extends JsonObjectRequest {


    private Priority mPriority;




    public CustomJsonGeRequest(String url, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super( url, listener, errorListener);
    }


    public void setPriority(Priority priority) {
        mPriority = priority;
    }

    @Override
    public Priority getPriority() {
        return mPriority == null ? Priority.NORMAL : mPriority;
    }

}