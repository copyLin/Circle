package com.example.linxl.circle.gson;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Linxl on 2018/11/24.
 */

public class LostItem implements Serializable {

    private String lostId;
    private String userImg;
    private String userName;
    private String userId;
    private String sendTime;
    private String content;
    private String eventTime;
    private String location;
    private String contact;
    private boolean flag;
    private int reportNum;
    private List<String> lostImgs;

    public LostItem(String lostId, String userImg, String userName, String userId, String sendTime, String content, String eventTime, String location, String contact, boolean flag, int reportNum, List<String> lostImgs) {
        this.lostId = lostId;
        this.userImg = userImg;
        this.userName = userName;
        this.userId = userId;
        this.sendTime = sendTime;
        this.content = content;
        this.eventTime = eventTime;
        this.location = location;
        this.contact = contact;
        this.flag = flag;
        this.reportNum = reportNum;
        this.lostImgs = lostImgs;
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

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public int getReportNum() {
        return reportNum;
    }

    public void setReportNum(int reportNum) {
        this.reportNum = reportNum;
    }

    public List<String> getLostImgs() {
        return lostImgs;
    }

    public void setLostImgs(List<String> lostImgs) {
        this.lostImgs = lostImgs;
    }

    @Override
    public String toString() {
        return "LostItem{" +
                "lostId='" + lostId + '\'' +
                ", userImg='" + userImg + '\'' +
                ", userName='" + userName + '\'' +
                ", userId='" + userId + '\'' +
                ", sendTime='" + sendTime + '\'' +
                ", content='" + content + '\'' +
                ", eventTime='" + eventTime + '\'' +
                ", location='" + location + '\'' +
                ", contact='" + contact + '\'' +
                ", flag=" + flag +
                ", reportNum=" + reportNum +
                ", lostImgs=" + lostImgs +
                '}';
    }
}
