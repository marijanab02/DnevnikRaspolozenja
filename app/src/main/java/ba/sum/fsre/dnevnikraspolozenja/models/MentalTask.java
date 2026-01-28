package ba.sum.fsre.dnevnikraspolozenja.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MentalTask {

    private long id;
    private String title;
    private String description;

    @SerializedName("mood_levels")
    private List<Integer> moodLevel;

    @SerializedName("created_at")
    private String createdAt;

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<Integer> getMoodLevel() {
        return moodLevel;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
