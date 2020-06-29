package com.uzair.smarttravelmanagement.Models;

import java.util.ArrayList;

public class NearbyLocationsModel {
    private String HotelId;
    private String City;
    private String LocationName;
    private String LocationLatitude;
    private String LocationLongitude;
    private ArrayList<String> ImageURL;

    public NearbyLocationsModel() {
    }

    public NearbyLocationsModel(String hotelId, String city, String locationName, String locationLatitude, String locationLongitude, ArrayList<String> imageURL) {
        HotelId = hotelId;
        City = city;
        LocationName = locationName;
        LocationLatitude = locationLatitude;
        LocationLongitude = locationLongitude;
        ImageURL = imageURL;
    }

    public String getHotelId() {
        return HotelId;
    }

    public void setHotelId(String hotelId) {
        HotelId = hotelId;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getLocationName() {
        return LocationName;
    }

    public void setLocationName(String locationName) {
        LocationName = locationName;
    }

    public String getLocationLatitude() {
        return LocationLatitude;
    }

    public void setLocationLatitude(String locationLatitude) {
        LocationLatitude = locationLatitude;
    }

    public String getLocationLongitude() {
        return LocationLongitude;
    }

    public void setLocationLongitude(String locationLongitude) {
        LocationLongitude = locationLongitude;
    }

    public ArrayList<String> getImageURL() {
        return ImageURL;
    }

    public void setImageURL(ArrayList<String> imageURL) {
        ImageURL = imageURL;
    }
}
