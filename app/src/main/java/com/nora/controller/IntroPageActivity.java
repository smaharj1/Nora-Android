package com.nora.controller;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nora.R;
import com.nora.model.RequestURL;
import com.nora.model.StaticNames;
import com.nora.model.UserSharedPreferences;

import java.io.File;
import java.util.Hashtable;
import java.util.Map;


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

    public void refreshImagesFromServer(View view) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        String sendingURL = RequestURL.URL + "getPhotos";
        StringRequest getRequest = new StringRequest(Request.Method.GET, sendingURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {
                        // Parse the JSON to get the list of base 64 images.
                        JsonElement jsonElement = new JsonParser().parse(res);
                        JsonObject jobject = jsonElement.getAsJsonObject();

                        boolean status = jobject.get("status").getAsBoolean();

                        // If status is true, then do all the computation. If not, then return.
                        if (!status ) return;

                        deleteExistingPhotos();

                        JsonArray pArray = jobject.get("photos").getAsJsonArray();

                        for (int i =0; i < pArray.size(); i++) {
                            JsonObject object = pArray.get(i).getAsJsonObject();

                            String filename = object.get("name").getAsString() +".jpg";
                            String storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();

                            String path = storageDir+"/"+filename;

                            File newFile = new File(path);

                            Bitmap image = decodeImage(object.get("image").getAsString());

                            ImageRecordActivity.writeToFile(newFile, image);

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        System.out.println("Couldn't feed request from the server");


                    }
                }){

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

        getRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(getRequest);
    }

    private Bitmap decodeImage(String base64EncodedImage){
        byte[] decodedString = Base64.decode(base64EncodedImage, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        return decodedImage;
    }

    private void deleteExistingPhotos() {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        for (File tempFile : storageDir.listFiles()) {
            if (tempFile.getName().startsWith("nora")) {
                tempFile.delete();
            }
        }
    }

}
