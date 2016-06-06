package com.example.liuqun.newsdaily.model.volleyhttp;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;

/**
 * Created by 90516 on 6/4/2016.
 */
public class MultiPosttRequest extends Request {
    public MultiPosttRequest(String url, Response.ErrorListener listener) {
        super(url, listener);
    }

    @Override
    protected Response parseNetworkResponse(NetworkResponse response) {
        return null;
    }

    @Override
    protected void deliverResponse(Object response) {

    }

    @Override
    public int compareTo(Object another) {
        return 0;
    }
}
