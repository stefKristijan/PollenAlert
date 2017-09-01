package hr.ferit.kstefancic.pollenalert;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Kristijan on 18.8.2017..
 */

public class User implements Serializable {
    private String mUsername, mUniqueId, mFullName, mPassword, mEmail, mAvatarPath;
    private int id;
    private Bitmap mAvatar;
    private ArrayList<Location> mLocations;
    private ArrayList<Pollen> mAllergies;

    public User (){
        mUniqueId ="";
        mEmail = "";
        mFullName ="";
        mPassword="";
        id=-1;
        mUsername="";
        mAvatar=null;
    }

    public String getmAvatarPath() {
        return mAvatarPath;
    }

    public void setmAvatarPath(String mAvatarPath) {
        this.mAvatarPath = mAvatarPath;
    }

    public User(String mUsername, String mEmail) {
        this.mUsername = mUsername;
        this.mEmail = mEmail;
    }

    public Bitmap getmAvatar() {
        return mAvatar;
    }

    public void setmAvatar(Bitmap mAvatar) {
        this.mAvatar = mAvatar;
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
