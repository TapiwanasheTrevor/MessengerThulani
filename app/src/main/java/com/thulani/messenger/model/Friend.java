package com.thulani.messenger.model;

import java.io.Serializable;

public class Friend implements Serializable {
    private long id;
    private String name;
    private String photo;
    private String number;

    public String getNumber() {
        return number;
    }

    public Friend() {
    }

    public Friend(long id, String name, String photo) {
        this.id = id;
        this.name = name;
        this.photo = photo;
    }

    public Friend(String name, String photo, String number) {
        this.name = name;
        this.photo = photo;
        this.number = number;
    }

    public Friend(String name, String photo) {
        this.name = name;
        this.photo = photo;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
