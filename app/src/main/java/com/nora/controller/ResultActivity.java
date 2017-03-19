package com.nora.controller;

import android.app.ProgressDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
            final String encodedString;

            try {
                encodedString = ImageHandler.encodeImageFileToBase64(filePath);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return;
            }
            getBestPrice(encodedString, isByTerm);
        }




    }

    private void getBestPrice(final String givenName, final boolean isSearch) {
        String sendingURL;

        if (isSearch) {
            sendingURL = RequestURL.URL + "keywordSearch";
        }
        else {
            sendingURL = RequestURL.URL + "ProcessImage";

        }

        final ProgressDialog progressDialog = new ProgressDialog(ResultActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Finding best price...");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, sendingURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {
                        JsonElement jsonElement = new JsonParser().parse(res);
                        JsonObject jobject = jsonElement.getAsJsonObject();

                        String condition = jobject.get("condition").getAsString();
                        String price = jobject.get("price").getAsString();
                        String offerURL = jobject.get("offerURL").getAsString();
                        String detailURL = jobject.get("detail").getAsString();

                        TextView bestPrice = (TextView) findViewById(R.id.bestPrice);
                        TextView conditionView = (TextView) findViewById(R.id.condition);
                        TextView offerView = (TextView) findViewById(R.id.offerLink);
                        TextView descriptionLinkView = (TextView) findViewById(R.id.descriptionLink);

                        bestPrice.setText(price);
                        conditionView.setText(condition);

                        String linkedText = String.format("<a href=\"%s\">Click here</a> ", offerURL);

                        offerView.setText(Html.fromHtml(linkedText));
                        offerView.setMovementMethod(LinkMovementMethod.getInstance());

                        linkedText = String.format("<a href=\"%s\">Click here</a> ", detailURL);

                        descriptionLinkView.setText(Html.fromHtml(linkedText));
                        descriptionLinkView.setMovementMethod(LinkMovementMethod.getInstance());

                        progressDialog.cancel();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        System.out.println("Couldn't feed request from the server");

                        Snackbar mySnackbar = Snackbar.make(findViewById(R.id.resultLayout),
                                "Server was not that friendly this time. Try again", Snackbar.LENGTH_LONG);
                        mySnackbar.show();
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
                    params.put(PHOTO, givenName);
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
