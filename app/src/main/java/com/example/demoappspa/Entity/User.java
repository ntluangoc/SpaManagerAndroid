package com.example.demoappspa.Entity;

import java.io.Serializable;

public class User implements Serializable {
    private String UID;
    private String name;
    private String email;
    private String phoneNumber;
    private String photoURL;
    public User() {
    }

    public User(String UID, String name, String email, String phoneNumber, String photoURL) {
        this.UID = UID;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.photoURL = photoURL;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "User{" +
                "UID='" + UID + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", photoURL='" + photoURL + '\'' +
                '}';
    }
}
