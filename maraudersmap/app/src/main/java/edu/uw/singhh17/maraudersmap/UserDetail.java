package edu.uw.singhh17.maraudersmap;

/**
 * Created by Patrick on 3/3/2016.
 */
public class UserDetail {

    private String fullName;
    public UserDetail() {}
    public UserDetail(String fullName) {
        this.fullName = fullName;

    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }
}
