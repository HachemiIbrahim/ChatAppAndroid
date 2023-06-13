package com.example.chatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class FriendActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        String uid = getIntent().getExtras().get("Uid").toString();
        Toast.makeText(this, "ID" + uid, Toast.LENGTH_SHORT).show();
    }
}