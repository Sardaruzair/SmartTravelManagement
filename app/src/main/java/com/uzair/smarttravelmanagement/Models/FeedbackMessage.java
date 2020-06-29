package com.uzair.smarttravelmanagement.Models;

public class FeedbackMessage {
    String uid, name, mobile, message, timestamp;

    public FeedbackMessage() {
    }

    public FeedbackMessage(String uid, String name, String mobile, String message, String timestamp) {
        this.uid = uid;
        this.name = name;
        this.mobile = mobile;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
