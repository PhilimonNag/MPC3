package com.philimonnag.mpc;

public class PrayerModel {
    private String forwho;
    private String request;
    private String uName;
    private String url;

    public PrayerModel(){}
    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getForwho() { return forwho; }

    public void setForwho(String forwho) { this.forwho = forwho;}

    public String getRequest() { return request;}

    public void setRequest(String request) {this.request = request;}

    public PrayerModel(String uName, String url, String forwho, String request) {
        this.forwho = forwho;
        this.request = request;
        this.uName = uName;
        this.url = url;
    }
}
