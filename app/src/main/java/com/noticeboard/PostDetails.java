package com.noticeboard;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class PostDetails {


    private String title, content, pagename, time, pageID, postID, saveValue, postersID, pageAdminID, status, search_post;
    private @ServerTimestamp Date timestamp;


    public PostDetails(String title, String content, String time, String postID, Date timestamp) {
        this.title = title;
        this.content = content;
        this.time = time;
        this.postID = postID;
        this.timestamp = timestamp;
        this.search_post = search_post;

    }

    public PostDetails(String pagename, String title, String content, String time, String pageID, String postID, String saveValue, String postersID, String pageAdminID, String status, Date timestamp, String  search_post) {
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
        this.timestamp = timestamp;
        this.search_post = search_post;
    }

    public PostDetails() {
    }

    public String getSearch_post() {
        return search_post;
    }

    public void setSearch_post(String search_post) {
        this.search_post = search_post;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
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
