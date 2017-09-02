package hr.ferit.kstefancic.pollenalert;

import android.*;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.*;
import android.location.Location;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import hr.ferit.kstefancic.pollenalert.helper.AccuCity;
import hr.ferit.kstefancic.pollenalert.helper.AccuPollenForecast;
import hr.ferit.kstefancic.pollenalert.helper.PollenCountAdapter;
import hr.ferit.kstefancic.pollenalert.helper.UserDBHelper;

/**
 * Created by Kristijan on 22.8.2017..
 */

public class LocationPollenFragment extends Fragment {


    private static final String GPS_DISABLED = "Please enable GPS to use this feature!";
    private static final String NO_PERMISSION = "You must allow the application to use location to use this feature!";
    private static final String USER_KEY = "user";
    private ImageButton ibSearch, ibMyLocation, ibHome;
    private AutoCompleteTextView atvLocation;
    private ArrayList<AccuCity> mAccuCities;
    private ArrayList<AccuPollenForecast> mAccuPollens;
    private RecyclerView mRvPollenData;
    private PollenCountAdapter mPollenCountAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.ItemDecoration mItemDecoration;
    private LocationListener mLocationListener;
    private LocationManager mLocationManager;
    private User mUser;
    public static final int GRASS = 0, MOLD = 1, WEED = 2, TREE = 3;


    //Potrebno da bi se mogao koristiti objekt mUser
    public static LocationPollenFragment newInstance(User user){
        LocationPollenFragment fragment = new LocationPollenFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(USER_KEY,user);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mUser = (User) getArguments().getSerializable(USER_KEY);
        View layout = inflater.inflate(R.layout.fragment_location_pollen, container, false);
        setUpUI(layout);
        return layout;
    }

