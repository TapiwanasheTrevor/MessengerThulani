package com.thulani.messenger.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.thulani.messenger.R;
import com.thulani.messenger.data.Constant;
import com.thulani.messenger.model.GroupDetails;
import com.thulani.messenger.utils.Preferences;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GroupDetailsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<GroupDetails> mMessages;
    private Context ctx;
    private SparseBooleanArray selectedItems;
    private boolean clicked = false;

    public GroupDetailsListAdapter(Context context, List<GroupDetails> messages) {
        super();
        this.ctx = context;
        this.mMessages = messages;
        selectedItems = new SparseBooleanArray();
    }

    @Override
    public long getItemId(int position) {
        return mMessages.get(position).getId();
    }

    @Override
    public int getItemViewType(int position) {
        GroupDetails msg = mMessages.get(position);
        if (msg.getMtype() == 1) {
            if (msg.getFriend().getNumber().equals(Preferences.getNumber(ctx))) {
                return 11;
            } else {
                return 1;
            }
        } else {
            if (msg.getFriend().getNumber().equals(Preferences.getNumber(ctx))) {
                return 0;
            } else {
                return 3;
            }
        }
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 11) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.picture_message_me_group, parent, false);
            return new PictureHolderOther(itemView);
        } else if (viewType == 1) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pic_message_group, parent, false);
            return new PictureHolderMe(itemView);
        } else if (viewType == 0) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat_me_group, parent, false);
            return new TextViewHolderMe(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat_details_group, parent, false);
            return new TextViewHolderOther(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, final int position) {

        final GroupDetails msg = mMessages.get(position);

        if (holder instanceof TextViewHolderOther) {
            TextViewHolderOther holder1 = (TextViewHolderOther) holder;
            holder1.message.setText(msg.getContent());
            holder1.sender.setText(msg.getFriend().getName());
            holder1.time.setText(Constant.formatTime(msg.getId()));
            holder1.lyt_parent.setActivated(selectedItems.get(position, false));
            holder1.message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clicked = true;
                    mOnItemClickListener.onItemClick(v, msg, position);
                }
            });
        } else if (holder instanceof TextViewHolderMe) {
            TextViewHolderMe holder1 = (TextViewHolderMe) holder;
            holder1.message.setText(msg.getContent());
            holder1.time.setText(Constant.formatTime(msg.getId()));
            holder1.lyt_parent.setActivated(selectedItems.get(position, false));
            holder1.message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clicked = true;
                    mOnItemClickListener.onItemClick(v, msg, position);
                }
            });
        } else if (holder instanceof PictureHolderOther) {
            PictureHolderOther pictureHolderOther = (PictureHolderOther) holder;
            pictureHolderOther.time.setText(Constant.formatTime(msg.getId()));
            File f = new File(msg.getContent());
            Picasso.with(ctx).load(msg.getContent()).resize(200, 200).into(((PictureHolderOther) holder).image);
            pictureHolderOther.lyt_parent.setActivated(selectedItems.get(position, false));

            pictureHolderOther.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clicked = true;
                    mOnItemClickListener.onItemClick(v, msg, position);
                }
            });

        } else if (holder instanceof PictureHolderMe) {
            PictureHolderMe pictureHolderOther = (PictureHolderMe) holder;
            pictureHolderOther.sender.setText(msg.getFriend().getName());
            pictureHolderOther.time.setText(Constant.formatTime(msg.getId()));
            File f = new File(msg.getContent());
            Picasso.with(ctx).load(msg.getContent()).resize(200, 200).into(((PictureHolderMe) holder).image);
            pictureHolderOther.lyt_parent.setActivated(selectedItems.get(position, false));
            pictureHolderOther.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clicked = true;
                    mOnItemClickListener.onItemClick(v, msg, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    class TextViewHolderMe extends RecyclerView.ViewHolder {
        TextView time;
        TextView message;
        LinearLayout lyt_parent;
        CardView lyt_thread;
        ImageView image_status;
        //  ProgressBar progressBar;

        public TextViewHolderMe(View convertView) {
            super(convertView);
            time = convertView.findViewById(R.id.text_time);
            message = convertView.findViewById(R.id.text_content);
            lyt_thread = convertView.findViewById(R.id.lyt_thread);
            lyt_parent = convertView.findViewById(R.id.lyt_parent);
            image_status = convertView.findViewById(R.id.image_status);

            //  progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar2);
        }
    }

    class TextViewHolderOther extends RecyclerView.ViewHolder {
        TextView time;
        TextView message;
        TextView sender;
        LinearLayout lyt_parent;
        CardView lyt_thread;
        ImageView image_status;

        public TextViewHolderOther(View convertView) {
            super(convertView);
            time = convertView.findViewById(R.id.text_time);
            message = convertView.findViewById(R.id.text_content);
            sender = convertView.findViewById(R.id.sender);
            lyt_thread = convertView.findViewById(R.id.lyt_thread);
            lyt_parent = convertView.findViewById(R.id.lyt_parent);
            image_status = convertView.findViewById(R.id.image_status);
        }
    }

    class PictureHolderOther extends RecyclerView.ViewHolder {
        TextView time;
        ImageView image;
        LinearLayout lyt_parent;
        CardView lyt_thread;
        ImageView image_status;

        public PictureHolderOther(View convertView) {
            super(convertView);
            time = convertView.findViewById(R.id.text_time);
            image = convertView.findViewById(R.id.text_content);
            lyt_thread = convertView.findViewById(R.id.lyt_thread);
            lyt_parent = convertView.findViewById(R.id.lyt_parent);
            image_status = convertView.findViewById(R.id.image_status);
        }
    }

    class PictureHolderMe extends RecyclerView.ViewHolder {
        TextView time;
        TextView sender;
        ImageView image;
        LinearLayout lyt_parent;
        CardView lyt_thread;
        ImageView image_status;

        public PictureHolderMe(View convertView) {
            super(convertView);
            time = (TextView) convertView.findViewById(R.id.text_time);
            sender = convertView.findViewById(R.id.sender);
            image = (ImageView) convertView.findViewById(R.id.text_content);
            lyt_thread = (CardView) convertView.findViewById(R.id.lyt_thread);
            lyt_parent = (LinearLayout) convertView.findViewById(R.id.lyt_parent);
            image_status = (ImageView) convertView.findViewById(R.id.image_status);
        }
    }

    public void setList(ArrayList<GroupDetails> list) {
        this.mMessages = list;
        notifyDataSetChanged();

    }

    // for item click listener
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, GroupDetails obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    /**
     * remove data item from messageAdapter
     **/
    public void remove(int position) {
        mMessages.remove(position);
    }

    /**
     * add data item to messageAdapter
     **/
    public void add(GroupDetails msg) {
        mMessages.add(msg);
    }

    private static class ViewHolder {
        TextView sender;
        TextView time;
        TextView message;
        LinearLayout lyt_parent;
        CardView lyt_thread;
        ImageView image_status;
    }
}
