package com.thulani.messenger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.thulani.messenger.adapter.FriendsGroupsListAdapter;
import com.thulani.messenger.adapter.FriendsListAdapter;
import com.thulani.messenger.data.Constant;
import com.thulani.messenger.model.Friend;
import com.thulani.messenger.model.Group;
import com.thulani.messenger.utils.Preferences;

import java.util.ArrayList;
import java.util.List;

public class ActivityAddMembers extends AppCompatActivity {

    Group group;
    private RecyclerView recyclerView;
    private List<Friend> items = new ArrayList<>();
    private FriendsGroupsListAdapter mAdapter;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_members);
        Gson gson = new Gson();
        group = gson.fromJson(getIntent().getStringExtra("group"), Group.class);
        initToolbar();

        recyclerView = findViewById(R.id.recyclerView);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // use a linear layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        Query query = FirebaseDatabase.getInstance().getReference().child(Preferences.getNumber(this)).child("contacts");

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.e("SNAPSHOT", dataSnapshot.toString());
                items.add(dataSnapshot.getValue(Friend.class));
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mAdapter = new FriendsGroupsListAdapter(this, items, group);

        mAdapter.setOnItemClickListener(new FriendsGroupsListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Friend obj, int position) {
                //check if i am admin....
                if (!group.getFriends().get(0).getNumber().equals(Preferences.getNumber(getBaseContext()))) {
                    Toast.makeText(getBaseContext(), "ONLY ADMIN CAN ADD NEW MEMBERS", Toast.LENGTH_LONG).show();
                }
            }
        });

        recyclerView.setAdapter(mAdapter);

    }

    public void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Add Friends to " + group.getName());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * Handle click on action bar
     **/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                Log.e("TAG", "SOMETHING ELSE");
                return super.onOptionsItemSelected(item);
        }
    }


}
