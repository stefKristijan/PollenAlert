package hr.ferit.kstefancic.pollenalert.helper;

import hr.ferit.kstefancic.pollenalert.User;

/**
 * Created by Kristijan on 18.9.2017..
 */

public class News {
    private String mSymptoms, mFeeling, mDate, mCity, mState, mCountry, mUsername, mAvatarPath;

    public News(String mSymptoms, String mFeeling, String mDate, String mCity, String mState, String mCountry, String mUsername, String mAvatarPath) {
        this.mSymptoms = mSymptoms;
        this.mFeeling = mFeeling;
        this.mDate = mDate;
        this.mCity = mCity;
        this.mState = mState;
        this.mCountry = mCountry;
        this.mUsername = mUsername;
        this.mAvatarPath = mAvatarPath;
    }

    public String getmAvatarPath() {
        return mAvatarPath;
    }

    public void setmAvatarPath(String mAvatarPath) {
        this.mAvatarPath = mAvatarPath;
    }

    public String getmCity() {
        return mCity;
    }

    public void setmCity(String mCity) {
        this.mCity = mCity;
    }

    public String getmState() {
        return mState;
    }

    public void setmState(String mState) {
        this.mState = mState;
    }

    public String getmCountry() {
        return mCountry;
    }

    public void setmCountry(String mCountry) {
        this.mCountry = mCountry;
    }

    public String getmSymptoms() {
        return mSymptoms;
    }

    public void setmSymptoms(String mSymptoms) {
        this.mSymptoms = mSymptoms;
    }

    public String getmFeeling() {
        return mFeeling;
    }

    public void setmFeeling(String mFeeling) {
        this.mFeeling = mFeeling;
    }

    public String getmDate() {
        return mDate;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    public String getmUsername() {
        return mUsername;
    }

    public void setmUsername(String mUsername) {
        this.mUsername = mUsername;
    }
}
