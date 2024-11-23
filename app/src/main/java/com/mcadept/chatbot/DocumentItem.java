package com.mcadept.chatbot;

public class DocumentItem {
    private String id;
    private String name;
    private String url;
    private long uploadTime;
    private long size;

    public DocumentItem(String id, String name, String url, long uploadTime, long size) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.uploadTime = uploadTime;
        this.size = size;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public long getUploadTime() {
        return uploadTime;
    }

    public long getSize() {
        return size;
    }
}
