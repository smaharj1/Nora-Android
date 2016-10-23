package com.nora.controller;

import org.json.*;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.github.magiepooh.recycleritemdecoration.ItemDecorations;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nora.R;
import com.nora.model.MySimpleArrayAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PhotoList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);

        /**
         * Requesting the database for the image starts here.
         */
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://f7b48076.ngrok.io/getUserData";

        //final String[] returned={""};
        final String[] collection = {""};


        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {
                        // Display the first 500 characters of the response string.
                        //collection.setText(response);


                        ArrayList<String> images = parse(res);
                        //Toast.makeText(getApplicationContext(), images, Toast.LENGTH_LONG);

                        // Testing
                        String[] imgs = new String[5];
                        for (int i = 0; i < 5; i++) {

                            imgs[i] = images.get(i).substring(1,images.get(i).length()-1);
//                            Log.v("THIS: ", imgs[i]);
                        }

                        ListView photoListView = (ListView) findViewById(R.id.photoList);

                        String[] oo = {"http://compass.xbox.com/assets/23/0d/230dc52a-8f0e-40bf-bbd1-c51fdb8371e3.png?n=Homepage-360-UA_Upgrade-big_1056x594.png", "https://pbs.twimg.com/profile_images/515112446898368512/oQSyacEo.jpeg","https://pbs.twimg.com/profile_images/515112446898368512/oQSyacEo.jpeg","https://pbs.twimg.com/profile_images/515112446898368512/oQSyacEo.jpeg"};
                        // This string array shows how many pictures to put.
                        photoListView.setAdapter(new MySimpleArrayAdapter(getApplicationContext(), imgs));

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);


        //Toast.makeText(getApplicationContext(), collection[0], Toast.LENGTH_LONG).show();

    }

    public ArrayList<String> parse(String jsonLine) {
        JsonElement jelement = new JsonParser().parse(jsonLine);
        JsonObject jobject = jelement.getAsJsonObject();
        //jobject = jobject.getAsJsonObject("items");
        JsonArray jarray = jobject.getAsJsonArray("items");

        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < jarray.size(); i++) {
            jobject = jarray.get(i).getAsJsonObject();
            result.add(jobject.get("image").toString());
        }
        return result;
    }

    private void updatePhotoListView(ArrayList<String> imageURLs) {
        ImageView[] images = new ImageView[imageURLs.size()];

        for (int i = 0; i < imageURLs.size(); i++) {
            images[i] = new ImageView(getApplicationContext());
            images[i].setId(400 + i);
            images[i].setBackgroundResource(R.mipmap.go);
        }


        ArrayAdapter adapter = new ArrayAdapter<ImageView>(this, android.R.layout.simple_list_item_1, images);

        ListView photoListView = (ListView) findViewById(R.id.photoList);
        photoListView.setAdapter(adapter);
    }

}