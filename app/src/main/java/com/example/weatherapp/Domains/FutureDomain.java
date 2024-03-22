package com.example.weatherapp.Domains;

public class FutureDomain {
    private String day;
    private String picPath;
    private String status;
    private String temp;

    public FutureDomain(String day, String picPath, String status, String temp) {
        this.day = day;
        this.picPath = picPath;
        this.status = status;
        this.temp = temp;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTemp() {
        return temp;
    }

    public void settemp(String temp) {
        this.temp = temp;
    }

}
