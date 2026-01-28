package ba.sum.fsre.dnevnikraspolozenja.models.response;

public class ProfileResponse {
    private String id;
    private String full_name;
    private String date_of_birth;
    private String avatar_url;

    public String getAvatarUrl() {
        return avatar_url;
    }

    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public String getFull_name() {
        return full_name;
    }

    public String getDate_of_birth() {
        return date_of_birth;
    }

}