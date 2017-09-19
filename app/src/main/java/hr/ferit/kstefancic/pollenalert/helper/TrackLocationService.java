package hr.ferit.kstefancic.pollenalert.helper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hr.ferit.kstefancic.pollenalert.AppController;
import hr.ferit.kstefancic.pollenalert.LocationPollenFragment;
import hr.ferit.kstefancic.pollenalert.MainActivity;
import hr.ferit.kstefancic.pollenalert.Pollen;
import hr.ferit.kstefancic.pollenalert.User;

public class TrackLocationService extends Service
{
    private static final String TAG = "TRACKINGSERVICE";
    private static final String USER_KEY = "user";
    private static final String MSG_TEXT = "On your location is a high level of pollen that you're alergic to! Please be careful.";
    private static final String MSG_KEY = "message";
    private LocationManager mLocationManager = null;
    private CurrLocationListener mLocationListener;
    private static final int LOCATION_INTERVAL = 60000;
    private static final float LOCATION_DISTANCE = 0;
    private ArrayList<AccuPollenForecast> mAccuPollens;
    private ArrayList<Pollen> mUserAllergies;
    private User mUser;
    private static final String HIGH = "High";

    private class CurrLocationListener implements LocationListener
    {
        Location mLastLocation;

        @Override
        public void onLocationChanged(Location location)
        {
            Log.e(TAG, "onLocationChanged: " + location);
            if(mLastLocation!=location){
                searchForLocation(location);
                mLastLocation = location;
            }
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }



    private void searchForLocation(Location location) {
        Log.d("SHOWDIALOG","searchforlocation");
        // showDialog();
        String tag_str_req = "req_city_code_service";
        String url = "http://dataservice.accuweather.com/locations/v1/cities/geoposition/search?apikey="+ MainActivity.ApiKey+"&q="+location.getLatitude()+
                "%2C"+location.getLongitude()+"&toplevel=true";
        JsonObjectRequest getRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response",response.toString());
                        AccuCity currentCity = parseJSONCity(response);
                        if(currentCity!=null){
                            getPollenData(currentCity.getmKey());
                        }
                        //  hideDialog();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response",error.toString());
                        //   hideDialog();
                        Toast.makeText(getApplicationContext(),"Something went wrong, please try again!",Toast.LENGTH_SHORT).show();
                    }
                });
        AppController.getInstance().addToRequestQueue(getRequest,tag_str_req);
    }

    private void getPollenData(String locationKey) {
        Log.d("Service getPOLLENDATA","getpollendata");
        String tag_str_req = "req_pollen_code_service";
        String url = "http://dataservice.accuweather.com/forecasts/v1/daily/5day/"+locationKey+"?apikey="+MainActivity.ApiKey+"&details=true";
        JsonObjectRequest getRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response",response.toString());
                        mAccuPollens.clear();
                        parseJSONPollenData(response);
                        checkDataForNotification();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response",error.toString());
                    }
                });
        AppController.getInstance().addToRequestQueue(getRequest,tag_str_req);
    }

    private void checkDataForNotification() {
        for(int i=0;i<mUserAllergies.size();i++){
            if(mUserAllergies.get(i).getCategory()=="tree"){
                if(mAccuPollens.get(3).getCategory(0).equals(HIGH)){
                    Log.d(TAG,mUserAllergies.get(i).getCategory()+" "+mAccuPollens.get(3).getCategory(0));
                    showNotification();
                    break;
                }
            }else if(mUserAllergies.get(i).getCategory()=="grass"){
                if(mAccuPollens.get(0).getCategory(0).equals(HIGH)){
                    Log.d(TAG,mUserAllergies.get(i).getCategory()+" "+mAccuPollens.get(0).getCategory(0));
                    showNotification();
                    break;
                }
            }else if(mUserAllergies.get(i).getCategory()=="weed"){
                Log.d(TAG,mUserAllergies.get(i).getCategory()+" "+mAccuPollens.get(2).getCategory(0));
                if(mAccuPollens.get(2).getCategory(0).equals(HIGH)){
                    Log.d(TAG,mUserAllergies.get(i).getCategory()+" "+mAccuPollens.get(1).getCategory(0));
                    showNotification();
                    break;
                }
            }else {
                if(mAccuPollens.get(1).getCategory(0).equals(HIGH)){
                    Log.d(TAG,mUserAllergies.get(i).getCategory()+" "+mAccuPollens.get(3).getCategory(0));
                    showNotification();
                    break;
                }
            }
        }

    }

    private void showNotification() {
        Log.d(TAG,"showNotify");
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        notificationIntent.putExtra(MSG_KEY,MSG_TEXT);
        notificationIntent.putExtra("user",mUser);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setAutoCancel(true)
                .setContentTitle("High level of pollen in your area!")
                .setContentText(MSG_TEXT)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentIntent(notificationPendingIntent)
                .setLights(Color.BLUE, 2000, 1000)
                .setVibrate(new long[]{1000,1000,1000,1000,1000})
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        Notification notification = notificationBuilder.build();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0,notification);
    }

    private void parseJSONPollenData(JSONObject jObj) {
        JSONObject joDailyForecast=null, joGrass = null, joMold=null, joWeeds=null, joTrees=null;
        JSONArray jsonDailyForecasts, jsonPollen;
        AccuPollenForecast grassForecast = new AccuPollenForecast();
        AccuPollenForecast moldForecast = new AccuPollenForecast();
        AccuPollenForecast weedForecast = new AccuPollenForecast();
        AccuPollenForecast treeForecast = new AccuPollenForecast();
        try {
            jsonDailyForecasts= jObj.getJSONArray("DailyForecasts");
            for(int i=0;i<1;i++) {
                joDailyForecast = jsonDailyForecasts.getJSONObject(i);
                jsonPollen = joDailyForecast.getJSONArray("AirAndPollen");
                joGrass = jsonPollen.getJSONObject(1);
                grassForecast.addValue(joGrass.getInt("Value"), i);
                grassForecast.addCategory(joGrass.getString("Category"),i);
                grassForecast.setmName(joGrass.getString("Name"));
                //accuPollen.addCategoryValue(joGrass.getInt("CategoryValue"), accuPollen.getGRASS());
                joMold = jsonPollen.getJSONObject(2);
                moldForecast.addValue(joMold.getInt("Value"), i);
                moldForecast.addCategory(joMold.getString("Category"), i);
                moldForecast.setmName(joMold.getString("Name"));
                //accuPollen.addCategoryValue(joMold.getInt("CategoryValue"), accuPollen.getMOLD());
                joWeeds = jsonPollen.getJSONObject(3);
                weedForecast.addValue(joWeeds.getInt("Value"), i);
                weedForecast.addCategory(joWeeds.getString("Category"), i);
                weedForecast.setmName(joWeeds.getString("Name"));
                // accuPollen.addCategoryValue(joWeeds.getInt("CategoryValue"), accuPollen.getWEED());
                joTrees = jsonPollen.getJSONObject(4);
                treeForecast.addValue(joTrees.getInt("Value"), i);
                treeForecast.addCategory(joTrees.getString("Category"), i);
                treeForecast.setmName(joTrees.getString("Name"));
                // accuPollen.addCategoryValue(joTrees.getInt("CategoryValue"), accuPollen.getTREE());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mAccuPollens.add(grassForecast);
        mAccuPollens.add(moldForecast);
        mAccuPollens.add(weedForecast);
        mAccuPollens.add(treeForecast);
    }

    private AccuCity parseJSONCity(JSONObject jo) {
        String city="",country="",key="",region="",adminArea="";
        AccuCity accuCity=null;
        try {
            city = jo.getString("LocalizedName");
            key = jo.getString("Key");
            JSONObject joAdmin = new JSONObject(jo.getString("AdministrativeArea"));
            adminArea = joAdmin.getString("LocalizedName");
            JSONObject joCountry = new JSONObject(jo.getString("Country"));
            country = joCountry.getString("LocalizedName");
            JSONObject joGeoPosition = new JSONObject(jo.getString("GeoPosition"));
            double latitude = joGeoPosition.getDouble("Latitude");
            double longitude = joGeoPosition.getDouble("Longitude");
            JSONObject joRegion = new JSONObject(jo.getString("Region"));
            region = joRegion.getString("LocalizedName");
            accuCity = new AccuCity(city,key,country,adminArea,region);
            accuCity.setmLatitude((float) latitude);
            accuCity.setmLongitude((float) longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return accuCity;
    }

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        mUser = (User) intent.getSerializableExtra(USER_KEY);
        mUserAllergies = mUser.getmAllergies();
        mAccuPollens = new ArrayList<>();
        Log.e(TAG, mUserAllergies.get(0).getCategory());
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }



    @Override
    public void onCreate()
    {
        Log.e(TAG, "onCreate");
        initializeLocationManager();
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();

        mLocationManager.removeUpdates(mLocationListener);

    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
               try{
                   mLocationListener = new CurrLocationListener();
                   Criteria criteria = new Criteria();
                   criteria.setAccuracy(Criteria.ACCURACY_FINE);
                   String locationProvider = mLocationManager.getBestProvider(criteria, true);
                   mLocationManager.requestLocationUpdates(locationProvider, 0, 0, mLocationListener);
               }
               catch (Exception ex){
                   Log.e(TAG,ex.toString());
               }
    }
}