    private void setUpUI(View layout) {
        //Prvo se provjerava je li dopušteno korištenje lokacije, ako je uzima se zadnja poznata lokacija
        //i na temelju nje se dobivaju podaci o peludi, inače se izvršava getKeyFromLocation()
        if(hasLocationPermission()) {
            Location location = getLastKnownLocation();
            if(location != null){
                searchForLocation(location);
            }else{
                getKeyFromUserLocation();
            }
        }
        else{
            getKeyFromUserLocation();
        }
        this.mRvPollenData = (RecyclerView) layout.findViewById(R.id.frLPrvPollenData);
        this.mAccuPollens = new ArrayList<>();
        this.mAccuCities = new ArrayList<>();
        this.ibSearch = (ImageButton) layout.findViewById(R.id.frLPibSearch);
        this.ibSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = atvLocation.getText().toString().trim();
                if(!city.equals("")) {
                    Log.d("getKEYFromSearch","from search");
                    getLocationCode(city, false);

                }
            }
        });
        this.ibMyLocation = (ImageButton) layout.findViewById(R.id.frLPibCurrentLocation);
        this.ibMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startGetCurrentLocation();
            }
        });
        this.atvLocation = (AutoCompleteTextView) layout.findViewById(R.id.frLPaetLocation);
        this.ibHome= (ImageButton) layout.findViewById(R.id.frLPibHome);
        this.ibHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getKeyFromUserLocation();
            }
        });
    }

    //Na temelju korisničke lokacije prilikom registracije se dobivaju podaci o peludi
    //ukoliko ne postoji zapis o Key-u u SQLite bazi, traži se od API-ja
    private void getKeyFromUserLocation() {
        if(!mUser.getmLocation().getmKey().equals("")){
            Log.d("getKEYIF","getPOllenFromSQLiteKEY");
            getPollenData(mUser.getmLocation().getmKey());
        }
        else{
            Log.d("getKEYIF","noSQLiteKey");
            getLocationCode(mUser.getmLocation().getmCity(), true);
        }
    }

    //Prilikom pritiska na gumb za trenutnu lokaciju se traži trenutna lokacija i na temelju nje se dobivaju
    //podaci o peludi
    private void startGetCurrentLocation() {
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if(hasLocationPermission()) {
            if (checkGPSEnabled()) {
                mLocationListener = new CurrentLocationListener();
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                String locationProvider = mLocationManager.getBestProvider(criteria, true);
                mLocationManager.requestLocationUpdates(locationProvider, 0, 0, mLocationListener);
            } else {
                Toast.makeText(getActivity(), GPS_DISABLED, Toast.LENGTH_SHORT).show();
            }
        }else Toast.makeText(getActivity(), NO_PERMISSION, Toast.LENGTH_SHORT).show();
    }

    private Location getLastKnownLocation() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = mLocationManager.getBestProvider(criteria, true);
        Location location = mLocationManager.getLastKnownLocation(locationProvider);
        return location;
    }

    private boolean hasLocationPermission(){
        String LocationPermission = android.Manifest.permission.ACCESS_FINE_LOCATION;
        int status = ContextCompat.checkSelfPermission(getActivity(),LocationPermission);
        if(status == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }

    private boolean checkGPSEnabled() {

        if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            return true;
        }
        return false;
    }

    //metoda za traženje Key-a od API-ja na temelju grada, boolean je da se zna jel se traži na temelju korisničke
    //lokacije iz registracije ili na temelju upisa u EditBox
    private void getLocationCode(String city, final boolean isUserLocation) {
        Log.d("SHOWDIALOG","getlocationcode");
       // showDialog();
        String tag_str_req = "req_location_code";


        String url = "http://dataservice.accuweather.com/locations/v1/cities/search?apikey="+MainActivity.ApiKey+"&q="+city;
        JsonObjectRequest getRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        //odlazi na error response jer je odziv zippan (gzip), pa ga ne prepoznaje kao JSON objekt
                        //API kao vraća JSON, ali nije u dobrom formatu pa ga je potrebno prepraviti
                        Log.d("Response",response.toString());
                       // hideDialog();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response",error.toString());
                      //  hideDialog();
                        mAccuCities.clear();
                        parseJSONCityList(error.toString());
                        if(!mAccuCities.isEmpty()){
                            if(isUserLocation){
                                Log.d("getKEYIF","in is user action "+mAccuCities.get(0).getmAdministrativeArea());
                                for(int i=0;i<mAccuCities.size();i++){
                                    if(mAccuCities.get(i).getmAdministrativeArea().equals(mUser.getmLocation().getmState())||
                                            mAccuCities.get(i).getmCountry().equals(mUser.getmLocation().getmCountry())){
                                        Log.d("getKEYIF","in if is user action "+mAccuCities.get(0).getmAdministrativeArea());
                                        String key=mAccuCities.get(i).getmKey();
                                        mUser.getmLocation().setmKey(key);
                                        UserDBHelper.getInstance(getActivity()).insertLocation(mUser.getmLocation());
                                        getPollenData(key);
                                        atvLocation.setText(mAccuCities.get(i).toString());
                                        Log.d("getKEYIF",key);
                                        break;
                                    }
                                }
                            }else {
                                showListDialog();
                            }
                        }else
                            Toast.makeText(getActivity(),"Something went wrong, please try again!",Toast.LENGTH_SHORT).show();
                    }
                });
        AppController.getInstance().addToRequestQueue(getRequest,tag_str_req);
    }

    private void showListDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pick one city");
        ArrayList<String> cityStrings=new ArrayList<>();
        for(int i=0;i<mAccuCities.size();i++){
            cityStrings.add(mAccuCities.get(i).toString());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,cityStrings);
        builder.setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                atvLocation.setText(mAccuCities.get(which).toString());
                getPollenData(mAccuCities.get(which).getmKey());
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void getPollenData(String locationKey) {
        Log.d("SHOWDIALOG","getpollendata");
       // showDialog();
        String tag_str_req = "req_pollen_code";
        String url = "http://dataservice.accuweather.com/forecasts/v1/daily/5day/"+locationKey+"?apikey="+MainActivity.ApiKey+"&details=true";
        JsonObjectRequest getRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                       // hideDialog();
                        Log.d("Response",response.toString());
                        mAccuPollens.clear();
                        parseJSONPollenData(response);
                        setRecyclerView();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response",error.toString());
                       // hideDialog();
                    }
                });
        AppController.getInstance().addToRequestQueue(getRequest,tag_str_req);
    }

    private void setRecyclerView() {
        Context context = getActivity();
        this.mRvPollenData.setAdapter(null);
        this.mPollenCountAdapter = new PollenCountAdapter(mAccuPollens,context);
        this.mLayoutManager = new LinearLayoutManager(context);
        this.mItemDecoration = new DividerItemDecoration(context,DividerItemDecoration.VERTICAL);
        this.mRvPollenData.addItemDecoration(this.mItemDecoration);
        this.mRvPollenData.setLayoutManager(mLayoutManager);
        this.mRvPollenData.setAdapter(mPollenCountAdapter);
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
            for(int i=0;i<5;i++) {
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

    private void parseJSONCityList(String response) {
        //prepravljanje odziva (javlja se greška da se ne može parsirati jer odziv nije dobrog formata,
        //pa je potrebno "urediti" JSON string, tj obrisati opis greške i formatirati string
        response = response.replace("com.android.volley.ParseError: org.json.JSONException: Value","");
        response= response.replace(" of type org.json.JSONArray cannot be converted to JSONObject","");
        response = "{\"city\":"+response+"}";
        JSONObject jObj = null;
        JSONArray jArray = null;

        try {
            jObj = new JSONObject(response);
            jArray = jObj.getJSONArray("city");

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jo = jArray.getJSONObject(i);
                AccuCity accuCity = parseJSONCity(jo);
                mAccuCities.add(accuCity);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /*private void showDialog(){
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Fetching location data");
        progressDialog.setMessage("Please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    private void hideDialog(){
        progressDialog.dismiss();
    }*/

    private void searchForLocation(Location location) {
        Log.d("SHOWDIALOG","searchforlocation");
       // showDialog();
        String tag_str_req = "req_city_code";
        String url = "http://dataservice.accuweather.com/locations/v1/cities/geoposition/search?apikey="+MainActivity.ApiKey+"&q="+location.getLatitude()+
                "%2C"+location.getLongitude()+"&toplevel=true";
        JsonObjectRequest getRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response",response.toString());
                        AccuCity currentCity = parseJSONCity(response);
                        if(currentCity!=null){
                            getPollenData(currentCity.getmKey());
                            atvLocation.setText(currentCity.toString());
                        }
                      //  hideDialog();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response",error.toString());
                     //   hideDialog();
                        Toast.makeText(getActivity(),"Something went wrong, please try again!",Toast.LENGTH_SHORT).show();
                    }
                });
        AppController.getInstance().addToRequestQueue(getRequest,tag_str_req);
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

    private class CurrentLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            searchForLocation(location);
            mLocationManager.removeUpdates(mLocationListener);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }


}
