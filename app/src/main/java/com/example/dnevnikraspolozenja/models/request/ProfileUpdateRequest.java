package com.example.dnevnikraspolozenja.models.request;

public class ProfileUpdateRequest {

    private String full_name;
    private String date_of_birth;

    public ProfileUpdateRequest(String full_name, String date_of_birth) {
        this.full_name = full_name;
        this.date_of_birth = date_of_birth;
    }

    public String getFull_name() {
        return full_name;
    }

    public String getDate_of_birth() {
        return date_of_birth;
    }
}
