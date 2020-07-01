package com.noticeboard;

public class PageDetails {


    String pagename, pageinfo, privacy, pageID, userID, pageimage;

    public PageDetails(String pagename, String pageinfo, String privacy) {
        this.pagename = pagename;
        this.pageinfo = pageinfo;
        this.privacy = privacy;

    }

    public PageDetails(String pagename, String pageinfo, String privacy, String pageID, String userID) {
        this.pagename = pagename;
        this.pageinfo = pageinfo;
        this.privacy = privacy;
        this.pageID = pageID;
        this.userID = userID;

    }

    public PageDetails(String pagename, String pageinfo, String privacy, String pageID, String userID, String pageimage) {
        this.pagename = pagename;
        this.pageinfo = pageinfo;
        this.privacy = privacy;
        this.pageID = pageID;
        this.userID = userID;
        this.pageimage = pageimage;

    }
    public PageDetails() {
    }

    public String getPageimage() {
        return pageimage;
    }

    public void setPageimage(String pageimage) {
        this.pageimage = pageimage;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPageID() {
        return pageID;
    }

    public void setPageID(String pageID) {
        this.pageID = pageID;
    }

    public String getPagename() {
        return pagename;
    }

    public void setPagename(String pagename) {
        this.pagename = pagename;
    }

    public String getPageinfo() {
        return pageinfo;
    }

    public void setPageinfo(String pageinfo) {
        this.pageinfo = pageinfo;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

}
