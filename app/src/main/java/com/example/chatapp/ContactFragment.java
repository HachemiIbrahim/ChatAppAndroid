package com.example.chatapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.TaskExecutor;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;


public class ContactFragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference ContactsReference;
    private FirebaseAuth auth;
    private String currentUid;
    public ContactFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        recyclerView = view.findViewById(R.id.contacts_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext() , LinearLayoutManager.VERTICAL));
        ContactsReference = FirebaseDatabase.getInstance().getReference().child("Contacts");
        auth = FirebaseAuth.getInstance();
        currentUid = auth.getCurrentUser().getUid();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<User >()
                .setQuery(ContactsReference.child(currentUid) , User.class).build();

        FirebaseRecyclerAdapter<User , ContactsViewHolder> adapter =
                new FirebaseRecyclerAdapter<User, ContactsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ContactsViewHolder holder, int position, @NonNull User model) {
                        String id = getRef(position).getKey();

                        FirebaseDatabase.getInstance().getReference().child("Users").child(id)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        holder.Username.setText(snapshot.child("Username").getValue().toString());
                                        String url = snapshot.child("imageURL").getValue().toString();
                                        if(url.equals("default")){
                                            holder.ProfilePicture.setImageResource(R.drawable.ic_profile);
                                        }else {
                                            Picasso.get().load(url).into(holder.ProfilePicture);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }

                    @NonNull
                    @Override
                    public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item , parent , false);
                        ContactsViewHolder viewHolder = new ContactsViewHolder(view);
                        return viewHolder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    public static class ContactsViewHolder extends RecyclerView.ViewHolder{

        CircleImageView ProfilePicture;
        TextView Username;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);

            ProfilePicture = itemView.findViewById(R.id.profile_picture);
            Username = itemView.findViewById(R.id.username);
        }
    }

}