package com.noticeboard;



class UserDetails {

    String fullname,level,regno,id,phonenumber;

    public UserDetails(){

    }
    public UserDetails(String id, String fullname, String phonenumber, String level, String regno){
        this.id = id;
        this.fullname = fullname;
        this.phonenumber = phonenumber;
        this.level = level;
        this.regno = regno;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getRegno() {
        return regno;
    }

    public void setRegno(String regno) {
        this.regno = regno;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }
}
