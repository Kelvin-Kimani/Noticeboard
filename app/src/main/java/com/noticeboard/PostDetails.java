package com.noticeboard;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties

public class PostDetails {

    private String title, content;
    private @ServerTimestamp Date timestamp;

    public PostDetails(String title, String content) {
        this.title = title;
        this.content = content;

    }

    public PostDetails() {
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
