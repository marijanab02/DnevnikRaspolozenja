package com.example.dnevnikraspolozenja.api;


import com.example.dnevnikraspolozenja.models.request.LoginRequest;
import com.example.dnevnikraspolozenja.models.response.AuthResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface SupabaseAPI {
    @POST("auth/v1/token?grant_type=password")
    Call<AuthResponse> login(
            @Body LoginRequest request
    );
}