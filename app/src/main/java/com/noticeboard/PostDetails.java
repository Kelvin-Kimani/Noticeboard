package com.noticeboard;

public class PostDetails {


    private String title, content, pagename, time, pageID, postID, saveValue, postersID, pageAdminID, status;


    public PostDetails(String title, String content, String time, String postID) {
        this.title = title;
        this.content = content;
        this.time = time;
        this.postID = postID;

    }

    public PostDetails(String pagename, String title, String content, String time, String pageID, String postID, String saveValue, String postersID, String pageAdminID, String status) {
        this.pagename = pagename;
        this.title = title;
        this.content = content;
        this.time = time;
        this.pageID = pageID;
        this.postID = postID;
        this.saveValue = saveValue;
        this.postersID = postersID;
        this.pageAdminID = pageAdminID;
        this.status = status;
    }

    public PostDetails() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPageAdminID() {
        return pageAdminID;
    }

    public void setPageAdminID(String pageAdminID) {
        this.pageAdminID = pageAdminID;
    }

    public String getPostersID() {
        return postersID;
    }

    public void setPostersID(String postersID) {
        this.postersID = postersID;
    }

    public String getSaveValue() {
        return saveValue;
    }

    public void setSaveValue(String saveValue) {
        this.saveValue = saveValue;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getPageID() {
        return pageID;
    }

    public void setPageID(String pageID) {
        this.pageID = pageID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPagename() {
        return pagename;
    }

    public void setPagename(String pagename) {
        this.pagename = pagename;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
