package ba.sum.fsre.dnevnikraspolozenja.models.request;

public class CreateMoodRequest {
    private String user_id;
    private int mood_score;
    private String note;

    public CreateMoodRequest(String user_id, int mood_score, String note) {
        this.user_id = user_id;
        this.mood_score = mood_score;
        this.note = note;
    }


    public String getUser_id() { return user_id; }
    public int getMood_score() { return mood_score; }
    public String getNote() { return note; }


}