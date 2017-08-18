package hr.ferit.kstefancic.pollenalert;

import java.io.Serializable;

/**
 * Created by Kristijan on 18.8.2017..
 */

public class Location implements Serializable{
    String mStreet, mCity, mCountry, mState, mNumber;

    public Location(String mStreet, String mCity, String mCountry, String mState, String mNumber) {
        this.mStreet = mStreet;
        this.mCity = mCity;
        this.mCountry = mCountry;
        this.mState = mState;
        this.mNumber = mNumber;
    }

    public String getmStreet() {
        return mStreet;
    }

    public void setmStreet(String mStreet) {
        this.mStreet = mStreet;
    }

    public String getmCity() {
        return mCity;
    }

    public void setmCity(String mCity) {
        this.mCity = mCity;
    }

    public String getmCountry() {
        return mCountry;
    }

    public void setmCountry(String mCountry) {
        this.mCountry = mCountry;
    }

    public String getmState() {
        return mState;
    }

    public void setmState(String mState) {
        this.mState = mState;
    }

    public String getmNumber() {
        return mNumber;
    }

    public void setmNumber(String mNumber) {
        this.mNumber = mNumber;
    }
}
