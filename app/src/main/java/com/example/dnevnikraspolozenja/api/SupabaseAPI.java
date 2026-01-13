package com.example.dnevnikraspolozenja.api;

import com.example.dnevnikraspolozenja.models.request.RegisterRequest;

import com.example.dnevnikraspolozenja.models.request.LoginRequest;
import com.example.dnevnikraspolozenja.models.response.AuthResponse;

import com.example.dnevnikraspolozenja.models.request.ProfileUpdateRequest;
import com.example.dnevnikraspolozenja.models.response.ProfileResponse;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SupabaseAPI {
    @Headers("Content-Type: application/json")
    @POST("auth/v1/token?grant_type=password")
    Call<AuthResponse> login(@Body LoginRequest request);




    @POST("auth/v1/signup")
    Call<AuthResponse> signup(@Body RegisterRequest request);

    @GET("rest/v1/profile")
    Call<ProfileResponse[]> getProfile(
            @Header("Authorization") String token,
            //@Header("apikey") String apiKey,
            @Query("id") String idFilter  // eq. ide u EditProfileActivity
    );


    @PATCH("rest/v1/profile")
    Call<ProfileResponse> updateProfile(
            @Header("Authorization") String token,
           // @Header("apikey") String apiKey,
            @Query("id") String idFilter,  // eq. ide u EditProfileActivity
            @Body ProfileUpdateRequest request
    );


}