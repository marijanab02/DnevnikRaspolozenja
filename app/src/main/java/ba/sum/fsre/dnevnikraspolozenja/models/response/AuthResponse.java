package ba.sum.fsre.dnevnikraspolozenja.models.response;


import com.google.gson.annotations.SerializedName;

import ba.sum.fsre.dnevnikraspolozenja.models.User;

public class AuthResponse extends BaseResponse {
    private String id;
    private String email;

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("refresh_token")
    private String refreshToken;

    private User user;

    // Getteri
    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public User getUser() { return user; }
}