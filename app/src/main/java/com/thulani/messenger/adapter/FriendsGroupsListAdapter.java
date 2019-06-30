package com.thulani.messenger.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;
import com.thulani.messenger.R;
import com.thulani.messenger.model.ChatsDetails;
import com.thulani.messenger.model.Friend;
import com.thulani.messenger.model.Group;
import com.thulani.messenger.utils.Preferences;
import com.thulani.messenger.widget.CircleTransform;

import java.util.ArrayList;
import java.util.List;

public class FriendsGroupsListAdapter extends RecyclerView.Adapter<FriendsGroupsListAdapter.ViewHolder> implements Filterable {

    private List<Friend> original_items = new ArrayList<>();
    private List<Friend> filtered_items = new ArrayList<>();
    private ItemFilter mFilter = new ItemFilter();
    private Group group;

    private Context ctx;

    private boolean clicked = false;
    private OnItemClickListener mOnItemClickListener;
    private OnMoreButtonClickListener onMoreButtonClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Friend obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public interface OnMoreButtonClickListener {
        void onItemClick(View view, Friend obj, int actionId);
    }

    public void setOnMoreButtonClickListener(final OnMoreButtonClickListener onMoreButtonClickListener) {
        this.onMoreButtonClickListener = onMoreButtonClickListener;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public FriendsGroupsListAdapter(Context context, List<Friend> items, Group group) {
        original_items = items;
        filtered_items = items;
        this.group = group;
        ctx = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView name;
        public ImageView image;
        public CheckBox more;
        public LinearLayout lyt_parent;

        public ViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.name);
            image = (ImageView) v.findViewById(R.id.image);
            more = v.findViewById(R.id.more);
            lyt_parent = (LinearLayout) v.findViewById(R.id.lyt_parent);
        }
    }

    public Filter getFilter() {
        return mFilter;
    }


    @Override
    public FriendsGroupsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_friends_groups, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Friend f = filtered_items.get(position);
        holder.name.setText(f.getName());
        Picasso.with(ctx).load(f.getPhoto()).resize(100, 100).transform(new CircleTransform()).into(holder.image);

        //set default based on whether friend is already in the group
        Query query = FirebaseDatabase.getInstance().getReference().child(f.getNumber()).child("groups").child(String.valueOf(group.getId()));

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.hasChildren()) {
                    holder.more.setChecked(true);
                }
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

        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked = true;
                mOnItemClickListener.onItemClick(view, f, position);
                if (group.getFriends().get(0).getNumber().equals(Preferences.getNumber(ctx))) {
                    if (holder.more.isChecked()) {
                        DatabaseReference mDatabase;
                        mDatabase = FirebaseDatabase.getInstance().getReference();
                        mDatabase.child(f.getNumber()).child("groups").child(String.valueOf(group.getId())).setValue(null);
                        holder.more.setChecked(false);
                    } else {
                        DatabaseReference mDatabase;
                        mDatabase = FirebaseDatabase.getInstance().getReference();
                        mDatabase.child(f.getNumber()).child("groups").child(String.valueOf(group.getId())).setValue(group);
                        holder.more.setChecked(true);
                    }
                }
            }
        });

        clicked = false;
    }

    public Friend getItem(int position) {
        return filtered_items.get(position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return filtered_items.size();
    }


    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String query = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();
            final List<Friend> list = original_items;
            final List<Friend> result_list = new ArrayList<>(list.size());

            for (int i = 0; i < list.size(); i++) {
                String str_title = list.get(i).getName();
                if (str_title.toLowerCase().contains(query)) {
                    result_list.add(list.get(i));
                }
            }

            results.values = result_list;
            results.count = result_list.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filtered_items = (List<Friend>) results.values;
            notifyDataSetChanged();
        }
    }

}