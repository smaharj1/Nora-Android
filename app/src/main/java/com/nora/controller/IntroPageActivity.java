package com.nora.controller;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.nora.R;
import com.nora.model.StaticNames;
import com.nora.model.UserSharedPreferences;


public class IntroPageActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_page);

        String extraMessage = getIntent().getStringExtra(StaticNames.USER_ID);
        if (!extraMessage.isEmpty()) {
            userID = extraMessage;
        }
    }

    /**
     * Event listener for going to the wishlist/photolist of the current user.
     * @param view
     */
    public void goToWishlist(View view) {
        // Make Volley Request here.
        Intent wishlistIntent = new Intent(getApplicationContext(), WishListActivity.class);
        wishlistIntent.putExtra(StaticNames.USER_ID, userID);

        startActivity(wishlistIntent);

    }

    /**
     * Event listener for the button to add new photo through camera
     * @param view
     */
    public void addPhoto(View view) {
        startCamera();
        //System.out.println("Clicked")
        // dispatchTakePictureIntent();
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
            newIntent.putExtra(StaticNames.USER_ID, userID);
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
