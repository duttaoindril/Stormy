package com.drillin.oindrildutta.stormy;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class WeatherModel {
    //METADATA
    private double latitude;
    private double longitude;
    private String timezone;
    private long time;
    private String alert;
    private String currentSummary;
    private String currentIcon;
    private String minSummary;
    private String minIcon;
    private String hourSummary;
    private String hourIcon;
    private String dailySummary;

    //MAIN WEATHER DATA
    private double precipProbability;
    private double apparentTemperature;
    private double humidity;
    private double windSpeed;
    private double cloudCover;

    //CHILD WEATHER DATA
    private WeatherHour[] hours;
    private WeatherDay[] days;

    public WeatherModel(double latitude,
                        double longitude,
                        String timezone,
                        String alert,
                        long time,
                        String currentSummary,
                        String currentIcon,
                        String minSummary,
                        String minIcon,
                        String hourSummary,
                        String hourIcon,
                        String dailySummary,
                        double precipProbability,
                        double apparentTemperature,
                        double humidity,
                        double windSpeed,
                        double cloudCover,
                        WeatherHour[] hours,
                        WeatherDay[] days) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timezone = timezone;
        this.time = time;
        this.alert = alert;
        this.currentSummary = currentSummary;
        this.currentIcon = currentIcon;
        this.minSummary = minSummary;
        this.minIcon = minIcon;
        this.hourSummary = hourSummary;
        this.hourIcon = hourIcon;
        this.dailySummary = dailySummary;
        this.precipProbability = precipProbability;
        this.apparentTemperature = apparentTemperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.cloudCover = cloudCover;
        this.hours = hours;
        this.days = days;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getTimezone() {
        return timezone;
    }

    public long getTime() {
        return time;
    }

    public String getAlert() {
        return alert;
    }

    public String getCurrentSummary() {
        return currentSummary;
    }

    public String getCurrentIcon() {
        return currentIcon;
    }

    public String getMinSummary() {
        return minSummary;
    }

    public String getMinIcon() {
        return minIcon;
    }

    public String getHourSummary() {
        return hourSummary;
    }

    public String getHourIcon() {
        return hourIcon;
    }

    public String getDailySummary() {
        return dailySummary;
    }

    public int getPrecipProbability() {
        return (int)Math.round(precipProbability*100);
    }

    public int getApparentTemperature() {
        return (int)Math.round(apparentTemperature);
    }

    public int getHumidity() {
        return (int)Math.round(humidity*100);
    }

    public double getWindSpeed() {
        return Math.floor(windSpeed*10)/10;
    }

    public int getCloudCover() {
        return (int)Math.round(cloudCover*100);
    }

    public WeatherHour[] getHours() {
        return hours;
    }

    public WeatherDay[] getDays() {
        return days;
    }

    public String getCity() {
        return "Cupertino, CA";
        //return latitude+", "+longitude;
    }

    public String getFormattedTime() {
        SimpleDateFormat format = new SimpleDateFormat("h:mm a");
        format.setTimeZone(TimeZone.getTimeZone(timezone));
        return format.format(time*1000);
    }

    public int getIconId(String icon) {
        int iconId = R.mipmap.clear_day;
        switch (icon) {
            case "clear-night":
                iconId = R.mipmap.clear_night;
                break;
            case "rain":
                iconId = R.mipmap.rain;
                break;
            case "snow":
                iconId = R.mipmap.snow;
                break;
            case "sleet":
                iconId = R.mipmap.sleet;
                break;
            case "wind":
                iconId = R.mipmap.wind;
                break;
            case "fog":
                iconId = R.mipmap.fog;
                break;
            case "cloudy":
                iconId = R.mipmap.cloudy;
                break;
            case "partly-cloudy-day":
                iconId = R.mipmap.partly_cloudy;
                break;
            case "partly-cloudy-night":
                iconId = R.mipmap.cloudy_night;
                break;
        }
        return iconId;
    }
}