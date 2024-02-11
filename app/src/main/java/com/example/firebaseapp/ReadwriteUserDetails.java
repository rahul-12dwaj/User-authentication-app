package com.example.firebaseapp;

public class ReadwriteUserDetails {
    public String fullName, gender, email, dob, mobile;
    public ReadwriteUserDetails(){}
    public ReadwriteUserDetails(String textFullName,String textEmail,String textGender,String textMobile,String textDob){
        this.fullName=textFullName;
        this.email=textEmail;
        this.gender=textGender;
        this.dob=textDob;
        this.mobile=textMobile;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getDob() {
        return dob;
    }

    public String getGender() {
        return gender;
    }

    public String getMobile() {
        return mobile;
    }

    // Setter methods
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
