package com.example.weatherapp.Domains;

public class Hourly {
    private String hour;
    private String temp;
    private String picPath;

    public Hourly(String hour, String temp, String picPath) {
        this.hour = convertTo12HourFormat(hour);
        this.temp = temp;
        this.picPath = picPath;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    // Method to convert time from 24-hour format to 12-hour format
    private String convertTo12HourFormat(String hour24) {
        int hourInt = Integer.parseInt(hour24.substring(0, 2));
        String period = " AM";
        if (hourInt >= 12) {
            period = " PM";
            if (hourInt > 12) {
                hourInt -= 12;
            }
        }
        return String.format("%02d", hourInt) + period;
    }
}
