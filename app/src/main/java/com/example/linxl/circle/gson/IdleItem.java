package com.example.linxl.circle.gson;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Linxl on 2018/11/24.
 */

public class IdleItem implements Serializable {

    private String idleId;
    private String userImg;
    private String userName;
    private String userId;
    private String sendTime;
    private String content;
    private String idleName;
    private String price;
    private boolean flag;
    private int reportNum;
    private List<String> idleImgs;

    public IdleItem(String idleId, String userImg, String userName, String userId, String sendTime, String content, String idleName, String price, boolean flag, int reportNum, List<String> idleImgs) {
        this.idleId = idleId;
        this.userImg = userImg;
        this.userName = userName;
        this.userId = userId;
        this.sendTime = sendTime;
        this.content = content;
        this.idleName = idleName;
        this.price = price;
        this.flag = flag;
        this.reportNum = reportNum;
        this.idleImgs = idleImgs;
    }

    public String getIdleId() {
        return idleId;
    }

    public void setIdleId(String idleId) {
        this.idleId = idleId;
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

    public String getIdleName() {
        return idleName;
    }

    public void setIdleName(String idleName) {
        this.idleName = idleName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public List<String> getIdleImgs() {
        return idleImgs;
    }

    public void setIdleImgs(List<String> idleImgs) {
        this.idleImgs = idleImgs;
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

    @Override
    public String toString() {
        return "IdleItem{" +
                "idleId='" + idleId + '\'' +
                ", userImg='" + userImg + '\'' +
                ", userName='" + userName + '\'' +
                ", userId='" + userId + '\'' +
                ", sendTime='" + sendTime + '\'' +
                ", content='" + content + '\'' +
                ", idleName='" + idleName + '\'' +
                ", price='" + price + '\'' +
                ", flag=" + flag +
                ", reportNum=" + reportNum +
                ", idleImgs=" + idleImgs +
                '}';
    }
}
