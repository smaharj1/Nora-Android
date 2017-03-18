package com.nora.controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nora.R;
import com.nora.model.ImageHandler;
import com.nora.model.RequestURL;
import com.nora.model.StaticNames;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {
    private final String PHOTO="photo";
    private RequestQueue requestQueue;
    public String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        userID = getIntent().getStringExtra(StaticNames.USER_ID);
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        String type = getIntent().getStringExtra("type");
        boolean isByTerm = true;
        if (type.equalsIgnoreCase("search")) {
            isByTerm = true;
            String term = getIntent().getStringExtra("term");
            getBestPrice(term, isByTerm);
        }
        else {
            isByTerm = false;
            String filePath = getIntent().getStringExtra("imageFile");
            getBestPrice(filePath, isByTerm);
        }




    }

    private void getBestPrice(final String givenName, final boolean isSearch) {
        String sendingURL;
        final String encodedString;

        if (isSearch) {
            sendingURL = RequestURL.URL + "keywordSearch";
        }
        else {
            sendingURL = RequestURL.URL + "ProcessImage";
        }

        try {
            encodedString = ImageHandler.encodeImageFileToBase64(givenName);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST, sendingURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        System.out.println("Couldn't feed request from the server");


                    }
                }){
            /**
             * Sends the username and password info as a parameter
             * @return
             * @throws AuthFailureError
             */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                if (isSearch) {
                    // THis is not actually a filepath
                    params.put("term", givenName);
                }
                else {
                    params.put(PHOTO, encodedString);
                }
                //returning parameters
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                //Creating parameters
                Map<String,String> headers = new Hashtable<String, String>();

                //Adding parameters
                headers.put(StaticNames.USER_ID, userID);

                //returning parameters
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(request);
    }
}
