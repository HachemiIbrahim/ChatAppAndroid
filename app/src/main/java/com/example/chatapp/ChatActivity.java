package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class ChatActivity extends AppCompatActivity {

    private EditText Message;
    private ImageView Send;
    private ScrollView scrollView;
    private LinearLayout chatContainer;
    private String currentUserId,uid,userName,currentDate,currentTime;
    private FirebaseAuth auth;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        uid = getIntent().getExtras().get("id").toString();
        Message = findViewById(R.id.message);
        Send = findViewById(R.id.send_message);
        scrollView = findViewById(R.id.scroll_view);
        chatContainer = findViewById(R.id.chatContainer);
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();
        currentUserId = auth.getCurrentUser().getUid();

        reference.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
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
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        reference.child("Private Chats").child(currentUserId).child(uid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){
                    displayAllMessages(snapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){
                    displayAllMessages(snapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void displayAllMessages(DataSnapshot snapshot) {
        Iterator iterator = snapshot.getChildren().iterator();
        while (iterator.hasNext()){
            String Date = ((DataSnapshot) iterator.next()).getValue().toString();
            String Message = ((DataSnapshot) iterator.next()).getValue().toString();
            String Name = ((DataSnapshot) iterator.next()).getValue().toString();
            String Time = ((DataSnapshot) iterator.next()).getValue().toString();

            TextView textView = new TextView(this);
            TextView nameTextView = new TextView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(16,12,16,8);
            textView.setPadding(16,8,16,8);
            textView.setTextColor(Color.BLACK);

            nameTextView.setPadding(16,8,16,8);
            nameTextView.setTextColor(Color.GRAY);
            nameTextView.setTextSize(8);
            nameTextView.setLabelFor(textView.getId());
            if(Name.equals(userName)){
                textView.setBackgroundResource(R.drawable.outgoing_message_background);
                layoutParams.gravity = Gravity.END;
            }else{
                textView.setBackgroundResource(R.drawable.incoming_message_background);
                layoutParams.gravity = Gravity.START;
            }

            textView.setLayoutParams(layoutParams);
            nameTextView.setLayoutParams(layoutParams);
            textView.setText(Message);
            nameTextView.setText(Name);

            chatContainer.addView(nameTextView);
            chatContainer.addView(textView);
            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }

    private void SendMessages() {
        String sentMessage = Message.getText().toString();
        String messageKey = reference.child("Private Chats").child(currentUserId).child(uid).push().getKey();

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
            reference.child("Private Chats").child(currentUserId).child(uid).updateChildren(map);
            reference.child("Private Chats").child(uid).child(currentUserId).updateChildren(map);

            HashMap<String,Object> messageMap = new HashMap<>();
            messageMap.put("Name", userName);
            messageMap.put("Message", sentMessage);
            messageMap.put("Date", currentDate);
            messageMap.put("Time", currentTime);
            reference.child("Private Chats").child(currentUserId).child(uid).child(messageKey).setValue(messageMap);
            reference.child("Private Chats").child(uid).child(currentUserId).child(messageKey).setValue(messageMap);
        }
    }
}