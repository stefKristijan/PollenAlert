package hr.ferit.kstefancic.pollenalert;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hr.ferit.kstefancic.pollenalert.helper.SessionManager;
import hr.ferit.kstefancic.pollenalert.helper.TrackLocationService;
import hr.ferit.kstefancic.pollenalert.helper.UserDBHelper;
import hr.ferit.kstefancic.pollenalert.registrationAndLogin.FirstActivity;

public class MainActivity extends AppCompatActivity implements NewPostFragment.InsertListener {

    private static final int REQUEST_LOCATION_PERMISSION = 10;
    private static final String URL_GET_LOCATION = "http://pollenalert.000webhostapp.com/get_user_location.php";
    private static final String URL_USER_ALLERGIES = "http://pollenalert.000webhostapp.com/get_user_allergies.php";
    private static final String USER_KEY = "user";
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private User mUser;
    private int[] tabIcons={
            R.mipmap.location_icon,
            R.mipmap.news,
            R.mipmap.new_post,
            R.mipmap.my_diary_icon
    };
    private SessionManager mSessionManager;
    public static final String ApiKey ="gP4M9GSljRr7BrbSVA22r447bUnhRQXL"; //"" eIswG7hdAtgPUincnaJgb8SuUaQzS45R";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUser = (User) getIntent().getSerializableExtra(FirstActivity.USER);
        getUserDataFromDatabase();

    }

    private void getUserDataFromDatabase() {
        Location location = UserDBHelper.getInstance(this).getLocation();
        ArrayList<Pollen> allergies = UserDBHelper.getInstance(this).getAllergies();
        if(location!=null && allergies!=null){
            Log.d("dataFromDatabase",location.getmCity()+" "+allergies.get(0).getName());
            mUser.setmLocation(location);
            mUser.setmAllergies(allergies);
            Intent serviceIntent = new Intent(this,TrackLocationService.class);
            serviceIntent.putExtra(USER_KEY,mUser.getmAllergies());
            startService(serviceIntent);
            setUpUI();
        }else{
            getLocationFromServer();
            Log.d("dataFromDatabase","from server");
        }
    }

    private void getLocationFromServer() {
        showDialog();
        String tag_str_req="req_user_location";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_GET_LOCATION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("RESPONSE",response.toString());
                parseJSONLocation(response);
                getUserAllergies();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("RESPONSE","Data error: "+ error.getMessage());
                Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
                hideDialog();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String,String>();
                params.put("user_id",String.valueOf(mUser.getId()));
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest,tag_str_req);
    }


    private void getUserAllergies() {
        String tag_str_req = "req_user_allergies";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_USER_ALLERGIES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("RESPONSE",response.toString());
                parseJSONAllergies(response);
                Intent serviceIntent = new Intent(MainActivity.this,TrackLocationService.class);
                serviceIntent.putExtra(USER_KEY,mUser.getmAllergies());
                startService(serviceIntent);
                setUpUI();
                hideDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("RESPONSE","Data error: "+ error.getMessage());
                hideDialog();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String,String>();
                params.put("user_id",String.valueOf(mUser.getId()));
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest,tag_str_req);

    }

    private void parseJSONAllergies(String response) {
        JSONObject jObj = null;
        JSONArray jArray = null;
        ArrayList<Pollen> allergies = new ArrayList<>();
        try {
            jObj = new JSONObject(response);
            boolean error = jObj.getBoolean("error");

            if(!error){
                jArray = jObj.getJSONArray("pollen_data");
                for(int i=0;i<jArray.length();i++){
                    JSONObject jo = jArray.getJSONObject(i);
                    Pollen pollen = new Pollen(jo.getInt("id"),jo.getString("name"),jo.getString("category"));
                    allergies.add(pollen);
                    Log.d("Serverpollen",pollen.getName());
                }
            }
            mUser.setmAllergies(allergies);
            UserDBHelper.getInstance(this).insertAllergies(allergies);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void parseJSONLocation(String response) {
        try {
            JSONObject jObj = new JSONObject(response);
            boolean error = jObj.getBoolean("error");

            if(!error){
                String street = jObj.getString("street");
                String street_num = jObj.getString("street_num");
                String city = jObj.getString("city");
                String state = jObj.getString("state");
                String country = jObj.getString("country");
                Location location = new Location(street,city,country,state,street_num);
                Log.d("locationSeerve",location.getmCity());
                mUser.setmLocation(location);
                UserDBHelper.getInstance(this).insertLocation(location);
            }
            else{
                String errorMsg = jObj.getString("error_msg");
                Toast.makeText(this,errorMsg,Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void showDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Fetching location data");
        progressDialog.setMessage("Please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    private void hideDialog(){
        progressDialog.dismiss();
    }

    private void setUpUI() {
        this.mSessionManager = new SessionManager(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        setUpViewPager();
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        setUpTabIcons();
    }

    private void setUpViewPager() {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        Fragment userNewsFr = UserNewsFragment.newInstance(mUser);
        Fragment locationPollenFr = LocationPollenFragment.newInstance(mUser);
        Fragment newPostFr = NewPostFragment.newInstance(mUser);
        adapter.addFragment(locationPollenFr);
        adapter.addFragment(userNewsFr);
        adapter.addFragment(newPostFr);
        //adapter.addFragment(new MyDiaryFragment());
        mViewPager.setAdapter(adapter);
    }

    private void setUpTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        //tabLayout.getTabAt(3).setIcon(tabIcons[3]);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!hasLocationPermission()){
            requestPermission();
        }
    }

    private boolean hasLocationPermission(){
        String LocationPermission = Manifest.permission.ACCESS_FINE_LOCATION;
        int status = ContextCompat.checkSelfPermission(this,LocationPermission);
        if(status == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }
    private void requestPermission(){
        String[] permissions = new String[]{ Manifest.permission.ACCESS_FINE_LOCATION };
        ActivityCompat.requestPermissions(MainActivity.this,
                permissions, REQUEST_LOCATION_PERMISSION);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_LOCATION_PERMISSION:
                if(grantResults.length >0){
                    if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                        Log.d("Permission","Permission granted. User pressed allow.");
                    }
                    else{
                        Log.d("Permission","Permission not granted. User pressed deny.");
                        askForPermission();
                    }
                }
        }
    }
    private void askForPermission(){
        boolean shouldExplain = ActivityCompat.shouldShowRequestPermissionRationale(
                MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(shouldExplain){
            Log.d("Permission","Permission should be explained, - don't show again not clicked.");
            this.displayDialog();
        }
        else{
            Log.d("Permission","Permission not granted. User pressed deny and don't show again.");
            //tvLocationDisplay.setText("Sorry, we really need that permission");
        }
    }
    private void displayDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Location permission")
                .setMessage("Your location is needed for showing local pollen information.")
                .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("Permission", "User declined and won't be asked again.");
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("Permission","Permission requested because of the explanation.");
                        requestPermission();
                        dialog.cancel();
                    }
                })
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sign_out) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setTitle("Sign out")
                    .setMessage("Do you really want to sign out?")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setPositiveButton("Sign out", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                           signOut();
                        }
                    })
                    .show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        Intent myService = new Intent(MainActivity.this, TrackLocationService.class);
        stopService(myService);
        this.mSessionManager.setLogin(false);
        UserDBHelper.getInstance(this).deleteLocation();
        UserDBHelper.getInstance(this).deleteAllergies();
        UserDBHelper.getInstance(this).deleteUser();
        Intent loginIntent = new Intent(MainActivity.this,FirstActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
    }

    @Override
    public void onInsertPost() {
        mViewPager.setCurrentItem(1);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment){
            mFragmentList.add(fragment);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }

}
