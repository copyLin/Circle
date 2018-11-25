package com.example.linxl.circle.gson;

import java.util.List;

/**
 * Created by Linxl on 2018/11/24.
 */

public class LostItem {

    private String lostId;
    private String userImg;
    private String userName;
    private String userId;
    private String sendTime;
    private String content;
    private String label;
    private String eventTime;
    private String location;
    private String contact;
    private List<String> LostImgs;

    public LostItem(String lostId, String userImg, String userName, String userId, String sendTime, String content, String label, String eventTime, String location, String contact, List<String> lostImgs) {
        this.lostId = lostId;
        this.userImg = userImg;
        this.userName = userName;
        this.userId = userId;
        this.sendTime = sendTime;
        this.content = content;
        this.label = label;
        this.eventTime = eventTime;
        this.location = location;
        this.contact = contact;
        LostImgs = lostImgs;
    }

    public String getLostId() {
        return lostId;
    }

    public void setLostId(String lostId) {
        this.lostId = lostId;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public List<String> getLostImgs() {
        return LostImgs;
    }

    public void setLostImgs(List<String> lostImgs) {
        LostImgs = lostImgs;
    }

    @Override
    public String toString() {
        return "LostItem{" +
                "userImg='" + userImg + '\'' +
                ", userName='" + userName + '\'' +
                ", userId='" + userId + '\'' +
                ", sendTime='" + sendTime + '\'' +
                ", content='" + content + '\'' +
                ", label='" + label + '\'' +
                ", eventTime='" + eventTime + '\'' +
                ", location='" + location + '\'' +
                ", contact='" + contact + '\'' +
                '}';
    }
}
