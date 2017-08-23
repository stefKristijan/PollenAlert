package hr.ferit.kstefancic.pollenalert;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
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

/**
 * Created by Kristijan on 22.8.2017..
 */

public class LocationPollenFragment extends Fragment{


    private ImageButton ibSearch, ibMyLocation;
    private AutoCompleteTextView atvLocation;
    private CardView cvPollen;
    private ProgressDialog progressDialog;
    private ArrayList<String> mCityStrings;
    private ArrayList<AccuCity> mAccuCities;
    private ArrayList<AccuPollenForecast> mAccuPollens;
    private RecyclerView mRvPollenData;
    private PollenCountAdapter mPollenCountAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.ItemDecoration mItemDecoration;
    public static final int GRASS =0, MOLD = 1, WEED =2, TREE=3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_location_pollen,container,false);
        setUpUI(layout);
        return layout;
    }

    private void setUpUI(View layout) {
        this.mRvPollenData = (RecyclerView) layout.findViewById(R.id.frLPrvPollenData);
        this.mAccuPollens = new ArrayList<>();
        this.mCityStrings = new ArrayList<>();
        this.mAccuCities = new ArrayList<>();
        this.ibSearch = (ImageButton) layout.findViewById(R.id.frLPibSearch);
        this.ibSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocationCode();
            }
        });
        this.ibMyLocation = (ImageButton) layout.findViewById(R.id.frLPibCurrentLocation);
        this.atvLocation = (AutoCompleteTextView) layout.findViewById(R.id.frLPaetLocation);
    }

    private void getLocationCode() {
        showDialog();
        String tag_str_req = "req_location_code";
        String city = atvLocation.getText().toString().trim();

        String url = "http://dataservice.accuweather.com/locations/v1/cities/search?apikey="+MainActivity.ApiKey+"&q="+city;
       /* Request getRequest = new Request(url, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Response",error.toString());
                hideDialog();
            }
        }) {
            @Override
            protected Response parseNetworkResponse(NetworkResponse response) {
                int BUFFER_SIZE = 32;
                String encoding = response.headers.get("Content-Encoding");
                StringBuilder string = new StringBuilder();
                if(encoding != null && encoding.equals("gzip")) {
                    try {
                        ByteArrayInputStream is = new ByteArrayInputStream(response.data);
                        GZIPInputStream gis = null;
                        gis = new GZIPInputStream(is);
                        byte[] data = new byte[BUFFER_SIZE];
                        int bytesRead;
                        while ((bytesRead = gis.read(data)) != -1) {
                            string.append(new String(data, 0, bytesRead));
                        }
                        gis.close();
                        is.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return Response.success(string.toString(), HttpHeaderParser.parseCacheHeaders(response));
            }

            @Override
            protected void deliverResponse(Object response) {
                Log.d("Response",response.toString());
                hideDialog();
            }

            @Override
            public int compareTo(@NonNull Object o) {
                return 0;
            }
        };*/
        JsonObjectRequest getRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        //odlazi na error response jer je odziv zippan (gzip), pa ga ne prepoznaje kao JSON objekt
                        //API kao vraća JSON, ali nije u dobrom formatu pa ga je potrebno prepraviti
                        Log.d("Response",response.toString());
                        hideDialog();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response",error.toString());
                        hideDialog();
                        mCityStrings.clear();
                        mAccuCities.clear();
                        parseJSONCity(error.toString());
                        if(!mCityStrings.isEmpty()){
                            showListDialog();
                        }else
                            Toast.makeText(getActivity(),"Something went wrong, please try again!",Toast.LENGTH_SHORT).show();
                    }
                });
        AppController.getInstance().addToRequestQueue(getRequest,tag_str_req);
    }

    private void showListDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pick one city");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,mCityStrings);
        builder.setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                atvLocation.setText(mCityStrings.get(which));
                getPollenData(mAccuCities.get(which).getmKey());
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void getPollenData(String locationKey) {
        showDialog();
        String tag_str_req = "req_pollen_code";
        String url = "http://dataservice.accuweather.com/forecasts/v1/daily/5day/"+locationKey+"?apikey="+MainActivity.ApiKey+"&details=true";
        JsonObjectRequest getRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response",response.toString());
                        mAccuPollens.clear();
                        parseJSONPollenData(response);
                        setRecyclerView();
                        hideDialog();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response",error.toString());
                        hideDialog();
                    }
                });/*
        StringRequest getRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response",response.toString());
                parseJSONPollenData(response);
                hideDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error.Response",error.toString());
                hideDialog();
            }
        });*/
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

    private void parseJSONCity(String response) {
        //prepravljanje odziva (javlja se greška da se ne može parsirati jer odziv nije dobrog formata,
        //pa je potrebno "urediti" JSON string, tj obrisati opis greške i formatirati string
        response = response.replace("com.android.volley.ParseError: org.json.JSONException: Value","");
        response= response.replace(" of type org.json.JSONArray cannot be converted to JSONObject","");
        response = "{\"city\":"+response+"}";
        JSONObject jObj = null;
        JSONArray jArray = null;
        JSONObject joAdmin = null;
        JSONObject joGeoPosition =  null;
        JSONObject joRegion = null;
        JSONObject joCountry = null;
        try {
            jObj = new JSONObject(response);
            jArray = jObj.getJSONArray("city");
            String city, adminArea, country, region, key;
            double latitude, longitude;
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jo = jArray.getJSONObject(i);
                city = jo.getString("LocalizedName");
                key = jo.getString("Key");
                joAdmin = new JSONObject(jo.getString("AdministrativeArea"));
                adminArea = joAdmin.getString("LocalizedName");
                joCountry = new JSONObject(jo.getString("Country"));
                country = joCountry.getString("LocalizedName");
                joGeoPosition = new JSONObject(jo.getString("GeoPosition"));
                latitude = joGeoPosition.getDouble("Latitude");
                longitude = joGeoPosition.getDouble("Longitude");
                joRegion = new JSONObject(jo.getString("Region"));
                region = joRegion.getString("LocalizedName");
                AccuCity accuCity = new AccuCity(city,key,country,adminArea,region);
                accuCity.setmLatitude((float) latitude);
                accuCity.setmLongitude((float) longitude);
                mAccuCities.add(accuCity);
                mCityStrings.add(accuCity.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void showDialog(){
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Fetching location data");
        progressDialog.setMessage("Please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    private void hideDialog(){
        progressDialog.dismiss();
    }
}
