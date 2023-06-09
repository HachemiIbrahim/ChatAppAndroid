package com.example.chatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

public class GroupChatActivity extends AppCompatActivity {

    private EditText Message;
    private ImageView Send;
    private ScrollView scrollView;
    private TextView displayMessages;
    private String currentGroupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        currentGroupName = getIntent().getExtras().get("GroupName").toString();

        getSupportActionBar().setTitle(currentGroupName);

        Message = findViewById(R.id.message);
        Send = findViewById(R.id.send_message);
        scrollView = findViewById(R.id.scroll_view);
        displayMessages = findViewById(R.id.group_chat_text_display);


    }
}