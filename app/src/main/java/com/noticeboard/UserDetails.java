package com.noticeboard;

import android.net.Uri;

public class UserDetails {

    String fullname;
    String level;
    String phonenumber;
    String registration;

    public UserDetails() {
    }

    public UserDetails(String fullname, String phonenumber, String level, String registration) {
        this.fullname = fullname;
        this.level = level;
        this.phonenumber = phonenumber;
        this.registration = registration;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }
}
