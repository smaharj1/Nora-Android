package com.nora.controller;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


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
import com.nora.model.UserSharedPreferences;

import java.util.Hashtable;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    private final String PASSWORD="password";
    private final String USERNAME = "username";

    @InjectView(R.id.input_username) EditText userNameText;
    @InjectView(R.id.input_password) EditText passwordText;
    @InjectView(R.id.btn_login) Button loginButton;
    @InjectView(R.id.link_signup) TextView signupLink;

    private RequestQueue requestQueue;

    // This tracks if the server call has completed successfully.
    private boolean loadSuccess;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Checks if the user has already signed in the app.
        if (UserSharedPreferences.getUserID(getApplicationContext()).length() != 0 ) {
            // Create an intent that directly takes to the main activity
            Intent intent = new Intent(getApplicationContext(), IntroPageActivity.class);
            intent.putExtra(StaticNames.USER_ID, UserSharedPreferences.getUserID(getApplicationContext()));
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        loadSuccess = false;

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        loginButton.setEnabled(false);


        String username = userNameText.getText().toString();
        String password = passwordText.getText().toString();

        // TODO: SSL is already done. Implement volley here.

        sendLoginInfo(username, password);
    }


    /**
     * Sends and validates the log in info from the server
     * @param user It holds the username. It is final as it has to be passed through string request.
     * @param pswd It holds the password.
     */
    private void sendLoginInfo(final String user, final String pswd) {
        // The progress load bar given.
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String sendingURL = RequestURL.URL + "login";
        StringRequest loginRequest = new StringRequest(Request.Method.POST, sendingURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {
                        // No response needed for this.
                        JsonElement jsonElement = new JsonParser().parse(res);
                        JsonObject jobject = jsonElement.getAsJsonObject();
                        //jobject = jobject.getAsJsonObject("items");
                        boolean loginSuccess = jobject.get("status").getAsBoolean();

                        System.out.println("LOG IN IS: " + jobject.get("status").getAsString());
                        String userID="";
                        if (loginSuccess) {
                            userID = jobject.get("id").getAsString();
                            if (progressDialog != null) progressDialog.cancel();
                            onLoginSuccess(userID);
                        }
                        else {
                            System.out.println("Canceling progress dialogue");
                            if (progressDialog != null) progressDialog.cancel();
                            onLoginFailed();
                        }
                        System.out.println("USER ID IS : " + userID);

                        loadSuccess = true;


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

                //Adding parameters
                params.put(USERNAME, user);
                params.put(PASSWORD, pswd);

                //returning parameters
                return params;
            }
        };

        loginRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(loginRequest);


        // Initiates time out in case of server waiting error
        initiateTimeout(progressDialog);
    }

    /**
     * This is an error handling case when the server does not respond for 3 seconds.
     * @param progressDialog
     */
    public void initiateTimeout(final ProgressDialog progressDialog) {
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // If load was unsuccessful from the server side, then dismiss the progressDialog.
                        if (!loadSuccess) {
                            // On complete call either onLoginSuccess or onLoginFailed
                            onLoginFailed();
                            // onLoginFailed();
                            if (progressDialog != null) progressDialog.cancel();
                        }
                    }
                }, 5000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                //this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess(String userID) {
        loginButton.setEnabled(true);
        UserSharedPreferences.setUserID(getApplicationContext(),userID);
        Intent contentPage = new Intent(getApplicationContext(), IntroPageActivity.class);
        contentPage.putExtra(StaticNames.USER_ID, userID);
        startActivity(contentPage);
        finish();


    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String username = userNameText.getText().toString();
        String password = passwordText.getText().toString();

        if (username.isEmpty()) {
            userNameText.setError("enter a valid email address");
            valid = false;
        } else {
            userNameText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }
}
