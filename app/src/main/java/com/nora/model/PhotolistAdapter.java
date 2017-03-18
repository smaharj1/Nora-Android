package com.nora.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.nora.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.HttpURLConnection;

/**
 * Created by sujil on 3/18/2017.
 */

public class PhotolistAdapter extends ArrayAdapter<File> {

    private final Context context;
    private final File[] values;

    public PhotolistAdapter(Context context, File[] values) {
        super(context,-1,values);

        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View rowView = inflater.inflate(R.layout.row_image, parent, false);
        rowView.setMinimumHeight(1000);


        File file = values[position];

        Picasso.with(context) //Context
                .load(file)
                .into((ImageView) rowView.findViewById(R.id.picture));

        return rowView;
    }
}
