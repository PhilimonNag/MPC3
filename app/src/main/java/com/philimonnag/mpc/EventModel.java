package com.philimonnag.mpc;

public class EventModel {
    String when,where,url,uName;

    public EventModel(){}

    public String getWhen() {
        return when;
    }

    public void setWhen(String when) {
        this.when = when;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public EventModel(String when, String where, String url, String uName) {
        this.when = when;
        this.where = where;
        this.url = url;
        this.uName = uName;
    }
}
