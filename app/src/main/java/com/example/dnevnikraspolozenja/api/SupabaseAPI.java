package com.example.dnevnikraspolozenja.api;

import com.example.dnevnikraspolozenja.models.request.RegisterRequest;

import com.example.dnevnikraspolozenja.models.request.LoginRequest;
import com.example.dnevnikraspolozenja.models.response.AuthResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface SupabaseAPI {
    @Headers("Content-Type: application/json")
    @POST("auth/v1/token?grant_type=password")
    Call<AuthResponse> login(@Body LoginRequest request);




    @POST("auth/v1/signup")
    Call<AuthResponse> signup(@Body RegisterRequest request);
}