package com.nora.controller;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;

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
import com.nora.model.RequestURL;
import com.nora.model.StaticNames;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

/**
 * This class holds the image and confirmation that the image has been stored.
 * Then, it displays the image.
 */
public class ImageRecordActivity extends AppCompatActivity {

    private String fileDir;
    private final String FILENAME = "name";
    private final String IMAGE = "image";
    private String userID;

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_record);

        ImageView img = (ImageView) findViewById(R.id.images);

        Bitmap bm = getIntent().getParcelableExtra("image");

        String extraMessage = getIntent().getStringExtra(StaticNames.USER_ID);
        if (!extraMessage.isEmpty()) {
            userID = extraMessage;
        }

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        if (bm != null) {
            img.setImageBitmap(bm);

            //create a file to write bitmap data
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "nora_" + timeStamp + "_";
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

            fileDir = storageDir.getAbsolutePath();
            File newFile = new File(fileDir+"/"+imageFileName+".jpg");

            writeToFile(newFile, bm);

            storeInServer(bm, imageFileName);
        }
    }

    public static void writeToFile(File newFile, Bitmap bitmap) {
        //Convert bitmap to byte array
        //Bitmap bitmap = bm;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0 , bos);
        byte[] bitmapdata = bos.toByteArray();

        //write the bytes in file
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(newFile);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            //System.out.println("File stored");
            if (newFile.exists()) {
                System.out.println("New file created: " + newFile.getName());
            }
            else {
                System.out.println("New file wasn't created");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void storeInServer(final Bitmap bmPhoto, final String filename) {
        String sendingURL = RequestURL.URL + "storePhoto";
        StringRequest loginRequest = new StringRequest(Request.Method.POST, sendingURL,
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

                String encodedImage = encodeImage(bmPhoto);

                //Adding parameters
                params.put(FILENAME, filename);
                params.put(IMAGE, encodedImage);

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

        loginRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(loginRequest);
    }

    /**
     * Encodes the Bitmap image to a string
     * @param photo It is the bitmap image.
     * @return Returns the base 64 equivalent of the image.
     */
    private String encodeImage(Bitmap photo) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
}
