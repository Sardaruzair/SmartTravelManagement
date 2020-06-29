package com.uzair.smarttravelmanagement.Models;

public class HotelBookingModel {
    private String UserId, BookingId, HotelId, HotelName, HotelCity, FirstName, LastName, Email, Phone, CheckinDate, CheckoutDate,
            Comments, SingleBeds, DoubleBeds, Adults, Childrens, BookingStatus, netPrice, totalPrice, pickupLatitude, pickupLongitude, transportType;
    private Boolean BookTransport;
    private Long Timestamp;

    public HotelBookingModel() {
    }

    public HotelBookingModel(String userId,
                             String bookingId,
                             String hotelId,
                             String hotelName,
                             String hotelCity,
                             String firstName,
                             String lastName,
                             String email,
                             String phone,
                             String checkinDate,
                             String checkoutDate,
                             String comments,
                             String singleBeds,
                             String doubleBeds,
                             String adults,
                             String childrens,
                             Boolean bookTransport,
                             Long timestamp,
                             String bookingStatus,
                             String netPrice,
                             String totalPrice,
                             String pickupLatitude,
                             String pickupLongitude,
                             String transportType) {
        UserId = userId;
        BookingId = bookingId;
        HotelId = hotelId;
        HotelName = hotelName;
        HotelCity = hotelCity;
        FirstName = firstName;
        LastName = lastName;
        Email = email;
        Phone = phone;
        CheckinDate = checkinDate;
        CheckoutDate = checkoutDate;
        Comments = comments;
        SingleBeds = singleBeds;
        DoubleBeds = doubleBeds;
        Adults = adults;
        Childrens = childrens;
        BookTransport = bookTransport;
        Timestamp = timestamp;
        BookingStatus = bookingStatus;
        this.netPrice = netPrice;
        this.totalPrice = totalPrice;
        this.pickupLatitude = pickupLatitude;
        this.pickupLongitude = pickupLongitude;
        this.transportType = transportType;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getBookingId() {
        return BookingId;
    }

    public void setBookingId(String bookingId) {
        BookingId = bookingId;
    }

    public String getHotelId() {
        return HotelId;
    }

    public String getHotelName() {
        return HotelName;
    }

    public void setHotelName(String hotelName) {
        HotelName = hotelName;
    }

    public String getHotelCity() {
        return HotelCity;
    }

    public void setHotelCity(String hotelCity) {
        HotelCity = hotelCity;
    }

    public void setHotelId(String hotelId) {
        HotelId = hotelId;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getCheckinDate() {
        return CheckinDate;
    }

    public void setCheckinDate(String checkinDate) {
        CheckinDate = checkinDate;
    }

    public String getCheckoutDate() {
        return CheckoutDate;
    }

    public void setCheckoutDate(String checkoutDate) {
        CheckoutDate = checkoutDate;
    }

    public String getComments() {
        return Comments;
    }

    public void setComments(String comments) {
        Comments = comments;
    }

    public String getSingleBeds() {
        return SingleBeds;
    }

    public void setSingleBeds(String singleBeds) {
        SingleBeds = singleBeds;
    }

    public String getDoubleBeds() {
        return DoubleBeds;
    }

    public void setDoubleBeds(String doubleBeds) {
        DoubleBeds = doubleBeds;
    }

    public String getAdults() {
        return Adults;
    }

    public void setAdults(String adults) {
        Adults = adults;
    }

    public String getChildrens() {
        return Childrens;
    }

    public void setChildrens(String childrens) {
        Childrens = childrens;
    }

    public Boolean getBookTransport() {
        return BookTransport;
    }

    public void setBookTransport(Boolean bookTransport) {
        BookTransport = bookTransport;
    }

    public Long getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(Long timestamp) {
        Timestamp = timestamp;
    }

    public String getBookingStatus() {
        return BookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        BookingStatus = bookingStatus;
    }

    public String getNetPrice() {
        return netPrice;
    }

    public void setNetPrice(String netPrice) {
        this.netPrice = netPrice;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getPickupLatitude() {
        return pickupLatitude;
    }

    public void setPickupLatitude(String pickupLatitude) {
        this.pickupLatitude = pickupLatitude;
    }

    public String getPickupLongitude() {
        return pickupLongitude;
    }

    public void setPickupLongitude(String pickupLongitude) {
        this.pickupLongitude = pickupLongitude;
    }

    public String getTransportType() {
        return transportType;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }
}
