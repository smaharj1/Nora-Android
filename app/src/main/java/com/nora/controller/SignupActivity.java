package com.nora.controller;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import java.util.Hashtable;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    private final String USER_NAME = "username";
    private final String PASSWORD = "password";
    private final String FULL_NAME = "name";

    private RequestQueue requestQueue;


    @InjectView(R.id.input_name) EditText fullName;
    @InjectView(R.id.input_username) EditText emailText;
    @InjectView(R.id.input_password) EditText passwordText;
    @InjectView(R.id.btn_signup) Button signupButton;
    @InjectView(R.id.link_login) TextView loginButton;

    private boolean loadSuccess;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);

        loadSuccess = false;
        requestQueue = Volley.newRequestQueue(getApplicationContext());


        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() {

        if (!validate()) {
            onSignupFailed();
            return;
        }

        signupButton.setEnabled(false);

        String name = fullName.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        // TODO: Implement the volley here
        sendInfoToServer(name, email, password);
    }

    /**
     * Sends the sign up information to the server
     * @param fullName It holds the full name of the user
     * @param username It holds the new username
     * @param password It holds the password
     */
    private void sendInfoToServer(final String fullName,  final String username,
                                  final String password) {

        // Initiates the progress dialog to show the load screen.
        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String sendingURL = RequestURL.URL + "signup";
        StringRequest signupRequest = new StringRequest(Request.Method.POST, sendingURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {
                        // No response needed for this.
                        JsonElement jsonElement = new JsonParser().parse(res);
                        JsonObject jobject = jsonElement.getAsJsonObject();
                        //jobject = jobject.getAsJsonObject("items");
                        boolean signupSuccess = jobject.get("status").getAsBoolean();

                        System.out.println("Status is"+ jobject.get("status").getAsString());
                        if (signupSuccess) {
                            System.out.println("Successfully signed up");
                            progressDialog.cancel();
                            onSignupSuccess();
                        }
                        else {
                            progressDialog.dismiss();
                            onSignupFailed();
                        }
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

                //Adding the parameters and sending the user info to the server.
                params.put(USER_NAME, username);
                params.put(FULL_NAME, fullName);
                params.put(PASSWORD, password);


                //returning parameters
                return params;
            }
        };

        // This helps not make more than one call to the server.
        signupRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(signupRequest);

        // Initiate time out so that if there is something wrong with the server and it doesn't
        // respond, then it gives out the error message.
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
                            onSignupFailed();
                            // onLoginFailed();
                            if (progressDialog != null) progressDialog.cancel();
                        }
                    }
                }, 5000);
    }

    public void onSignupSuccess() {
        signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = fullName.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            fullName.setError("at least 3 characters");
            valid = false;
        } else {
            fullName.setError(null);
        }

        if (email.isEmpty()) {
            emailText.setError("enter a valid email address");
            valid = false;
        } else {
            emailText.setError(null);
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