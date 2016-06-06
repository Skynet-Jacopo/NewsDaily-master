package com.example.liuqun.newsdaily.model.volleyhttp;

import android.content.Context;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.liuqun.newsdaily.R;

import java.io.File;

/**
 * Created by 90516 on 6/2/2016.
 */
public class VolleyHttp {
    public static RequestQueue mQueue;

    Context context;

    public VolleyHttp(Context context) {
        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(context);
        }
        this.context = context;
    }
    public void getJSONObject (String url, Response.Listener<String>
            listener, Response.ErrorListener errorListener){
        StringRequest request =new StringRequest(url,listener,errorListener);
        mQueue.add(request);
    }
    public void addImage(String url, ImageLoader.ImageCache imageCache, ImageView iv) {
        ImageLoader mImageLoader = new ImageLoader(mQueue, imageCache);
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(iv,
                R.drawable.defaultpic, android.R.drawable.ic_delete);
        mImageLoader.get(url, listener);
    }

    public void uploadImage(String url, File file, Response.Listener<String>
            listener, Response.ErrorListener errorListener){

    }



}
