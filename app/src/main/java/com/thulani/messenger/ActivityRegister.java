package com.thulani.messenger;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.thulani.messenger.model.Friend;
import com.thulani.messenger.utils.JSONParser;
import com.thulani.messenger.utils.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

public class ActivityRegister extends AppCompatActivity {
    EditText txtUsername, txtNumber;
    String username, number, token;
    Button btn_login;
    private PhoneNumberUtil util = null;
    ProgressBar progressbar;
    boolean flag = false;
    int id;
    private DatabaseReference mDatabase;
    Friend myself;
    String mprofile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtUsername = findViewById(R.id.input_username);
        txtNumber = findViewById(R.id.input_number);
        btn_login = findViewById(R.id.btn_login);
        progressbar = findViewById(R.id.progressbar);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        token = "test";

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressbar.setVisibility(View.VISIBLE);

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(txtNumber.getWindowToken(), 0);

                //validate fields
                if (txtUsername.getText().toString().equals("")) {
                    txtUsername.setError("Enter a preferred username");
                } else {
                    username = txtUsername.getText().toString();
                }

                if (util == null) {
                    util = PhoneNumberUtil.createInstance(getApplicationContext());
                }

                try {
                    final Phonenumber.PhoneNumber phoneNumber = util.parse(txtNumber.getText().toString(), "ZW");

                    if (util.isValidNumber(phoneNumber)) {
                        number = util.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);

                        Preferences.savePrefPair(ActivityRegister.this, "id", number);
                        Preferences.savePrefPair(ActivityRegister.this, "username", username);

                        myself = new Friend();
                        myself.setNumber(number);
                        myself.setId(0);
                        myself.setName(username);
                        myself.setPhoto("https://api.adorable.io/avatars/285/abott@adorable.png");

                        //save the user to firebase
                        mDatabase.child(number).child("profile").setValue(myself);

                        Gson gson = new Gson();
                        mprofile = gson.toJson(myself);

                        Intent i = new Intent(ActivityRegister.this, ActivitySetup.class);
                        i.putExtra("mprofile", mprofile);
                        startActivity(i);

                        progressbar.setVisibility(View.INVISIBLE);
                        finish();
                    } else {
                        txtNumber.setError("Number appears invalid");
                    }
                } catch (NumberParseException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
