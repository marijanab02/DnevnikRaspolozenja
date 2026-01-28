package ba.sum.fsre.dnevnikraspolozenja.models.request;

public class UpdateUserTaskRequest {
    private boolean completed;
    private String completed_at;

    public UpdateUserTaskRequest(boolean completed, String completed_at) {
        this.completed = completed;
        this.completed_at = completed_at;
    }
}