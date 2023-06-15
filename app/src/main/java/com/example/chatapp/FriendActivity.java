package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.units.qual.C;

import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class FriendActivity extends AppCompatActivity {

    private CircleImageView ProfilePicture;
    private TextView Username;
    private TextView FullName;
    private Button SendMessage,DeclineRequest;
    private DatabaseReference reference;
    private DatabaseReference ChatRequestReference;
    private String uid;
    private String currentUserid;
    private String stat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        ProfilePicture = findViewById(R.id.profile_picture);
        Username = findViewById(R.id.username);
        FullName = findViewById(R.id.full_name);
        SendMessage = findViewById(R.id.send_message);
        DeclineRequest = findViewById(R.id.decline_message);

        uid = getIntent().getExtras().get("Uid").toString();
        currentUserid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        ChatRequestReference = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        stat = "new";

        FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserid).child(uid)
                .child("contact").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            stat = "friend";
                            SendMessage.setVisibility(View.INVISIBLE);
                            DeclineRequest.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        ChatRequestReference.child(currentUserid).child(uid).child("request type").
                addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    stat = snapshot.getValue(String.class);
                    if(stat.equals("sent")) {
                        SendMessage.setText("CANCEL REQUEST");
                    } else if (stat.equals("received")) {
                        SendMessage.setText("Accept Request");
                        DeclineRequest.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                Username.setText(user.getUsername());
                FullName.setText(user.getName());
                if (user.getImageURL().equals("default")) {
                    ProfilePicture.setImageResource(R.drawable.ic_profile);
                } else {
                    Picasso.get().load(user.getImageURL()).into(ProfilePicture);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        SendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stat.equals("new")) {
                    SendRequest();
                }else if(stat.equals("sent")){
                    CancelRequest();
                }else if(stat.equals("received")){
                    AcceptRequest();
                }
            }
        });

        DeclineRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stat.equals("received")){
                    CancelRequest();
                    DeclineRequest.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void AcceptRequest() {
        FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserid).child(uid)
                .child("contact").setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FirebaseDatabase.getInstance().getReference().child("Contacts")
                                .child(uid).child(currentUserid).child("contact").setValue("saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                ChatRequestReference.child(currentUserid).removeValue();
                                                ChatRequestReference.child(uid).removeValue();
                                                SendMessage.setVisibility(View.GONE);
                                                DeclineRequest.setVisibility(View.GONE);
                                                Toast.makeText(FriendActivity.this, "You are Friends now", Toast.LENGTH_SHORT).show();
                                                stat = "friend";
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void CancelRequest() {
        ChatRequestReference.child(currentUserid).removeValue();
        ChatRequestReference.child(uid).removeValue();
        Toast.makeText(this, "Request canceled", Toast.LENGTH_SHORT).show();
        stat = "new";
        SendMessage.setText("SEND REQUEST");

    }


    private void SendRequest() {
        ChatRequestReference.child(currentUserid).child(uid).child("request type").setValue("sent").
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            ChatRequestReference.child(uid).child(currentUserid).child("request type")
                                    .setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(FriendActivity.this, "Request sent", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}