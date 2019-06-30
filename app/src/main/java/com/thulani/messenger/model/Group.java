package com.thulani.messenger.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Group implements Serializable {
    private long id;
    private String date;
    private String name;
    private String snippet;
    private String photo;
    private ArrayList<Friend> friends = new ArrayList<>();

    public Group() {
    }

    public Group(long id, String date, String name, String snippet, String photo, ArrayList<Friend> friends) {
        this.id = id;
        this.date = date;
        this.name = name;
        this.snippet = snippet;
        this.photo = photo;
        this.friends = friends;
    }

    public long getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public String getSnippet() {
        return snippet;
    }

    public String getPhoto() {
        return photo;
    }

    public List<Friend> getFriends() {
        return friends;
    }

    public String getMember() {
        if (friends.size() > 100) {
            return "100+ sembers";
        }
        return (friends.size() + 1) + " members";
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setFriends(ArrayList<Friend> friends) {
        this.friends = friends;
    }
}


