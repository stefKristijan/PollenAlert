package hr.ferit.kstefancic.pollenalert;

import java.io.Serializable;

/**
 * Created by Kristijan on 18.8.2017..
 */

public class User implements Serializable {
    private String mUsername, mUniqueId, mFullName, mPassword, mEmail;
    private int id;

    public User (){
        mUniqueId ="";
        mEmail = "";
        mFullName ="";
        mPassword="";
        id=-1;
        mUsername="";
    }

    public User(String mUsername, String mEmail) {
        this.mUsername = mUsername;
        this.mEmail = mEmail;
    }

    public String getmPassword() {
        return mPassword;
    }

    public void setmPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public String getmUsername() {
        return mUsername;
    }

    public void setmUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public String getmUniqueId() {
        return mUniqueId;
    }

    public void setmUniqueId(String mUniqueId) {
        this.mUniqueId = mUniqueId;
    }

    public String getmFullName() {
        return mFullName;
    }

    public void setmFullName(String mFullName) {
        this.mFullName = mFullName;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
