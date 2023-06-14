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

import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class FriendActivity extends AppCompatActivity {

    private CircleImageView ProfilePicture;
    private TextView Username;
    private TextView FullName;
    private Button SendMessage;
    private DatabaseReference reference;
    private String uid;
    private String currentUserid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        ProfilePicture = findViewById(R.id.profile_picture);
        Username = findViewById(R.id.username);
        FullName = findViewById(R.id.full_name);
        SendMessage = findViewById(R.id.send_message);
        uid = getIntent().getExtras().get("Uid").toString();
        currentUserid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                Username.setText(user.getUsername());
                FullName.setText(user.getName());
                if(user.getImageURL().equals("default")){
                    ProfilePicture.setImageResource(R.drawable.ic_profile);
                }else {
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
                SendRequest();
            }
        });
    }

    private void SendRequest() {
        FirebaseDatabase.getInstance().getReference().child("Chat Requests").child(currentUserid)
                .child(uid).child("request type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FirebaseDatabase.getInstance().getReference().child("Chat Requests").
                               child(uid).child(currentUserid).child("request type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
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