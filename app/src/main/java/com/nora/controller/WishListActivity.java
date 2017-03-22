package com.nora.controller;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.nora.R;
import com.nora.model.AnalyticActivity;
import com.nora.model.PhotolistAdapter;
import com.nora.model.StaticNames;

import java.io.File;
import java.util.ArrayList;

import butterknife.InjectView;

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
        File mainDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        ArrayList<File> imagesLoc = new ArrayList<>();

        for (File file : mainDir.listFiles()) {
            if (file.getName().startsWith("nora")) {
                imagesLoc.add(file);
            }
        }

        final File [] imagesArr = new File[imagesLoc.size()];

        imagesLoc.toArray(imagesArr);

        ListView photoList = (ListView) findViewById(R.id.photoListView);

        photoList.setAdapter(new PhotolistAdapter(getApplicationContext(), imagesArr));

        // Set on click listener
        photoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id){
                Intent newIntent = new Intent(getApplicationContext(), ResultActivity.class);
                String filename = imagesArr[position].getAbsolutePath();
                newIntent.putExtra("imageFile", filename);
                newIntent.putExtra("type","photoSearch");
                newIntent.putExtra(StaticNames.USER_ID, userID);

                startActivity(newIntent);
            }
        });

    }
}
