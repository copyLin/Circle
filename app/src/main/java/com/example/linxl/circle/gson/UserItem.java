package com.example.linxl.circle.gson;

/**
 * Created by Linxl on 2019/2/10.
 */

public class UserItem {

    private String userId;
    private String userName;
    private String userImg;
    private String department;
    private String major;
    private String words;

    public UserItem() {
    }

    public UserItem(String userId, String userName, String userImg, String department, String major, String words) {
        this.userId = userId;
        this.userName = userName;
        this.userImg = userImg;
        this.department = department;
        this.major = major;
        this.words = words;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
    }

    @Override
    public String toString() {
        return "UserItem{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", userImg='" + userImg + '\'' +
                ", department='" + department + '\'' +
                ", major='" + major + '\'' +
                ", words='" + words + '\'' +
                '}';
    }
}
