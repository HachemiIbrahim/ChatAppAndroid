package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

import Model.Group;
import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;


public class GroupFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private DatabaseReference GroupsReference;
    private FirebaseAuth auth;

    public GroupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_group, container, false);

        recyclerView = view.findViewById(R.id.groups_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext() , LinearLayoutManager.VERTICAL));
        GroupsReference = FirebaseDatabase.getInstance().getReference().child("Groups");
        auth = FirebaseAuth.getInstance();

        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Group>()
                .setQuery(GroupsReference , Group.class).build();
        FirebaseRecyclerAdapter<Group , GroupViewHolder > adapter = new FirebaseRecyclerAdapter<Group , GroupViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull GroupViewHolder holder, int position, @NonNull Group model) {
               GroupsReference.addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                       for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                           Group group = dataSnapshot.getValue(Group.class);
                           holder.Username.setText(group.getGroupName());
                           Picasso.get().load(group.getImageURL()).into(holder.ProfilePicture);

                       }
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {

                   }
               });
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String currentGroupName = holder.Username.getText().toString();
                        Intent intent = new Intent(getContext() , GroupChatActivity.class);
                        intent.putExtra("GroupName" , currentGroupName);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item , parent , false);
                GroupViewHolder viewHolder = new GroupViewHolder(view);
                return viewHolder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder{

        CircleImageView ProfilePicture;
        TextView Username;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);

            ProfilePicture = itemView.findViewById(R.id.profile_picture);
            Username = itemView.findViewById(R.id.username);
        }
    }
}