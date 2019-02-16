package com.example.linxl.circle.gson;

import java.io.Serializable;

/**
 * Created by Linxl on 2018/11/24.
 */

public class DeliveryItem implements Serializable {

    private String deliveryId;
    private String userImg;
    private String userName;
    private String userId;
    private String sendTime;
    private String content;
    private String price;
    private boolean flag;
    private int reportNum;

    public DeliveryItem(String deliveryId, String userImg, String userName, String userId, String sendTime, String content, String price, boolean flag, int reportNum) {
        this.deliveryId = deliveryId;
        this.userImg = userImg;
        this.userName = userName;
        this.userId = userId;
        this.sendTime = sendTime;
        this.content = content;
        this.price = price;
        this.flag = flag;
        this.reportNum = reportNum;
    }

    public String getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(String deliveryId) {
        this.deliveryId = deliveryId;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
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
        return "DeliveryItem{" +
                "deliveryId='" + deliveryId + '\'' +
                ", userImg='" + userImg + '\'' +
                ", userName='" + userName + '\'' +
                ", userId='" + userId + '\'' +
                ", sendTime='" + sendTime + '\'' +
                ", content='" + content + '\'' +
                ", price='" + price + '\'' +
                ", flag=" + flag +
                ", reportNum=" + reportNum +
                '}';
    }
}
