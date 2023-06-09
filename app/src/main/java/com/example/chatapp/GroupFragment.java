package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class GroupFragment extends Fragment {

    private View view;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list = new ArrayList<>();
    private DatabaseReference reference;

    public GroupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view =  inflater.inflate(R.layout.fragment_group, container, false);

        listView = view.findViewById(R.id.groups_list);
        arrayAdapter = new ArrayAdapter<String>(getContext() , android.R.layout.simple_list_item_1 , list);
        listView.setAdapter(arrayAdapter);

        reference = FirebaseDatabase.getInstance().getReference();

        reference.child("Groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    list.add(snapshot1.getKey());
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String currentGroupName = parent.getItemAtPosition(position).toString();
                Intent intent = new Intent(getContext() , GroupChatActivity.class);
                intent.putExtra("GroupName",currentGroupName);
                startActivity(intent);
            }
        });

        return view;
    }
}