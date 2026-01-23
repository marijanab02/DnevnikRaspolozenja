package com.example.dnevnikraspolozenja.api;

import com.example.dnevnikraspolozenja.models.MentalTask;
import com.example.dnevnikraspolozenja.models.request.CreateMentalTaskRequest;
import com.example.dnevnikraspolozenja.models.request.RegisterRequest;

import com.example.dnevnikraspolozenja.models.request.LoginRequest;
import com.example.dnevnikraspolozenja.models.request.UpdateUserTaskRequest;
import com.example.dnevnikraspolozenja.models.request.UserTaskRequest;
import com.example.dnevnikraspolozenja.models.response.AuthResponse;

import com.example.dnevnikraspolozenja.models.request.ProfileUpdateRequest;
import com.example.dnevnikraspolozenja.models.response.MentalTaskResponse;
import com.example.dnevnikraspolozenja.models.response.ProfileResponse;


import com.example.dnevnikraspolozenja.models.response.MoodEntryResponse;
import com.example.dnevnikraspolozenja.models.request.CreateMoodRequest;
import com.example.dnevnikraspolozenja.models.request.UpdateMoodRequest;
import com.example.dnevnikraspolozenja.models.response.UserTaskStatusResponse;


import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.DELETE;
import retrofit2.http.QueryMap;

public interface SupabaseAPI {
    @Headers("Content-Type: application/json")


    @POST("auth/v1/token?grant_type=password")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("auth/v1/signup")
    Call<AuthResponse> signup(@Body RegisterRequest request);

    @POST("rest/v1/profile")
    Call<Void> insertProfile(
            @Body ProfileUpdateRequest request
    );

    @GET("rest/v1/profile")
    Call<ProfileResponse[]> getProfile(
            @Header("Authorization") String token,
            @Query("id") String idFilter  // eq. ide u EditProfileActivity
    );

    @PATCH("rest/v1/profile")
    Call<ProfileResponse> updateProfile(
            @Header("Authorization") String token,
            // @Header("apikey") String apiKey,
            @Query("id") String idFilter,  // eq. ide u EditProfileActivity
            @Body ProfileUpdateRequest request
    );





    @GET("rest/v1/mood_entries")
    Call<MoodEntryResponse[]> getMoodEntries(
            @Header("Authorization") String token,
            @Query("user_id") String userIdFilter,
            @Query("order") String order,
            @Query("deleted") String deletedFilter

    );


    @POST("rest/v1/mood_entries")
    Call<Void> createMoodEntry(
            @Header("Authorization") String token,
            @Body CreateMoodRequest request
    );


    @DELETE("rest/v1/mood_entries")
    Call<Void> deleteMoodEntry(
            @Header("Authorization") String token,
            @Query("id") String idFilter
    );




    @PATCH("rest/v1/mood_entries")
    Call<Void> softDeleteMood(
            @Header("Authorization") String token,
            @Query("id") String idFilter,
            @Body UpdateMoodRequest request
    );

    @GET("rest/v1/mental_tasks")
    Call<MentalTaskResponse[]> getMentalTasks(
            @Header("Authorization") String token,
            @Query("order") String order
    );

    @POST("rest/v1/mental_tasks")
    Call<Void> createMentalTask(
            @Header("Authorization") String token,
            @Body CreateMentalTaskRequest request
    );

    @DELETE("rest/v1/mental_tasks")
    Call<Void> deleteMentalTask(
            @Header("Authorization") String token,
            @Query("id") String idFilter
    );
    @GET("rest/v1/mental_tasks")
    Call<List<MentalTask>> getMentalTasks(
            @Header("Authorization") String token
    );
    @GET("rest/v1/mental_tasks")
    Call<List<MentalTask>> getTasksForMood(
            @Header("Authorization") String token,
            @Query("mood_levels") String moodLevelArray,
            @Query("order") String order,   // npr. "id.asc"
            @Query("limit") Integer limit   // npr. 100
    );
    @POST("rest/v1/user_task_status")
    Call<Void> insertUserTask(
            @Header("Authorization") String token,
            @Body UserTaskRequest request
    );
    // Dohvati zadnji task za korisnika
    @GET("rest/v1/user_task_status")
    Call<UserTaskStatusResponse[]> getLastUserTask(
            @Header("Authorization") String token,
            @Query("user_id") String userIdFilter,
            @Query("select") String select,
            @Query("order") String order, // npr. "id.desc"
            @Query("limit") int limit      // npr. 1
    );

    // Update user task status
    @PATCH("rest/v1/user_task_status")
    Call<Void> updateUserTask(
            @Header("Authorization") String token,
            @Query("id") String idFilter,
            @Body UpdateUserTaskRequest request
    );

    @GET("rest/v1/mood_entries")
    Call<MoodEntryResponse[]> getMoodEntriesForMonth(
            @Header("Authorization") String token,
            @QueryMap Map<String, String> filters
    );

}