package com.me.njerucyrus.gradea;

/**
 * Created by njerucyrus on 3/1/18.
 */

public class User {
    private int userId;
    private String phoneNumber;
    private String email;
    private String fullName;
    private int userStatus;
    private int userLevel;
    private String dateJoined;


    public User() {
    }

    public User(int userId, String phoneNumber, String email, String fullName, int userStatus, int userLevel, String dateJoined) {
        this.userId = userId;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.fullName = fullName;
        this.userStatus = userStatus;
        this.userLevel = userLevel;
        this.dateJoined = dateJoined;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(int userStatus) {
        this.userStatus = userStatus;
    }

    public int getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(int userLevel) {
        this.userLevel = userLevel;
    }

    public String getDateJoined() {
        return dateJoined;
    }

    public void setDateJoined(String dateJoined) {
        this.dateJoined = dateJoined;
    }
}
