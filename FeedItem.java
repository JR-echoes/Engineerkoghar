package com.engineerkoghar.engineerkoghar;

import java.io.Serializable;

public class FeedItem implements Serializable {

    private String title;
    private String date;
    private String attachmentUrl;
    private Boolean attachState, readState;
    private long id;
    private String content;
    private String url;
    private String label;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean getReadState() {
        return readState;
    }

    public void setReadState(boolean state) {
        this.readState = state;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public boolean getAttachmentState() {
        return attachState;
    }

    public void setAttachmentState(Boolean state) {
        this.attachState = state;
    }

    @Override
    public String toString() {
        return "[ title=" + title + ", date=" + date + "]";
    }
}
