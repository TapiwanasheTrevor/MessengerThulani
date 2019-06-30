package com.thulani.messenger;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.thulani.messenger.adapter.ChatDetailsListAdapter;
import com.thulani.messenger.adapter.ImageAdapter;
import com.thulani.messenger.data.Constant;
import com.thulani.messenger.data.Tools;
import com.thulani.messenger.fragment.FragmentActivity;
import com.thulani.messenger.model.Chat;
import com.thulani.messenger.model.ChatsDetails;
import com.thulani.messenger.model.Friend;
import com.thulani.messenger.utils.Preferences;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.models.sort.SortingTypes;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class ActivityChatDetails extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    public static String KEY_FRIEND = "com.app.sample.messenger.FRIEND";
    public static String KEY_SNIPPET = "com.app.sample.messenger.SNIPPET";

    public static final int RC_PHOTO_PICKER_PERM = 123;
    public static final int RC_FILE_PICKER_PERM = 321;
    private static final int CUSTOM_REQUEST_CODE = 532;
    private int MAX_ATTACHMENT_COUNT = 10;
    private ArrayList<String> photoPaths = new ArrayList<>();
    private ArrayList<String> docPaths = new ArrayList<>();

    // give preparation animation activity transition
    public static void navigate(AppCompatActivity activity, View transitionImage, Friend obj, String snippet) {
        Intent intent = new Intent(activity, ActivityChatDetails.class);
        intent.putExtra(KEY_FRIEND, obj);
        intent.putExtra(KEY_SNIPPET, snippet);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionImage, KEY_FRIEND);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    private Button btn_send;
    private EditText et_content;
    public static ChatDetailsListAdapter adapter;

    private RecyclerView listview;
    private ActionBar actionBar;
    private Friend friend;
    private List<ChatsDetails> items = new ArrayList<>();
    private View parent_view;
    private DatabaseReference mDatabase;
    private StorageReference storageRef;
    String Storage_Path = "MEDIA/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_details);
        parent_view = findViewById(android.R.id.content);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storageRef = FirebaseStorage.getInstance().getReference();

        iniComponen();

        listview.setLayoutManager(new LinearLayoutManager(this));
        listview.setHasFixedSize(true);


        //set my presence system here
        DatabaseReference userStatus = FirebaseDatabase.getInstance().getReference().child(Preferences.getNumber(this)).child("presence");
        userStatus.setValue("Online");

        long millis = System.currentTimeMillis();

        userStatus.onDisconnect().setValue(millis);

        // animation transition
        ViewCompat.setTransitionName(parent_view, KEY_FRIEND);

        // initialize conversation data
        Intent intent = getIntent();
        friend = (Friend) intent.getExtras().getSerializable(KEY_FRIEND);

        initToolbar();

        Query query = FirebaseDatabase.getInstance().getReference().child(String.valueOf(Preferences.getUserId(this))).child("conversations").child(friend.getNumber());

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                items.add(dataSnapshot.getValue(ChatsDetails.class));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        adapter = new ChatDetailsListAdapter(this, items);
        adapter.setOnItemClickListener(new ChatDetailsListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, ChatsDetails obj, int position) {
                Log.e("ON ITEM CLICKED", obj.getContent());
                if (obj.getMtype() == 1) {
                    Gson gson = new Gson();
                    String pic = gson.toJson(obj);
                    Intent i = new Intent(ActivityChatDetails.this, ActivityImageFull.class);
                    i.putExtra("chat", pic);
                    startActivity(i);
                }
            }
        });

        listview.setAdapter(adapter);
        listview.requestFocus();
        registerForContextMenu(listview);

        // for system bar in lollipop
        Tools.systemBarLolipop(this);
    }


    public void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(friend.getName());

        //is he online ... set the value here
        DatabaseReference userStatus = FirebaseDatabase.getInstance().getReference().child(friend.getNumber()).child("presence");

        userStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    if (!dataSnapshot.getValue().toString().equals("Online")) {
                        String seen = "Last seen " + Constant.formatTime(Long.valueOf(dataSnapshot.getValue().toString()));
                        actionBar.setSubtitle(seen);
                    } else {
                        actionBar.setSubtitle(dataSnapshot.getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void bindView() {
        try {
            adapter.notifyDataSetChanged();
            // listview.setSelectionFromTop(adapter.getCount(), 0);
        } catch (Exception e) {

        }
    }

    public void iniComponen() {
        listview = findViewById(R.id.listview);
        btn_send = findViewById(R.id.btn_send);
        et_content = findViewById(R.id.text_content);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.child(Preferences.getUserId(getBaseContext())).child("conversations").child(String.valueOf(friend.getNumber())).push().setValue(new ChatsDetails(items.size(), Constant.formatTime(System.currentTimeMillis()), friend, et_content.getText().toString(), true, 0, ""));
                mDatabase.child(friend.getNumber()).child("conversations").child(Preferences.getUserId(getBaseContext())).push().setValue(new ChatsDetails(items.size(), Constant.formatTime(System.currentTimeMillis()), friend, et_content.getText().toString(), false, 0, ""));

                Chat chat = new Chat(0, "Now", true, friend, et_content.getText().toString());

                //my threads
                mDatabase.child(Preferences.getUserId(getBaseContext())).child("threads").child(String.valueOf(friend.getNumber())).setValue(chat);

                //my profile has to be the object i put in this chat
                Friend myself = new Friend();
                myself.setNumber(Preferences.getNumber(getBaseContext()));
                myself.setId(0);
                myself.setName(Preferences.getUserName(getBaseContext()));
                myself.setPhoto("https://api.adorable.io/avatars/285/abott@adorable.png");

                Chat chat2 = new Chat(0, "Now", true, myself, et_content.getText().toString());

                //my friend's threads
                mDatabase.child(friend.getNumber()).child("threads").child(String.valueOf(Preferences.getUserId(getBaseContext()))).setValue(chat2);

                et_content.setText("");
                bindView();
                hideKeyboard();
            }
        });

        et_content.addTextChangedListener(contentWatcher);
        if (et_content.length() == 0) {
            btn_send.setEnabled(false);
        }
        hideKeyboard();
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private TextWatcher contentWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable etd) {
            if (etd.toString().trim().length() == 0) {
                btn_send.setEnabled(false);
            } else {
                btn_send.setEnabled(true);
            }
            //draft.setContent(etd.toString());
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_details, menu);
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
            case R.id.action_attach:
                pickPhotoClicked();
                return true;
            default:
                Snackbar.make(parent_view, item.getTitle() + " Clicked ", Snackbar.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
        }
    }

    public void uploadFile(String path) {
        Uri file = Uri.fromFile(new File(path));

        StorageReference picturesRef = storageRef.child(Storage_Path + System.currentTimeMillis() + "." + GetFileExtension(file));

        picturesRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl();
                        final String metadata = downloadUrl.toString();
                        downloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String path = uri.toString();
                                Log.e("DOWNLOAD PATH", metadata);
                                mDatabase.child(Preferences.getUserId(getBaseContext())).child("conversations").child(String.valueOf(friend.getNumber())).push().setValue(new ChatsDetails(items.size(), Constant.formatTime(System.currentTimeMillis()), friend, path, true, 1, metadata));
                                mDatabase.child(friend.getNumber()).child("conversations").child(Preferences.getUserId(getBaseContext())).push().setValue(new ChatsDetails(items.size(), Constant.formatTime(System.currentTimeMillis()), friend, path, false, 1, metadata));

                                Chat chat = new Chat(0, "Now", true, friend, "Image");

                                //my threads
                                mDatabase.child(Preferences.getUserId(getBaseContext())).child("threads").child(String.valueOf(friend.getNumber())).setValue(chat);

                                //my profile has to be the object i put in this chat
                                Friend myself = new Friend();
                                myself.setNumber(Preferences.getNumber(getBaseContext()));
                                myself.setId(0);
                                myself.setName(Preferences.getUserName(getBaseContext()));
                                myself.setPhoto("https://api.adorable.io/avatars/285/abott@adorable.png");

                                Chat chat2 = new Chat(0, "Now", true, myself, "Image");

                                //my friend's threads
                                mDatabase.child(friend.getNumber()).child("threads").child(String.valueOf(Preferences.getUserId(getBaseContext()))).setValue(chat2);

                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                    }
                });
    }

    @AfterPermissionGranted(RC_PHOTO_PICKER_PERM)
    public void pickPhotoClicked() {
        if (EasyPermissions.hasPermissions(this, FilePickerConst.PERMISSIONS_FILE_PICKER)) {
            onPickPhoto();
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_photo_picker),
                    RC_PHOTO_PICKER_PERM, FilePickerConst.PERMISSIONS_FILE_PICKER);
        }
    }

    @AfterPermissionGranted(RC_FILE_PICKER_PERM)
    public void pickDocClicked() {
        if (EasyPermissions.hasPermissions(this, FilePickerConst.PERMISSIONS_FILE_PICKER)) {
            onPickDoc();
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_doc_picker),
                    RC_FILE_PICKER_PERM, FilePickerConst.PERMISSIONS_FILE_PICKER);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CUSTOM_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    photoPaths = new ArrayList<>();
                    photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
                }
                break;

            case FilePickerConst.REQUEST_CODE_DOC:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    docPaths = new ArrayList<>();
                    docPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                }
                break;
        }

        addThemToView(photoPaths, docPaths);
    }

    private void addThemToView(ArrayList<String> imagePaths, ArrayList<String> docPaths) {
        ArrayList<String> filePaths = new ArrayList<>();
        if (imagePaths != null) filePaths.addAll(imagePaths);

        if (docPaths != null) filePaths.addAll(docPaths);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        if (recyclerView != null) {
            StaggeredGridLayoutManager layoutManager =
                    new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL);
            layoutManager.setGapStrategy(
                    StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
            recyclerView.setLayoutManager(layoutManager);

            ImageAdapter imageAdapter = new ImageAdapter(this, filePaths);

            recyclerView.setAdapter(imageAdapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }

        Toast.makeText(this, "Num of files selected: " + filePaths.size(), Toast.LENGTH_SHORT).show();

        for (int i = 0; i < filePaths.size(); i++) {
            uploadFile(filePaths.get(i));
        }

    }

    public void onPickPhoto() {
        int maxCount = MAX_ATTACHMENT_COUNT - docPaths.size();
        if ((docPaths.size() + photoPaths.size()) == MAX_ATTACHMENT_COUNT) {
            Toast.makeText(this, "Cannot select more than " + MAX_ATTACHMENT_COUNT + " items",
                    Toast.LENGTH_SHORT).show();
        } else {
            FilePickerBuilder.getInstance()
                    .setMaxCount(maxCount)
                    .setSelectedFiles(photoPaths)
                    .setActivityTheme(R.style.FilePickerTheme)
                    .setActivityTitle("Please select media")
                    .enableVideoPicker(true)
                    .enableCameraSupport(true)
                    .showGifs(true)
                    .showFolderView(true)
                    .enableSelectAll(false)
                    .enableImagePicker(true)
                    .setCameraPlaceholder(R.drawable.custom_camera)
                    .withOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                    .pickPhoto(this, CUSTOM_REQUEST_CODE);
        }
    }

    public void onPickDoc() {
        String[] zips = {".zip", ".rar"};
        String[] pdfs = {".pdf"};
        int maxCount = MAX_ATTACHMENT_COUNT - photoPaths.size();
        if ((docPaths.size() + photoPaths.size()) == MAX_ATTACHMENT_COUNT) {
            Toast.makeText(this, "Cannot select more than " + MAX_ATTACHMENT_COUNT + " items",
                    Toast.LENGTH_SHORT).show();
        } else {
            FilePickerBuilder.getInstance()
                    .setMaxCount(maxCount)
                    .setSelectedFiles(docPaths)
                    .setActivityTheme(R.style.FilePickerTheme)
                    .setActivityTitle("Please select doc")
                    .addFileSupport("ZIP", zips)
                    .addFileSupport("PDF", pdfs, R.drawable.pdf_blue)
                    .enableDocSupport(true)
                    .enableSelectAll(true)
                    .sortDocumentsBy(SortingTypes.name)
                    .withOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                    .pickFile(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    public void onOpenFragmentClicked(View view) {
        Intent intent = new Intent(this, FragmentActivity.class);
        startActivity(intent);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }
}
