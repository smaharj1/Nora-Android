package com.nora.controller;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.nora.R;
import com.nora.model.FileHandler;
import com.nora.model.UserSharedPreferences;

import java.io.File;
import java.io.IOException;

import butterknife.InjectView;

import static com.nora.model.FileHandler.createImageFile;

public class IntroPageActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_page);
    }

    /**
     * Event listener for going to the wishlist/photolist of the current user.
     * @param view
     */
    public void goToWishList(View view) {
        // Make Volley Request here.

    }

    /**
     * Event listener for the button to add new photo through camera
     * @param view
     */
    public void addPhoto(View view) {
        //startCamera();

        dispatchTakePictureIntent();
    }

    /**
     * Starts the camera intent for capturing the picture
     */
    private void startCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    /**
     * Takes the picture and stores it locally and externally.
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = FileHandler.createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.v("FILE ERROR", "The file could not be created. ");
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    /**
     * This funtion returns from the camera with the photo captured.
     * @param requestCode Checks if the image has been captured.
     * @param resultCode Holds the result code.
     * @param data Holds the data of the image.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // System.out.println("HEYY: Image being received" );

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            Intent newIntent = new Intent(getApplicationContext(), ImageRecordActivity.class);
            newIntent.putExtra("image", imageBitmap);
            startActivity(newIntent);
        }
    }

    /**
     * Event handler for logging out of the application.
     * @param view
     */
    public void performLogout(View view) {
        UserSharedPreferences.clearUserName(getApplicationContext());

        Intent loginPage = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(loginPage);

        finish();

    }

}
