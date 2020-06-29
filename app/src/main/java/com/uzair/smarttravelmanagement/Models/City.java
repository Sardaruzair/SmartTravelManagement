package com.uzair.smarttravelmanagement.Models;

public class City {
    String CityName;

    public int id;
    public String name;
    public Coord coord;
    public String country;

    public City() {
    }

    public String getCityName() {
        return CityName;
    }

    public void setCityName(String cityName) {
        CityName = cityName;
    }
}
