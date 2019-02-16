package com.example.linxl.circle.gson;

/**
 * Created by Linxl on 2019/1/19.
 */

public class CollectionItem {

    private String collectionId;
    private String keyId;
    private String label;
    private String collectionName;
    private String collectionTime;

    public CollectionItem(String collectionId, String keyId, String label, String collectionName,
                          String collectionTime) {
        super();
        this.collectionId = collectionId;
        this.keyId = keyId;
        this.label = label;
        this.collectionName = collectionName;
        this.collectionTime = collectionTime;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
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

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getCollectionTime() {
        return collectionTime;
    }

    public void setCollectionTime(String collectionTime) {
        this.collectionTime = collectionTime;
    }

    @Override
    public String toString() {
        return "CollectionItem [collectionId=" + collectionId + ", keyId=" + keyId + ", label=" + label
                + ", collectionName=" + collectionName + ", collectionTime=" + collectionTime + "]";
    }
}
