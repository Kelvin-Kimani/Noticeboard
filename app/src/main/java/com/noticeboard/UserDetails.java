package com.noticeboard;

public class UserDetails {

    String fullname;
    String level;
    String phonenumber, userimage, userID, email;


    public UserDetails(String fullname, String phonenumber, String level,  String email, String userID) {
        this.fullname = fullname;
        this.level = level;
        this.phonenumber = phonenumber;
        this.email = email;
        this.userID = userID;
    }

    public UserDetails(String fullname, String level, String userID, String phonenumber, String userimage, String email) {
        this.fullname = fullname;
        this.level = level;
        this.userID = userID;
        this.phonenumber = phonenumber;
        this.userimage = userimage;
        this.email = email;
    }


    public UserDetails(String userimage) {
        this.userimage = userimage;
    }


    public UserDetails() {
    }

    public UserDetails(String username, String level, String userID, String userImage) {

        this.fullname = username;
        this.level = level;
        this.userID =userID;
        this.userimage = userImage;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserimage() {
        return userimage;
    }

    public void setUserimage(String userimage) {
        this.userimage = userimage;
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
