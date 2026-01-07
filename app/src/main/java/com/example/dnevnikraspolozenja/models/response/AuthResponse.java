package com.example.dnevnikraspolozenja.models.response;


import com.example.dnevnikraspolozenja.models.User;
import com.google.gson.annotations.SerializedName;

public class AuthResponse extends BaseResponse {
    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("refresh_token")
    private String refreshToken;

    private User user;

    // Getteri
    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public User getUser() { return user; }
}