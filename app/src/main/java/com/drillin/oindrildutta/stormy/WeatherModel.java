package com.drillin.oindrildutta.stormy;

public class WeatherModel {

    private double latitude;
    private double longitude;

    public String getCity() {
        return latitude+", "+longitude;
    }
}
