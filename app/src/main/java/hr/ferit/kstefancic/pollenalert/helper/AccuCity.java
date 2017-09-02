package hr.ferit.kstefancic.pollenalert.helper;

/**
 * Created by Kristijan on 22.8.2017..
 */

public class AccuCity {
    String mCity, mKey, mCountry, mAdministrativeArea, mRegion;
    float mLatitude, mLongitude;

    public AccuCity(String mCity, String mKey, String mCountry, String mAdministrativeArea, String mRegion) {
        this.mCity = mCity;
        this.mKey = mKey;
        this.mCountry = mCountry;
        this.mAdministrativeArea = mAdministrativeArea;
        this.mRegion = mRegion;
    }

    @Override
    public String toString() {
        if(mAdministrativeArea.isEmpty()){
            return mCity+", "+mCountry+", "+mRegion;
        }
        else{
            return  mCity+", "+mAdministrativeArea+", "+mCountry+", "+mRegion;
        }
    }

    public String getmRegion() {
        return mRegion;
    }

    public void setmRegion(String mRegion) {
        this.mRegion = mRegion;
    }

    public String getmCity() {
        return mCity;
    }

    public void setmCity(String mCity) {
        this.mCity = mCity;
    }

    public String getmKey() {
        return mKey;
    }

    public void setmKey(String mKey) {
        this.mKey = mKey;
    }

    public String getmCountry() {
        return mCountry;
    }

    public void setmCountry(String mCountry) {
        this.mCountry = mCountry;
    }

    public String getmAdministrativeArea() {
        return mAdministrativeArea;
    }

    public void setmAdministrativeArea(String mAdministrativeArea) {
        this.mAdministrativeArea = mAdministrativeArea;
    }

    public float getmLatitude() {
        return mLatitude;
    }

    public void setmLatitude(float mLatitude) {
        this.mLatitude = mLatitude;
    }

    public float getmLongitude() {
        return mLongitude;
    }

    public void setmLongitude(float mLongitude) {
        this.mLongitude = mLongitude;
    }
}
