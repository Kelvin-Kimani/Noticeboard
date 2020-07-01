package com.noticeboard;

import com.google.firebase.Timestamp;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class PostDetails {


    private String title, content, pagename, time, pageID, postID, saveValue;



    public PostDetails(String title, String content, String time, String postID) {
        this.title = title;
        this.content = content;
        this.time = time;
        this.postID = postID;

    }

    public PostDetails(String pagename,String title, String content, String time, String pageID, String postID, String saveValue){
        this.pagename = pagename;
        this.title = title;
        this.content = content;
        this.time = time;
        this.pageID = pageID;
        this.postID = postID;
        this.saveValue = saveValue;
    }

    public PostDetails() {
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
