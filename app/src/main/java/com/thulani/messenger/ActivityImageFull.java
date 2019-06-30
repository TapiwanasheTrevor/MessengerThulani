package com.thulani.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.thulani.messenger.adapter.ChatDetailsListAdapter;
import com.thulani.messenger.data.Constant;
import com.thulani.messenger.model.ChatsDetails;
import com.thulani.messenger.model.Group;
import com.thulani.messenger.model.GroupDetails;

import java.io.File;
import java.io.IOException;

public class ActivityImageFull extends AppCompatActivity {

    ChatsDetails chatsDetails;
    GroupDetails groupDetails;
    FloatingActionButton fab;
    private StorageReference storageRef;
    String stringurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_full);

        fab = findViewById(R.id.fab);

        if (getIntent().getIntExtra("threadType", 0) == 1) {

            Gson gson = new Gson();
            chatsDetails = gson.fromJson(getIntent().getStringExtra("chat"), ChatsDetails.class);
            storageRef = FirebaseStorage.getInstance().getReference();
            stringurl = chatsDetails.getContent();

            ImageView mContentView = findViewById(R.id.fullscreen_content);

            Picasso.with(this).load(chatsDetails.getContent()).fit().into(mContentView);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadFile(chatsDetails.getContent());
                    Toast.makeText(getBaseContext(), "Downloading to local storage...", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Gson gson = new Gson();
            groupDetails = gson.fromJson(getIntent().getStringExtra("chat"), GroupDetails.class);
            storageRef = FirebaseStorage.getInstance().getReference();

            stringurl = groupDetails.getContent();

            ImageView mContentView = findViewById(R.id.fullscreen_content);

            Picasso.with(this).load(groupDetails.getContent()).fit().into(mContentView);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadFile(groupDetails.getContent());
                    Toast.makeText(getBaseContext(), "Downloading to local storage...", Toast.LENGTH_LONG).show();
                }
            });
        }

        initToolbar();
    }

    public void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Shared Media");
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
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void downloadFile(String path) {
        try {
            StorageReference fileRef = FirebaseStorage.getInstance().getReferenceFromUrl(path);
            File localFile = File.createTempFile("MESSENGER/images", "jpg");

            fileRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Log.e("DOWNLOAD SUCCESS", "FILE DOWN");
                    if (isExternalStorageWritable()) {
                        File localFile = getPublicAlbumStorageDir("MESSENGER/" + stringurl);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e("DOWNLOAD FAILED", "FILE FAILED");
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


    public File getPublicAlbumStorageDir(String albumName) {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("STORAGE MESSAGE", "Directory not created");
        }
        return file;
    }


}
