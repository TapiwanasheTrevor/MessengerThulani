package com.thulani.messenger;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;
import com.thulani.messenger.data.Tools;
import com.thulani.messenger.model.Friend;
import com.thulani.messenger.widget.CircleTransform;

public class ActivityCalling extends AppCompatActivity {

    public static String KEY_FRIEND = "com.app.sample.messenger.FRIEND";

    private Friend friend;
    private View parent_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);
        parent_view = findViewById(android.R.id.content);

        // initialize conversation data
        Intent intent = getIntent();
        friend = (Friend) intent.getExtras().getSerializable(KEY_FRIEND);

        initComponent();
        initToolbar();

        // for system bar in lollipop
        Tools.systemBarLolipop(this);
    }

    private void initComponent() {
        ((TextView) findViewById(R.id.name)).setText(friend.getName());
        ImageView image = (ImageView) findViewById(R.id.image);
        Picasso.with(this).load(friend.getPhoto()).resize(200, 200).transform(new CircleTransform()).into(image);
    }

    public void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void actionClick(View v) {
        // Handle item selection
        switch (v.getId()) {
            case R.id.bt_end:
                onBackPressed();
                break;
            case R.id.bt_speaker:
                Snackbar.make(parent_view, "Loud Speaker Clicked ", Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.bt_record:
                Snackbar.make(parent_view, "Record Clicked ", Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.bt_chat:
                Snackbar.make(parent_view, "Chat Clicked ", Snackbar.LENGTH_SHORT).show();
                break;
        }
    }
}
