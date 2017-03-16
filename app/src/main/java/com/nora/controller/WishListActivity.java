package com.nora.controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.nora.R;
import com.nora.model.StaticNames;

public class WishListActivity extends AppCompatActivity {
    private String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);

        String extraMessage = getIntent().getStringExtra(StaticNames.USER_ID);

        if (!extraMessage.isEmpty()) {
            userID = extraMessage;
        }

        showList();
    }

    private void showList() {

    }
}
