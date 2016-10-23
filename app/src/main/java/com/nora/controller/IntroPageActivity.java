package com.nora.controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.nora.R;

public class IntroPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_page);
    }

    public void activateNora(View view) {
//        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
//        alertDialog.setTitle("Head or tail choice");
//        alertDialog.show();

        Intent newIntent = new Intent(getApplicationContext(), PhotoList.class);

        startActivity(newIntent);
    }
}
