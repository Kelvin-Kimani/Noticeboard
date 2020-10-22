package com.noticeboard;

public class CommentDetails {

    String comment, userID, commentID, time, username, imageURL;

    public CommentDetails() {

    }

    public CommentDetails(String comment, String userID, String commentID, String time, String username, String imageURL) {
        this.comment = comment;
        this.userID = userID;
        this.commentID = commentID;
        this.time = time;
        this.username = username;
        this.imageURL = imageURL;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
