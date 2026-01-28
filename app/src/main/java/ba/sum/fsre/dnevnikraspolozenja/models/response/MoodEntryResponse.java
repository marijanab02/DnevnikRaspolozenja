package ba.sum.fsre.dnevnikraspolozenja.models.response;

public class MoodEntryResponse {
    private long id;
    private String user_id;
    private int mood_score;
    private String note;
    private String created_at;

    private boolean deleted;

    // Getters
    public long getId() {
        return id;
    }

    public String getUserId() {
        return user_id;
    }

    public int getMoodScore() {
        return mood_score;
    }

    public String getNote() {
        return note;
    }

    public String getCreatedAt() {
        return created_at;
    }

    // Setters
    public void setId(long id) {
        this.id = id;
    }

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    public void setMoodScore(int mood_score) {
        this.mood_score = mood_score;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setCreatedAt(String created_at) {
        this.created_at = created_at;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}