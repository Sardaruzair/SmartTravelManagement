package com.uzair.smarttravelmanagement.Models;

public class User {

    private String UserId, FirstName, LastName, MobileNumber, EmailAddress, UserType;

    public User() {
    }

    public User(String firstName, String lastName, String mobileNumber, String emailAddress, String UserType) {
        this.FirstName = firstName;
        this.LastName = lastName;
        this.MobileNumber = mobileNumber;
        this.EmailAddress = emailAddress;
        this.UserType = UserType;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
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

    public String getMobileNumber() {
        return MobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        MobileNumber = mobileNumber;
    }

    public String getEmailAddress() {
        return EmailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        EmailAddress = emailAddress;
    }

    public String getUserType() {
        return UserType;
    }

    public void setUserType(String userType) {
        UserType = userType;
    }
}
