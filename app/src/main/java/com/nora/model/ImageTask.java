package com.nora.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by sujil on 10/22/2016.
 */

public class ImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;

    public ImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        /*
        String urldisplay = urls[0];

        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            ByteArrayInputStream imageStream = new ByteArrayInputStream(urldisplay);
            mIcon11 = BitmapFactory.decodeStream(in);
            float aspectRatio = mIcon11.getWidth() /
                    (float) mIcon11.getHeight();
            int width = 520;
            int height = Math.round(width / aspectRatio);

            mIcon11 = Bitmap.createScaledBitmap(
                    mIcon11, width, height, false);


            return mIcon11;
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
        return null;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }

}