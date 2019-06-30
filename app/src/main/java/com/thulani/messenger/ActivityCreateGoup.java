package com.thulani.messenger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thulani.messenger.data.Constant;
import com.thulani.messenger.model.Friend;
import com.thulani.messenger.model.Group;
import com.thulani.messenger.utils.Preferences;

import java.util.ArrayList;
import java.util.List;

public class ActivityCreateGoup extends AppCompatActivity {

    EditText input_groupname;
    Button btn_create;
    private DatabaseReference mDatabase;
    ArrayList<Friend> friends = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_goup);

        input_groupname = findViewById(R.id.input_groupname);
        btn_create = findViewById(R.id.btn_create);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //get myself and add to the group
        Friend myself = new Friend();
        myself.setNumber(Preferences.getNumber(getBaseContext()));
        myself.setId(0);
        myself.setName(Preferences.getUserName(getBaseContext()));
        myself.setPhoto("https://api.adorable.io/avatars/285/abott@adorable.png");

        friends.add(myself);


        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input_groupname.getText().toString().equals("") || input_groupname.getText().toString().length() < 3) {
                    input_groupname.setError("Group name missing or too short");
                } else {
                    Group group = new Group();
                    group.setDate(Constant.formatTime(System.currentTimeMillis()));
                    group.setId(System.currentTimeMillis());
                    group.setSnippet(Preferences.getUserName(getBaseContext()) + " Created this group");
                    group.setName(input_groupname.getText().toString());
                    group.setPhoto("https://api.adorable.io/avatars/285/abott@adorable.png");
                    group.setFriends(friends);

                    //save the node to firebase
                    mDatabase.child(Preferences.getNumber(getBaseContext())).child("groups").child(String.valueOf(group.getId())).setValue(group);

                    // mDatabase.child("groups").push().setValue(group);

                    //go to the groups page
                    finish();
                }
            }
        });
    }
}
