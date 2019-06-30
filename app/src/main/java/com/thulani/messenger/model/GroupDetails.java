package com.thulani.messenger.model;

import java.io.Serializable;

public class GroupDetails implements Serializable {
    private long id;
    private String date;
    private Friend friend;
    private String content;
    private boolean fromMe;
    private int mtype;
    private String metadata;

    public GroupDetails() {
    }

    public GroupDetails(long id, String date, Friend friend, String content, boolean fromMe, int type, String metadata) {
        this.id = id;
        this.date = date;
        this.friend = friend;
        this.content = content;
        this.fromMe = fromMe;
        this.mtype = type;
        this.metadata = metadata;
    }

    public long getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public Friend getFriend() {
        return friend;
    }

    public String getContent() {
        return content;
    }

    public boolean isFromMe() {
        return fromMe;
    }

    public int getMtype() {
        return mtype;
    }

    public String getMetadata() {
        return metadata;
    }
}