package com.nora.controller;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.nora.R;

/**
 * This class holds the image and confirmation that the image has been stored.
 * Then, it displays the image.
 */
public class ImageRecordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_record);

        ImageView img = (ImageView) findViewById(R.id.images);

        Bitmap bm = getIntent().getParcelableExtra("image");

        if (bm != null) {
            img.setImageBitmap(bm);
        }

    }
}
