package com.example.dnevnikraspolozenja.models.request;

public class CreateMentalTaskRequest {
    private String title;
    private String description;
    private int mood_level;

    public CreateMentalTaskRequest(String title, String description, int mood_level) {
        this.title = title;
        this.description = description;
        this.mood_level = mood_level;
    }

}
