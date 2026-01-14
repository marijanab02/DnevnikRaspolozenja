package com.example.dnevnikraspolozenja.models.response;

public class MentalTaskResponse {
    private long id;
    private String title;
    private String description;
    private int mood_level;
    private String created_at;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMood_level() {
        return mood_level;
    }

    public void setMood_level(int mood_level) {
        this.mood_level = mood_level;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
