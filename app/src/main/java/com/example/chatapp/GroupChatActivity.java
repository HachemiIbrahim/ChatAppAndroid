package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class GroupChatActivity extends AppCompatActivity {

    private EditText Message;
    private ImageView Send;
    private ScrollView scrollView;
    private TextView displayMessages;
    private String currentGroupName,userId,userName,currentDate,currentTime;
    private FirebaseAuth auth;
    private DatabaseReference reference;


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

        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();
        userId = auth.getCurrentUser().getUid();

        reference.child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userName = snapshot.child("Name").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessages();
                Message.setText("");
            }
        });
    }

    private void SendMessages() {
        String sentMessage = Message.getText().toString();
        String messageKey = reference.child("Groups").child(currentGroupName).push().getKey();

        if(TextUtils.isEmpty(sentMessage)){
            Toast.makeText(this, "please write something", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show();
            Calendar calendarDate = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd,yyyy");
            currentDate = dateFormat.format(calendarDate.getTime());

            Calendar calendarTime = Calendar.getInstance();
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
            currentTime = timeFormat.format(calendarTime.getTime());

            HashMap<String,Object> map = new HashMap<>();
            reference.child("Groups").child(currentGroupName).updateChildren(map);

            HashMap<String,Object> messageMap = new HashMap<>();
            messageMap.put("Name", userName);
            messageMap.put("Message", sentMessage);
            messageMap.put("Date", currentDate);
            messageMap.put("Time", currentTime);
            reference.child("Groups").child(currentGroupName).child(messageKey).setValue(messageMap);
        }
    }
}