package com.example.linxl.circle.gson;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Linxl on 2018/11/24.
 */

public class QuestionItem implements Serializable {

    private String questionId;
    private String userImg;
    private String userName;
    private String userId;
    private String sendTime;
    private String content;
    private boolean flag;
    private int reportNum;
    private List<String> questionImgs;

    public QuestionItem(String questionId, String userImg, String userName, String userId, String sendTime, String content, boolean flag, int reportNum, List<String> questionImgs) {
        this.questionId = questionId;
        this.userImg = userImg;
        this.userName = userName;
        this.userId = userId;
        this.sendTime = sendTime;
        this.content = content;
        this.flag = flag;
        this.reportNum = reportNum;
        this.questionImgs = questionImgs;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
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

    public List<String> getQuestionImgs() {
        return questionImgs;
    }

    public void setQuestionImgs(List<String> questionImgs) {
        this.questionImgs = questionImgs;
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
        return "QuestionItem{" +
                "questionId='" + questionId + '\'' +
                ", userImg='" + userImg + '\'' +
                ", userName='" + userName + '\'' +
                ", userId='" + userId + '\'' +
                ", sendTime='" + sendTime + '\'' +
                ", content='" + content + '\'' +
                ", flag=" + flag +
                ", reportNum=" + reportNum +
                ", questionImgs=" + questionImgs +
                '}';
    }
}
