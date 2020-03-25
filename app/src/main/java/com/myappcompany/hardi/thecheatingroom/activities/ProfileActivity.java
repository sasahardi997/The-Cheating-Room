package com.myappcompany.hardi.thecheatingroom.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.myappcompany.hardi.thecheatingroom.R;

public class ProfileActivity extends AppCompatActivity {

    private TextView mDisplayId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        String user_id = getIntent().getStringExtra("user_id");

        mDisplayId = findViewById(R.id.profile_display_name);
        mDisplayId.setText(user_id);
    }
}
