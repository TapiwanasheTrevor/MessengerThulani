package com.thulani.messenger.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.thulani.messenger.ActivityCalling;
import com.thulani.messenger.ActivityVideo;
import com.thulani.messenger.R;
import com.thulani.messenger.model.Friend;
import com.thulani.messenger.widget.CircleTransform;

import java.util.ArrayList;
import java.util.List;

public class CallListAdapter extends RecyclerView.Adapter<CallListAdapter.ViewHolder> implements Filterable {

    private List<Friend> original_items = new ArrayList<>();
    private List<Friend> filtered_items = new ArrayList<>();
    private ItemFilter mFilter = new ItemFilter();

    private Context ctx;

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Friend obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CallListAdapter(Context context, List<Friend> items) {
        original_items = items;
        filtered_items = items;
        ctx = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView name;
        public ImageView image;
        public ImageView bt_call;
        public ImageView bt_video;
        public LinearLayout lyt_parent;

        public ViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.name);
            image = (ImageView) v.findViewById(R.id.image);
            bt_call = (ImageView) v.findViewById(R.id.bt_call);
            bt_video = (ImageView) v.findViewById(R.id.bt_video);
            lyt_parent = (LinearLayout) v.findViewById(R.id.lyt_parent);
        }
    }

    public Filter getFilter() {
        return mFilter;
    }


    @Override
    public CallListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_call_friends, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Friend c = filtered_items.get(position);
        holder.name.setText(c.getName());
        Picasso.with(ctx).load(c.getPhoto()).resize(100, 100).transform(new CircleTransform()).into(holder.image);

        holder.bt_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ctx, ActivityCalling.class);
                i.putExtra(ActivityCalling.KEY_FRIEND, c);
                ctx.startActivity(i);
            }
        });

        holder.bt_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ctx, ActivityVideo.class);
                i.putExtra(ActivityCalling.KEY_FRIEND, c);
                ctx.startActivity(i);
            }
        });

        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, c, position);
                }
            }
        });
    }

    public Friend getItem(int position){
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