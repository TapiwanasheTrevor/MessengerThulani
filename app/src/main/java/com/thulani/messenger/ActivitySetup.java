package com.thulani.messenger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.gson.Gson;
import com.thulani.messenger.data.Constant;
import com.thulani.messenger.model.ChatsDetails;
import com.thulani.messenger.model.Friend;
import com.thulani.messenger.utils.JSONParser;
import com.thulani.messenger.utils.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

public class ActivitySetup extends AppCompatActivity {

    ProgressBar progressBar;
    ArrayList<Friend> contacts = new ArrayList<>();
    private PhoneNumberUtil util = null;
    private static final int PERMISSION_REQUEST_READ_PHONE_STATE = 1;
    private DatabaseReference mDatabase;
    List<String> numbers = new ArrayList<>();
    Friend myself;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        progressBar = findViewById(R.id.progressbar);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Gson gson = new Gson();
        myself = gson.fromJson(getIntent().getStringExtra("mprofile"), Friend.class);

        Query query = FirebaseDatabase.getInstance().getReference();

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                numbers.add(dataSnapshot.getKey());
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

        getPermissions();
    }

    public void getPermissions() {

        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            int PERMISSION_ALL = 1;
            String[] PERMISSIONS = {
                    android.Manifest.permission.READ_CONTACTS,
                    android.Manifest.permission.WRITE_CONTACTS,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_SMS,
                    android.Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_FINE_LOCATION
            };

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_CONTACTS)) {
                } else {
                    ActivityCompat.requestPermissions(this,
                            PERMISSIONS,
                            PERMISSION_ALL);
                }
            }
        } else {
            new SetUpContactsTask().execute();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_READ_PHONE_STATE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new SetUpContactsTask().execute();
                } else {
                    Toast.makeText(this, "App can not work without permissions", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void getContactList() {
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));

                        Friend friend = new Friend(name, "https://api.adorable.io/avatars/285/abott@adorable.png", phoneNo);
                        friend.setId(0);
                        contacts.add(friend);
                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }

        if (util == null) {
            util = PhoneNumberUtil.createInstance(getApplicationContext());
        }

        for (int i = 0; i < contacts.size(); i++) {
            final Phonenumber.PhoneNumber phoneNumber;
            try {
                phoneNumber = util.parse(contacts.get(i).getNumber(), "ZW");
                String number = util.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);

                //only attach the contact if their number is one of the items in the database already/ inside you use the listener again to pretty much run this same function
                if (numbers.contains(number) && !number.equals(myself.getNumber())) {
                    Friend friend = contacts.get(i);
                    friend.setNumber(number);
                    mDatabase.child(Preferences.getUserId(getBaseContext())).child("contacts").child(number).setValue(friend);
                    mDatabase.child(number).child("contacts").child(myself.getNumber()).setValue(myself);
                }
            } catch (NumberParseException e) {
                e.printStackTrace();
            }
        }
    }

    class SetUpContactsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Nullable
        @Override
        protected Void doInBackground(Void... params) {
            getContactList();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressBar.setVisibility(View.INVISIBLE);
            startActivity(new Intent(ActivitySetup.this, ActivityMain.class));
        }
    }

}
