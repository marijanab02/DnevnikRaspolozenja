package com.example.dnevnikraspolozenja.models.request;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CreateMentalTaskRequest {

    private String title;
    private String description;

    @SerializedName("mood_levels")
    private List<Integer> moodLevel;

    public CreateMentalTaskRequest(String title, String description, List<Integer> moodLevel) {
        this.title = title;
        this.description = description;
        this.moodLevel = moodLevel;
    }
}
