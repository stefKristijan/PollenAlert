package hr.ferit.kstefancic.pollenalert.registrationAndLogin;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hr.ferit.kstefancic.pollenalert.AppController;
import hr.ferit.kstefancic.pollenalert.Location;
import hr.ferit.kstefancic.pollenalert.MainActivity;
import hr.ferit.kstefancic.pollenalert.Pollen;
import hr.ferit.kstefancic.pollenalert.R;
import hr.ferit.kstefancic.pollenalert.User;
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
    private static final String URL_ADDPOLLEN = "https://pollenalert.000webhostapp.com/insert_pollen.php";
    private static final String ADDPOLLEN_SUCCESS = "Allergie information successfully added!";
    private User mUser;
    private Location mLocation;
    private ArrayList<Pollen> mPollenList;
    private ProgressDialog progressDialog;
    private SessionManager mSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        checkIfLoggedIn();
        setUpFragment();

    }

    private void checkIfLoggedIn() {
        this.mSessionManager = new SessionManager(this);
       /* this.mSessionManager.setLogin(false);
        UserDBHelper.getInstance(this).deleteAllergies();
        UserDBHelper.getInstance(this).deleteUser();
        UserDBHelper.getInstance(this).deleteLocation();*/
        if(this.mSessionManager.isLoggedIn()){
            mUser = UserDBHelper.getInstance(this).getUser();
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
        this.mPollenList = new ArrayList<>();
        this.mPollenList = pollenList;
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

    private String encodeImageToString(Bitmap image){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image = scaleImage(image);
        image.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte [] imgBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes,Base64.DEFAULT);
    }

    private Bitmap scaleImage(Bitmap image) {

        float originalWidth = image.getWidth();
        float originalHeight = image.getHeight();

        int width= (int) originalWidth, height= (int) originalHeight;

        for(int i=2;height>200;i++){
            width = (int) (originalWidth/i);
            height = (int) (originalHeight/i);
        }

        Bitmap scaledImage = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(scaledImage);

        float scale = width / originalWidth;

        float xTranslation = 0.0f;
        float yTranslation = (height - originalHeight * scale) / 2.0f;

        Matrix transformation = new Matrix();
        transformation.postTranslate(xTranslation, yTranslation);
        transformation.preScale(scale, scale);

        Paint paint = new Paint();
        paint.setFilterBitmap(true);

        canvas.drawBitmap(image, transformation, paint);
        return scaledImage;
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
                params.put("image",encodeImageToString(mUser.getmAvatar()));

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
                        //Toast.makeText(FirstActivity.this, ADDLOCATION_SUCCESS,Toast.LENGTH_SHORT).show();
                        addPollenToDatabase();
                    }
                    else{
                       // Toast.makeText(FirstActivity.this,jsonObject.getString("error_msg"),Toast.LENGTH_SHORT).show();
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

    private void addPollenToDatabase() {
        showDialog("Adding your allergies");
        String tag_str_req = "addPollen_req";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_ADDPOLLEN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("POLLEN_RESPONSE",response.toString());
               /* boolean error = false;
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jArray = jsonObject.getJSONArray("responses");
                    for(int i=0;i<jArray.length();i++) {
                        if (!jArray.getJSONObject(i).getBoolean("responses")) {
                            error=true;
                            break;
                        }
                    }
                    if(!error){
                        Toast.makeText(FirstActivity.this, ADDPOLLEN_SUCCESS,Toast.LENGTH_SHORT).show();
                        setUpFragment();
                    }
                    else{
                        Toast.makeText(FirstActivity.this,jsonObject.getString("error_msg"),Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
                hideDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("RESPONSE","Pollen insert error: "+ error.getMessage());
                Toast.makeText(FirstActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                hideDialog();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String,String>();
                for(int i=0; i<mPollenList.size();i++){
                    params.put("pollen_id["+i+"]",String.valueOf(mPollenList.get(i).getId()));
                }
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
        UserDBHelper.getInstance(this).insertUser(user);
        mSessionManager.setLogin(true);
        Intent mainIntent = new Intent(FirstActivity.this,MainActivity.class);
        mainIntent.putExtra(USER,user);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
    }

    @Override
    public void onOfflineAccountCreated(User user, Location location) {
        Toast.makeText(this,OFFLINE_ACC_SUCCESS,Toast.LENGTH_SHORT).show();
        UserDBHelper.getInstance(this).insertUser(user);
        //mUserDBHelper.getInstance(this).insertLocation(location);
        mSessionManager.setLogin(true);
        Intent mainIntent = new Intent(FirstActivity.this,MainActivity.class);
        mainIntent.putExtra(USER,user);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
    }


}
