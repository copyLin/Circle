package com.example.linxl.circle.gson;

/**
 * Created by Linxl on 2018/11/27.
 */

public class ContactItem {

    private String contactId;
    private String contactName;
    private String contactImg;
    private String recentChat;

    public ContactItem() {
        super();
    }

    public ContactItem(String contactId, String contactName, String contactImg, String recentChat) {
        super();
        this.contactId = contactId;
        this.contactName = contactName;
        this.contactImg = contactImg;
        this.recentChat = recentChat;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactImg() {
        return contactImg;
    }

    public void setContactImg(String contactImg) {
        this.contactImg = contactImg;
    }

    public String getRecentChat() {
        return recentChat;
    }

    public void setRecentChat(String recentChat) {
        this.recentChat = recentChat;
    }


}
