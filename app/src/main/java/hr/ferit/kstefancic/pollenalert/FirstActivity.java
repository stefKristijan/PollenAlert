package hr.ferit.kstefancic.pollenalert;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hr.ferit.kstefancic.pollenalert.helper.SessionManager;
import hr.ferit.kstefancic.pollenalert.helper.UserDBHelper;

public class FirstActivity extends AppCompatActivity implements LogInFragment.LoggedInListener,
        SignUpFragment1.UserCreatedListener, SignUpFragment2.LocationCreatedListener,
        SignUpFragment3.FinishListener, CreateAccFragment.OfflineAccountCreatedListener {

    private static final String LOGIN_FRAGMENT = "login";
    private static final String URL_REGISTER = "https://pollenalert.000webhostapp.com/register.php";
    private static final String REGISTRATION_SUCCESS = "Successfully registered, you can now log in!";
    private static final String URL_ADDLOCATION = "https://pollenalert.000webhostapp.com/insert_location.php";
    private static final String ADDLOCATION_SUCCESS = "Location successfully added to database!";
    public static final String USER = "user";
    private static final String LOGIN_SUCCESS = "You were successfully logged in!";
    private static final String OFFLINE_ACC_SUCCESS = "You are successfully logged in to your offline account!";
    private User mUser;
    private Location mLocation;
    private ArrayList<Pollen> mPollenList;
    private ProgressDialog progressDialog;
    private SessionManager mSessionManager;
    private UserDBHelper mUserDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        checkIfLoggedIn();
        setUpFragment();

    }

    private void checkIfLoggedIn() {
        this.mSessionManager = new SessionManager(this);
        if(this.mSessionManager.isLoggedIn()){
            mUserDBHelper = new UserDBHelper(this);
            mUser = mUserDBHelper.getUser();
            Intent mainIntent = new Intent(FirstActivity.this,MainActivity.class);
            mainIntent.putExtra(USER, mUser);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
        }
    }

    private void setUpFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.activityFirst_fl, new LogInFragment(), this.LOGIN_FRAGMENT);
        fragmentTransaction.commit();
    }

    @Override
    public void onUserCreated(User user){
        mUser = user;
        Log.d("ONUSER",mUser.getmEmail());
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.activityFirst_fl, new SignUpFragment2());
        fragmentTransaction.commit();
    }

    @Override
    public void onLocationCreated(Location location){
        mLocation=location;

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.activityFirst_fl, new SignUpFragment3());
        fragmentTransaction.commit();
    }


    @Override
    public void onFinish(ArrayList<Pollen> pollenList) {
        //mPollenList = pollenList;
        register();
    }

    private void register() {
        progressDialog = new ProgressDialog(FirstActivity.this);
        showDialog("Registering");
        registerUser();
        setUpFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.activityFirst_fl, new LogInFragment());
        fragmentTransaction.commit();
    }

    private void registerUser() {
        String tag_str_req = "register_req";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGISTER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("REG_RESPONSE",response.toString());
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    if(!error){
                        mUser.setId(jsonObject.getInt("id"));
                        Toast.makeText(FirstActivity.this, REGISTRATION_SUCCESS,Toast.LENGTH_SHORT).show();
                        addLocationToDatabase();
                    }
                    else{
                        Toast.makeText(FirstActivity.this,jsonObject.getString("error_msg"),Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                hideDialog();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("RESPONSE","Register error: "+ error.getMessage());
                Toast.makeText(FirstActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                hideDialog();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String,String>();
                params.put("username", mUser.getmUsername());
                params.put("password",mUser.getmPassword());
                params.put("email",mUser.getmEmail());

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest,tag_str_req);
    }

    private void addLocationToDatabase() {
        showDialog("Adding location");
        String tag_str_req = "addLocation_req";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_ADDLOCATION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("LOCATION_RESPONSE",response.toString());
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    if(!error){
                        Toast.makeText(FirstActivity.this, ADDLOCATION_SUCCESS,Toast.LENGTH_SHORT).show();
                        setUpFragment();
                    }
                    else{
                        Toast.makeText(FirstActivity.this,jsonObject.getString("error_msg"),Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                hideDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("RESPONSE","Location insert error: "+ error.getMessage());
                Toast.makeText(FirstActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                hideDialog();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String,String>();
                params.put("street",mLocation.getmStreet());
                params.put("street_num",mLocation.getmNumber());
                params.put("city",mLocation.getmCity());
                params.put("state",mLocation.getmState());
                params.put("country",mLocation.getmCountry());
                params.put("user_id",String.valueOf(mUser.getId()));
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest,tag_str_req);

    }

    private void showDialog(String title){
        progressDialog.setTitle(title);
        progressDialog.setMessage("Please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    private void hideDialog(){
        progressDialog.dismiss();
    }

    @Override
    public void onLoggedIn(User user) {
        Toast.makeText(this,LOGIN_SUCCESS,Toast.LENGTH_SHORT).show();
        mUserDBHelper = new UserDBHelper(this);
        mUserDBHelper.insertUser(user);
        mSessionManager.setLogin(true);
        Intent mainIntent = new Intent(FirstActivity.this,MainActivity.class);
        mainIntent.putExtra(USER,user);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
    }

    @Override
    public void onOfflineAccountCreated(User user, Location location) {
        Toast.makeText(this,OFFLINE_ACC_SUCCESS,Toast.LENGTH_SHORT).show();
        mUserDBHelper = new UserDBHelper(this);
        mUserDBHelper.insertUser(user);
        mUserDBHelper.insertLocation(location);
        mSessionManager.setLogin(true);
        Intent mainIntent = new Intent(FirstActivity.this,MainActivity.class);
        mainIntent.putExtra(USER,user);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
    }
}
