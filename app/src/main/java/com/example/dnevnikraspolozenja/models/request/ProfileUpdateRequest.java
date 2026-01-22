package com.example.dnevnikraspolozenja.models.request;

public class ProfileUpdateRequest {
    private String id;
    private String full_name;
    private String date_of_birth;
    private String avatar_url;

    public ProfileUpdateRequest(String id, String full_name, String date_of_birth, String avatar_url) {
        this.id = id;
        this.full_name = full_name;
        this.date_of_birth = date_of_birth;
        this.avatar_url = avatar_url;
    }

    public String getId() {
        return id;
    }

    public String getFull_name() {
        return full_name;
    }

    public String getDate_of_birth() {
        return date_of_birth;
    }

    public String getAvatar_url() {
        return avatar_url;
    }
}