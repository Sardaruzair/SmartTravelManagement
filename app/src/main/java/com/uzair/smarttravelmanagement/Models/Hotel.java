package com.uzair.smarttravelmanagement.Models;

import java.util.ArrayList;

public class Hotel {
    private String HotelId;
    private String HotelName;
    private String SingleBedPrice;
    private String DoubleBedPrice;
    private String DiscountedSingleBedPrice;
    private String DiscountedDoubleBedPrice;
    private String HotelDetails;
    private String City;
    private ArrayList<String> ImageURL;
    private String OwnerName;
    private String OwnerPhone;
    private String OwnerCnic;
    private String FoodAvailable;
    private Boolean isSingleBed;
    private Boolean isDoubleBed;
    private Boolean isDiscountAvailable;
    private String hotelLatitude;
    private String hotelLongitude;
    private Boolean isRoomsAvailable;

    public Hotel() {
    }

    public Hotel(String HotelId,
                 String hotelName,
                 String singleBedPrice,
                 String doubleBedPrice,
                 String discountedSingleBedPrice,
                 String discountedDoubleBedPrice,
                 String hotelDetails,
                 String city,
                 ArrayList<String> imageURL,
                 String ownerName,
                 String ownerPhone,
                 String ownerCnic,
                 String foodAvailable,
                 Boolean isSingleBed,
                 Boolean isDoubleBed,
                 Boolean isDiscountAvailable,
                 String hotelLatitude,
                 String hotelLongitude,
                 Boolean isRoomsAvailable) {
        HotelId = HotelId;
        HotelName = hotelName;
        SingleBedPrice = singleBedPrice;
        DoubleBedPrice = doubleBedPrice;
        DiscountedSingleBedPrice = discountedSingleBedPrice;
        DiscountedDoubleBedPrice = discountedDoubleBedPrice;
        HotelDetails = hotelDetails;
        City = city;
        ImageURL = imageURL;
        OwnerName = ownerName;
        OwnerPhone = ownerPhone;
        OwnerCnic = ownerCnic;
        FoodAvailable = foodAvailable;
        this.isSingleBed = isSingleBed;
        this.isDoubleBed = isDoubleBed;
        this.isDiscountAvailable = isDiscountAvailable;
        this.hotelLatitude = hotelLatitude;
        this.hotelLongitude = hotelLongitude;
        this.isRoomsAvailable = isRoomsAvailable;
    }

    public String getHotelId() {
        return HotelId;
    }

    public void setHotelId(String hotelId) {
        HotelId = hotelId;
    }

    public String getHotelName() {
        return HotelName;
    }

    public void setHotelName(String hotelName) {
        HotelName = hotelName;
    }

    public String getSingleBedPrice() {
        return SingleBedPrice;
    }

    public void setSingleBedPrice(String singleBedPrice) {
        SingleBedPrice = singleBedPrice;
    }

    public String getDoubleBedPrice() {
        return DoubleBedPrice;
    }

    public void setDoubleBedPrice(String doubleBedPrice) {
        DoubleBedPrice = doubleBedPrice;
    }

    public String getHotelDetails() {
        return HotelDetails;
    }

    public void setHotelDetails(String hotelDetails) {
        HotelDetails = hotelDetails;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public ArrayList<String> getImageURL() {
        return ImageURL;
    }

    public void setImageURL(ArrayList<String> imageURL) {
        ImageURL = imageURL;
    }

    public String getOwnerName() {
        return OwnerName;
    }

    public void setOwnerName(String ownerName) {
        OwnerName = ownerName;
    }

    public String getOwnerPhone() {
        return OwnerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        OwnerPhone = ownerPhone;
    }

    public String getOwnerCnic() {
        return OwnerCnic;
    }

    public void setOwnerCnic(String ownerCnic) {
        OwnerCnic = ownerCnic;
    }

    public String getFoodAvailable() {
        return FoodAvailable;
    }

    public void setFoodAvailable(String foodAvailable) {
        FoodAvailable = foodAvailable;
    }

    public Boolean getSingleBed() {
        return isSingleBed;
    }

    public void setSingleBed(Boolean singleBed) {
        isSingleBed = singleBed;
    }

    public Boolean getDoubleBed() {
        return isDoubleBed;
    }

    public void setDoubleBed(Boolean doubleBed) {
        isDoubleBed = doubleBed;
    }

    public String getHotelLatitude() {
        return hotelLatitude;
    }

    public void setHotelLatitude(String hotelLatitude) {
        this.hotelLatitude = hotelLatitude;
    }

    public String getHotelLongitude() {
        return hotelLongitude;
    }

    public void setHotelLongitude(String hotelLongitude) {
        this.hotelLongitude = hotelLongitude;
    }

    public Boolean getRoomsAvailable() {
        return isRoomsAvailable;
    }

    public void setRoomsAvailable(Boolean roomsAvailable) {
        isRoomsAvailable = roomsAvailable;
    }

    public String getDiscountedSingleBedPrice() {
        return DiscountedSingleBedPrice;
    }

    public void setDiscountedSingleBedPrice(String discountedSingleBedPrice) {
        DiscountedSingleBedPrice = discountedSingleBedPrice;
    }

    public String getDiscountedDoubleBedPrice() {
        return DiscountedDoubleBedPrice;
    }

    public void setDiscountedDoubleBedPrice(String discountedDoubleBedPrice) {
        DiscountedDoubleBedPrice = discountedDoubleBedPrice;
    }

    public Boolean getDiscountAvailable() {
        return isDiscountAvailable;
    }

    public void setDiscountAvailable(Boolean discountAvailable) {
        isDiscountAvailable = discountAvailable;
    }
}
