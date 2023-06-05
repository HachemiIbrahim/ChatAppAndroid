package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private TabsAccessorAdapter tabsAccessorAdapter;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("ChatApp");

        mViewPager = findViewById(R.id.ViewPager);
        tabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(tabsAccessorAdapter);

        auth = FirebaseAuth.getInstance();

        mTabLayout = findViewById(R.id.TabLayout);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_options , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.logout){
            auth.signOut();
            startActivity(new Intent(MainActivity.this , LoginActivity.class));
        }

        if(item.getItemId() == R.id.find_friends){

        }

        if (item.getItemId() == R.id.profile){

            startActivity(new Intent(MainActivity.this , ProfileActivity.class));
        }
        return true;
    }
}