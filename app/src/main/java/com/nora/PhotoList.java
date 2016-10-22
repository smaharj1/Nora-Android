package com.nora;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PhotoList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);

        String[] photoContent = {"Ola","Ciao","Nora","Namaste"};
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, photoContent);

        ListView photoListView = (ListView) findViewById(R.id.photoList);
        photoListView.setAdapter(adapter);
    }


}
