package com.example.ashis.entertainmentguide;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

      getFragmentManager().beginTransaction().replace(R.id.content_frame,new SettingsFragment()).commit();
    }
}
