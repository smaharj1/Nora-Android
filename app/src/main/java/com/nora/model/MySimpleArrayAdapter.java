package com.nora.model;

/**
 * Created by sujil on 10/22/2016.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nora.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class MySimpleArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;

    public MySimpleArrayAdapter(Context context, String[] values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.row_image, parent, false);

        final ImageView v = (ImageView) rowView.findViewById(R.id.picture);
        RequestQueue queue = Volley.newRequestQueue(context);

        String url = values[position];

        //final String[] returned={""};
        final String[] collection = {""};


        // Request a string response from the provided URL.
        ImageRequest request = new ImageRequest(url,
                new Response.Listener<Bitmap>() {

                    @Override
                    public void onResponse(Bitmap response) {
                        v.setImageBitmap(response);
                    }
                },0,0,null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse.statusCode == HttpURLConnection.HTTP_MOVED_PERM || networkResponse.statusCode == HttpURLConnection.HTTP_MOVED_PERM){
                   final String location = networkResponse.headers.get("Location");
                    Log.v("Error","Error");
                    Picasso.with(context) //Context
                             .load(location)
                            .resize(800,600)
                    .centerInside()
                    .into((ImageView) rowView.findViewById(R.id.picture));

                }
            }
        });
        // Add the request to the RequestQueue.
        queue.add(request);


        return rowView;
    }


}
