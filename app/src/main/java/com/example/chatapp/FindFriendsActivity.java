package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import Model.User;
import Model.contacts;
import adapter.UserAdapter;
import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<User> mUsers;
    private UserAdapter userAdapter;
    private DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);
        getSupportActionBar().setTitle("Find Friends");


        recyclerView = findViewById(R.id.find_friends_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this , LinearLayoutManager.VERTICAL));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mUsers = new ArrayList<>();
        userAdapter = new UserAdapter( mUsers,this);
        recyclerView.setAdapter(userAdapter);
       FirebaseDatabase.getInstance().getReference().child("Users")
               .addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                       for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                           User user = dataSnapshot.getValue(User.class);
                           if(!user.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                               mUsers.add(user);
                           }
                       }
                       userAdapter.notifyDataSetChanged();
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {

                   }
               });

    }



}