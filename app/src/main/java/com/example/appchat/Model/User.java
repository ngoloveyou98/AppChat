package com.example.appchat.Model;

public class User {
    private String userid;
    private String username;
    private String imageUrl;
    private String status;
    private String search;
    private long timeOff;
    private String sex;

    public User(String userid, String username, String imageUrl, String status, String search,long timeOff,String sex) {
        this.userid = userid;
        this.username = username;
        this.imageUrl = imageUrl;
        this.status = status;
        this.search = search;
        this.timeOff = timeOff;
        this.sex = sex;
    }

    public User() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public long getTimeOff() {
        return timeOff;
    }

    public void setTimeOff(long timeOff) {
        this.timeOff = timeOff;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
