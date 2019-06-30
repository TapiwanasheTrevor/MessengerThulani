package com.thulani.messenger.adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
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
import com.thulani.messenger.R;
import com.thulani.messenger.model.Chat;
import com.thulani.messenger.widget.CircleTransform;

import java.util.ArrayList;
import java.util.List;

public class ChatsListAdapter extends RecyclerView.Adapter<ChatsListAdapter.ViewHolder> implements Filterable {

    private SparseBooleanArray selectedItems;

    private List<Chat> original_items = new ArrayList<>();
    private List<Chat> filtered_items = new ArrayList<>();
    private ItemFilter mFilter = new ItemFilter();

    private Context ctx;

    // for item click listener
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Chat obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    // for item long click listener
    private OnItemLongClickListener mOnItemLongClickListener;
    private boolean clicked = false;

    public interface OnItemLongClickListener {
        void onItemClick(View view, Chat obj, int position);
    }

    public void setOnItemLongClickListener(final OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView title;
        public TextView content;
        public TextView time;
        public ImageView image;
        public LinearLayout lyt_parent;

        public ViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.title);
            content = (TextView) v.findViewById(R.id.content);
            time = (TextView) v.findViewById(R.id.time);
            image = (ImageView) v.findViewById(R.id.image);
            lyt_parent = (LinearLayout) v.findViewById(R.id.lyt_parent);
        }

    }

    public Filter getFilter() {
        return mFilter;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ChatsListAdapter(Context ctx, List<Chat> items) {
        this.ctx = ctx;
        original_items = items;
        filtered_items = items;
        selectedItems = new SparseBooleanArray();
    }

    @Override
    public ChatsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chats, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Chat c = filtered_items.get(position);
        holder.title.setText(c.getFriend().getName());

        holder.time.setText(c.getDate());
        holder.content.setText(c.getSnippet());
        Picasso.with(ctx).load(c.getFriend().getPhoto()).resize(100, 100).transform(new CircleTransform()).into(holder.image);

        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked = true;
                mOnItemClickListener.onItemClick(view, c, position);

            }
        });

        holder.lyt_parent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mOnItemLongClickListener != null) {
                    mOnItemLongClickListener.onItemClick(view, c, position);
                }
                return false;
            }
        });

        holder.lyt_parent.setActivated(selectedItems.get(position, false));
        clicked = false;

    }

    /**
     * For multiple selection
     */
    public void toggleSelection(int pos) {
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
        } else {
            selectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public void removeSelectedItem() {
        List<Chat> items = getSelectedItems();
        filtered_items.removeAll(items);
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Chat> getSelectedItems() {
        List<Chat> items = new ArrayList<>();
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(filtered_items.get(selectedItems.keyAt(i)));
        }
        return items;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return filtered_items.size();
    }

    public void remove(int position) {
        filtered_items.remove(position);
    }

    @Override
    public long getItemId(int position) {
        return filtered_items.get(position).getId();
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String query = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();
            final List<Chat> list = original_items;
            final List<Chat> result_list = new ArrayList<>(list.size());

            for (int i = 0; i < list.size(); i++) {
                String str_title = list.get(i).getFriend().getName();
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
            filtered_items = (List<Chat>) results.values;
            notifyDataSetChanged();
        }

    }
}