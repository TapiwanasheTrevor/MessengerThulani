package com.thulani.messenger.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;
import com.thulani.messenger.ActivityChatDetails;
import com.thulani.messenger.ActivityMain;
import com.thulani.messenger.ActivityViewProfile;
import com.thulani.messenger.R;
import com.thulani.messenger.adapter.FriendsListAdapter;
import com.thulani.messenger.data.Constant;
import com.thulani.messenger.model.Friend;
import com.thulani.messenger.utils.JSONParser;
import com.thulani.messenger.utils.Preferences;
import com.thulani.messenger.widget.CircleTransform;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class PageFriendFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private List<Friend> items = new ArrayList<>();
    private FriendsListAdapter mAdapter;
    private DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.page_fragment_friend, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // use a linear layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        Query query = FirebaseDatabase.getInstance().getReference().child(Preferences.getNumber(getActivity())).child("contacts");

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

        mAdapter = new FriendsListAdapter(getActivity(), items);
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new FriendsListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, Friend obj, int position) {
                ActivityChatDetails.navigate((ActivityMain) getActivity(), v.findViewById(R.id.lyt_parent), obj, null);
            }
        });

        mAdapter.setOnMoreButtonClickListener(new FriendsListAdapter.OnMoreButtonClickListener() {
            @Override
            public void onItemClick(View v, Friend obj, int actionId) {
                switch (actionId) {
                    case R.id.action_profile:
                        Intent intent = new Intent(getActivity(), ActivityViewProfile.class);
                        intent.putExtra(ActivityChatDetails.KEY_FRIEND, obj);
                        startActivity(intent);
                        break;
                    case R.id.action_info:
                        dialogPeopoleDetails(obj);
                        break;
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void dialogPeopoleDetails(final Friend friend) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_contact_info);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        ((TextView) dialog.findViewById(R.id.name)).setText(friend.getName());
        ((TextView) dialog.findViewById(R.id.address)).setText(Constant.getBoolean() ? "Active Now" : "Inactive");
        ImageView image = (ImageView) dialog.findViewById(R.id.image);
        Picasso.with(getActivity()).load(friend.getPhoto())
                .resize(200, 200)
                .transform(new CircleTransform())
                .into(image);
        ((Button) dialog.findViewById(R.id.bt_send_message)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ActivityChatDetails.class);
                intent.putExtra(ActivityChatDetails.KEY_FRIEND, friend);
                startActivity(intent);
            }
        });
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

}
