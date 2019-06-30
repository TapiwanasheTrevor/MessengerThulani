package com.thulani.messenger.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.google.firebase.database.ValueEventListener;
import com.thulani.messenger.ActivityChatDetails;
import com.thulani.messenger.ActivityMain;
import com.thulani.messenger.R;
import com.thulani.messenger.adapter.ChatsListAdapter;
import com.thulani.messenger.data.Constant;
import com.thulani.messenger.model.Chat;
import com.thulani.messenger.model.ChatsDetails;
import com.thulani.messenger.model.Friend;
import com.thulani.messenger.utils.Preferences;

import java.util.ArrayList;
import java.util.List;

public class PageRecentFragment extends Fragment {

    private View view;

    private RecyclerView recyclerView;
    private List<Chat> items = new ArrayList<>();
    private ChatsListAdapter mAdapter;
    private DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.page_fragment_recent, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // use a linear layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);


        mDatabase = FirebaseDatabase.getInstance().getReference().child(Preferences.getUserId(getContext())).child("threads");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                items.clear();
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    Chat chat = messageSnapshot.getValue(Chat.class);
                    items.add(chat);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // specify an adapter (see also next example)
        mAdapter = new ChatsListAdapter(getActivity(), items);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new ChatsListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, Chat obj, int position) {
                ActivityChatDetails.navigate((ActivityMain) getActivity(), v.findViewById(R.id.lyt_parent), obj.getFriend(), obj.getSnippet());
            }
        });

        return view;
    }

    Chat findChat(String number) {
        for (Chat chat : items) {
            if (chat.getFriend().getNumber().equals(number)) {
                return chat;
            }
        }
        return null;
    }

    @Override
    public void onResume() {
        mAdapter.notifyDataSetChanged();
        super.onResume();
    }
}
