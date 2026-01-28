package ba.sum.fsre.dnevnikraspolozenja.models.request;

public class UpdateMoodRequest {
    private boolean deleted;

    public UpdateMoodRequest(boolean deleted) {
        this.deleted = deleted;
    }

}