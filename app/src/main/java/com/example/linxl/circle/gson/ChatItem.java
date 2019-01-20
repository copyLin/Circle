package com.example.linxl.circle.gson;


import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by Linxl on 2018/11/26.
 */

public class ChatItem extends DataSupport implements Serializable {

    private String id;
    private String content;
    private String fromId;
    private String toId;
    private String sendTime;
    private boolean flag;

    public ChatItem() {
    }

    public ChatItem(String id, String content, String fromId, String toId, String sendTime, boolean flag) {
        this.id = id;
        this.content = content;
        this.fromId = fromId;
        this.toId = toId;
        this.sendTime = sendTime;
        this.flag = flag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    @Override
    public String toString() {
        return "ChatItem{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", fromId='" + fromId + '\'' +
                ", toId='" + toId + '\'' +
                ", sendTime='" + sendTime + '\'' +
                ", flag=" + flag +
                '}';
    }
}
