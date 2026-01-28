package ba.sum.fsre.dnevnikraspolozenja.models.response;

import com.google.gson.annotations.SerializedName;

public class UserTaskStatusResponse {

    // ID statusa korisnika za taj task
    private long id;

    @SerializedName("user_id")
    private String userId;

    private Task task;

    @SerializedName("completed")
    private boolean completed;

    @SerializedName("completed_at")
    private String completedAt; // ISO timestamp

    // Getteri i setteri
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }

    // Inner klasa za Task
    public static class Task {
        private long id;
        private String title;
        private String description;
        private int moodLevel; // opcionalno, ako postoji

        // Getteri i setteri
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

        public int getMoodLevel() {
            return moodLevel;
        }

        public void setMoodLevel(int moodLevel) {
            this.moodLevel = moodLevel;
        }
    }
}

