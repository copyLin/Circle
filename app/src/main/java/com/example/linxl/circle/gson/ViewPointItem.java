package com.example.linxl.circle.gson;

/**
 * Created by Linxl on 2019/2/10.
 */

public class ViewPointItem {

    private String viewPointId;
    private String userId;
    private String userImg;
    private String userName;
    private String content;
    private String sendTime;
    private String tip;
    private String keyId;
    private String label;

    public ViewPointItem(String viewPointId, String userId, String userImg, String userName, String content,
                         String sendTime, String tip, String keyId, String label) {
        super();
        this.viewPointId = viewPointId;
        this.userId = userId;
        this.userImg = userImg;
        this.userName = userName;
        this.content = content;
        this.sendTime = sendTime;
        this.tip = tip;
        this.keyId = keyId;
        this.label = label;
    }

    public String getViewPointId() {
        return viewPointId;
    }

    public void setViewPointId(String viewPointId) {
        this.viewPointId = viewPointId;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "ViewPointItem [viewPointId=" + viewPointId + ", userId=" + userId + ", userImg=" + userImg
                + ", userName=" + userName + ", content=" + content + ", sendTime=" + sendTime + ", tip=" + tip
                + ", keyId=" + keyId + ", label=" + label + "]";
    }
}
