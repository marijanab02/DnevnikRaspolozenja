package ba.sum.fsre.dnevnikraspolozenja.models.request;

import com.google.gson.annotations.SerializedName;

public class UserTaskRequest {

    @SerializedName("user_id")
    private String userId;

    @SerializedName("task_id")
    private long taskId;

    private boolean completed;

    @SerializedName("completed_at")
    private String completedAt; // ISO timestamp, može biti null

    // Defaultni konstruktor potreban za Gson
    public UserTaskRequest() {}

    // Konstruktor za kreiranje novog taska
    public UserTaskRequest(String userId, long taskId) {
        this.userId = userId;
        this.taskId = taskId;
        this.completed = false;
        this.completedAt = null;
    }

    // Konstruktor za update statusa
    public UserTaskRequest(boolean completed, String completedAt) {
        this.completed = completed;
        this.completedAt = completedAt;
    }

    // Getteri i setteri
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
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
}
