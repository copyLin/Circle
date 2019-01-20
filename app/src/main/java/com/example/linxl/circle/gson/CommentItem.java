package com.example.linxl.circle.gson;

/**
 * Created by Linxl on 2018/11/27.
 */

public class CommentItem {

    private String commentId;
    private String userId;
    private String userImg;
    private String userName;
    private String commentContent;
    private String commentTime;
    private String commentTip;

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }

    public String getCommentTip() {
        return commentTip;
    }

    public void setCommentTip(String commentTip) {
        this.commentTip = commentTip;
    }
}
