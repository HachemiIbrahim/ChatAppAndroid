package com.example.chatapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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


public class RequestFragment extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseAuth auth;
    private DatabaseReference reference;
    private String uid;
    private String currentUserid;
    private DatabaseReference ChatRequestReference;



    public RequestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_request, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext() , LinearLayoutManager.VERTICAL));
        auth = FirebaseAuth.getInstance();
        ChatRequestReference = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        currentUserid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        uid = auth.getCurrentUser().getUid();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(reference.child(uid) , User.class).build();
       FirebaseRecyclerAdapter<User , RequestsViewHolder> adapter = new FirebaseRecyclerAdapter<User, RequestsViewHolder>(options) {
           @Override
           protected void onBindViewHolder(@NonNull RequestsViewHolder holder, int position, @NonNull User model) {
                holder.itemView.findViewById(R.id.accept).setVisibility(View.VISIBLE);
                holder.itemView.findViewById(R.id.refuse).setVisibility(View.VISIBLE);

               String id = getRef(position).getKey();

                holder.Accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserid).child(id)
                                .child("contact").setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            FirebaseDatabase.getInstance().getReference().child("Contacts")
                                                    .child(id).child(currentUserid).child("contact").setValue("saved")
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()) {
                                                                ChatRequestReference.child(currentUserid).removeValue();
                                                                ChatRequestReference.child(id).removeValue();
                                                                Toast.makeText(getContext(), "You are Friends now", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });
                    }
                });

                holder.Refuse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ChatRequestReference.child(currentUserid).removeValue();
                        ChatRequestReference.child(id).removeValue();
                        Toast.makeText(getContext(), "Request canceled", Toast.LENGTH_SHORT).show();
                    }
                });


                DatabaseReference typeReference = getRef(position).child("request type").getRef();
                typeReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String type = snapshot.getValue().toString();

                            if (type.equals("received")){
                                FirebaseDatabase.getInstance().getReference().child("Users").child(id).
                                        addValueEventListener(new ValueEventListener() {
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
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
           }

           @NonNull
           @Override
           public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
               View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_item , parent , false);
               RequestsViewHolder requestsViewHolder = new RequestsViewHolder(view);
               return requestsViewHolder;
           }
       };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class RequestsViewHolder extends RecyclerView.ViewHolder{

        CircleImageView ProfilePicture;
        TextView Username;
        Button Accept;
        Button Refuse;

        public RequestsViewHolder(@NonNull View itemView) {
            super(itemView);

            ProfilePicture = itemView.findViewById(R.id.profile_picture);
            Username = itemView.findViewById(R.id.username);
            Accept = itemView.findViewById(R.id.accept);
            Refuse = itemView.findViewById(R.id.refuse);
        }
    }

}