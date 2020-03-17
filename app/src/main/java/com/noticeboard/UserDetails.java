package com.noticeboard;

public class UserDetails {

    String fullname;
    String level;
    String phonenumber;

    public UserDetails() {
    }

    public UserDetails(String fullname, String level, String phonenumber) {
        this.fullname = fullname;
        this.level = level;
        this.phonenumber = phonenumber;
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
