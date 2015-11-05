package com.temp.simplevoipdialer.items;

/**
 * Created by klim-mobile on 07.09.2015.
 */
public class Call {
    private String contactName;
    private String callNumber;
    private String callType;
    private String callDate;
    private String callDuration;
    private String imageLink;
    private String id;

    public Call(String contactName, String callNumber, String callType, String callDate, String callDuration) {
        this.contactName = contactName;
        this.callNumber = callNumber;
        this.callType = callType;
        this.callDate = callDate;
        this.callDuration = callDuration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getContactName() {
        return contactName;
    }

    public String getCallNumber() {
        return callNumber;
    }

    public String getCallType() {
        return callType;
    }

    public String getCallDate() {
        return callDate;
    }

    public String getCallDuration() {
        int secs;
        int mins;
        secs = Integer.parseInt(callDuration);
        mins = secs / 60;
        secs = secs % 60;
        return ""+ String.format("%02d", mins) + ":" + String.format("%02d", secs);
    }
}
