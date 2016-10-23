package com.nora.model;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nora.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AnalyticActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytic);

        final String url = getIntent().getStringExtra("url");
        TextView v = (TextView) findViewById(R.id.contentURL);

        RequestQueue queue = Volley.newRequestQueue(this);
        String urlMain = "http://cbf1bcba.ngrok.io/bankAnalytics";

        //final String[] returned={""};
        final String[] collection = {""};
        //RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest sr = new StringRequest(Request.Method.POST,urlMain, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //mPostCommentResponse.requestCompleted();
                TextView v = (TextView) findViewById(R.id.contentURL);
                Log.v("Response",response);
                try {
                    JSONObject json = new JSONObject(response);
                    StringBuilder data =new StringBuilder();
                    data.append("Name: ");
                    data.append(json.getString("title"));
                    data.append("\n\nPrice: ");
                    data.append(json.getString("price"));
                    data.append("\n\nBalance before Purchase: ");
                    data.append(json.getString("balance"));
                    data.append("\n\nBalance after Purchase: ");
                    data.append(json.getString("newBalance"));
                    data.append("\n\nRate of Decrease: ");
                    NumberFormat formatter = new DecimalFormat("#0.00");
                    data.append(formatter.format(json.getDouble("rate")));
                    v.setText(data.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.v("Response", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mPostCommentResponse.requestEndedWithError(error);
                error.printStackTrace();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("phone","+19084774708");
                params.put("url", url);


                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(sr);
    }
}
