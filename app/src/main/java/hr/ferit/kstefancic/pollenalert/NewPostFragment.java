package hr.ferit.kstefancic.pollenalert;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.*;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import hr.ferit.kstefancic.pollenalert.helper.MultiSpinner;
import hr.ferit.kstefancic.pollenalert.registrationAndLogin.FirstActivity;
import hr.ferit.kstefancic.pollenalert.registrationAndLogin.SignUpFragment3;

/**
 * Created by Kristijan on 18.9.2017..
 */

public class NewPostFragment extends Fragment implements View.OnClickListener {


    private static final String GPS_DISABLED = "Please enable GPS to use this feature!";
    private static final String NO_PERMISSION = "You must allow the application to use location to use this feature!";
    private static final String USER_KEY = "user";
    private static final String URL_INSERT_POST = "http://pollenalert.000webhostapp.com/insert_post.php";
    private static final String INSERT_SUCCESS = "Post inserted successfully!";
    private User mUser;
    private MultiSpinner multiSpinner;
    private TextView tvUserName, tvLocation;
    //private TextView tvVeryGood, tvGood, tvNeutral, tvBad, tvVeryBad, tvACold, tvSick, tvVerySick;
    private ImageView ivVeryGood, ivGood, ivNeutral, ivBad, ivVeryBad, ivACold, ivSick, ivVerySick, ivAvatar;
    private TableLayout tlEmojis;
    List<String> symptoms;
    private Button btnPost;
    private Location currentLocation;
    private String currentCity, currentState, currentCountry;
    private StringBuilder sbSymptoms;
    private InsertListener mInsertListener;

    public static NewPostFragment newInstance(User user) {
        NewPostFragment fragment = new NewPostFragment();
        Bundle bundle = new Bundle();

        bundle.putSerializable(USER_KEY, user);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mUser = (User) getArguments().getSerializable(USER_KEY);
        View layout = inflater.inflate(R.layout.fragment_new_post, container, false);
        setUpUI(layout);
        return layout;
    }

    private void setUpUI(View layout) {
        startGetCurrentLocation();
        this.ivAvatar = (ImageView) layout.findViewById(R.id.newPost_ivAvatar);
        Picasso.with(getActivity())
                .load(mUser.getmAvatarPath())
                .into(this.ivAvatar);
        this.tvUserName = (TextView) layout.findViewById(R.id.newPost_tvUser);
        this.tvUserName.setText(mUser.getmUsername());
        this.tvLocation = (TextView) layout.findViewById(R.id.newPost_tvLocation);
        this.tvLocation.setText(mUser.getmLocation().toString());
        setUpEmoticonGrid(layout);
        multiSpinner = (MultiSpinner) layout.findViewById(R.id.newPost_spinnerSymptoms);
        symptoms = Arrays.asList(getResources().getStringArray(R.array.symptoms));
        multiSpinner.setItems(symptoms, "Select symptoms", new MultiSpinner.MultiSpinnerListener() {
            @Override
            public void onItemsSelected(boolean[] selected) {
                sbSymptoms = new StringBuilder();
                String sufix = ",";
                for (int i = 0; i < selected.length; i++) {
                    if (selected[i]) {
                        if (i != selected.length - 1) {
                            sbSymptoms.append(symptoms.get(i));
                            sbSymptoms.append(sufix);
                        } else {
                            sbSymptoms.append(symptoms.get(i));
                        }
                    }
                }

            }
        });
        btnPost = (Button) layout.findViewById(R.id.newPost_btnPost);
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String feeling = getEmojiText();
                insertIntoDatabase(feeling);
            }
        });
    }

    private void insertIntoDatabase(final String feeling) {
        String tag_str_req = "insert_post_req";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_INSERT_POST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("POST_RESPONSE", response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    if (!error) {
                        Toast.makeText(getActivity(), INSERT_SUCCESS, Toast.LENGTH_SHORT).show();
                        mInsertListener.onInsertPost();
                    } else {
                        Toast.makeText(getActivity(), jsonObject.getString("error_msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("RESPONSE", "Register error: " + error.getMessage());
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("feeling", feeling);
                params.put("symptoms", sbSymptoms.toString());
                if (currentCity != null) {
                    params.put("city", currentCity);
                    params.put("state", currentState);
                    params.put("country", currentCountry);
                    params.put("longitude", String.valueOf(currentLocation.getLongitude()));
                    params.put("latitude", String.valueOf(currentLocation.getLatitude()));
                } else {
                    params.put("city", mUser.getmLocation().getmCity());
                    params.put("state", mUser.getmLocation().getmState());
                    params.put("country", mUser.getmLocation().getmCountry());
                    params.put("longitude", "0");
                    params.put("latitude", "0");
                }
                params.put("user_id", String.valueOf(mUser.getId()));

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest, tag_str_req);
    }

    private String getEmojiText() {
        String emojiText = "";
        for (int i = 0; i < 4; i = i + 2) {
            TableRow row = (TableRow) tlEmojis.getChildAt(i);
            for (int j = 0; j < 4; j++) {
                ImageView iv = (ImageView) row.getChildAt(j);
                ColorDrawable color = (ColorDrawable) iv.getBackground();
                if (color.getColor() == getResources().getColor(R.color.colorLightGreen)) {
                    TableRow rowTv = (TableRow) tlEmojis.getChildAt(i + 1);
                    TextView tvText = (TextView) rowTv.getChildAt(j);
                    emojiText = tvText.getText().toString();
                }
            }
        }
        return emojiText;
    }

    private void setUpEmoticonGrid(View layout) {
        tlEmojis = (TableLayout) layout.findViewById(R.id.newPost_tableEmojis);

      /*  tvACold = (TextView) layout.findViewById(R.id.newPost_tvACold);
        tvBad = (TextView) layout.findViewById(R.id.newPost_tvBad);
        tvGood= (TextView) layout.findViewById(R.id.newPost_tvGood);
        tvNeutral= (TextView) layout.findViewById(R.id.newPost_tvNeutral);
        tvVeryBad = (TextView) layout.findViewById(R.id.newPost_tvVeryBad);
        tvVeryGood = (TextView) layout.findViewById(R.id.newPost_tvVeryGood);
        tvVerySick = (TextView) layout.findViewById(R.id.newPost_tvVerySick);
        tvSick = (TextView) layout.findViewById(R.id.newPost_tvSick);*/

        ivACold = (ImageView) layout.findViewById(R.id.newPost_ivACold);
        ivACold.setOnClickListener(this);
        ivBad = (ImageView) layout.findViewById(R.id.newPost_ivBad);
        ivBad.setOnClickListener(this);
        ivGood = (ImageView) layout.findViewById(R.id.newPost_ivGood);
        ivGood.setOnClickListener(this);
        ivNeutral = (ImageView) layout.findViewById(R.id.newPost_ivNeutral);
        ivNeutral.setOnClickListener(this);
        ivVeryBad = (ImageView) layout.findViewById(R.id.newPost_ivVeryBad);
        ivVeryBad.setOnClickListener(this);
        ivVeryGood = (ImageView) layout.findViewById(R.id.newPost_ivVeryGood);
        ivVeryGood.setOnClickListener(this);
        ivVerySick = (ImageView) layout.findViewById(R.id.newPost_ivVerySick);
        ivVerySick.setOnClickListener(this);
        ivSick = (ImageView) layout.findViewById(R.id.newPost_ivSick);
        ivSick.setOnClickListener(this);
    }

    private void startGetCurrentLocation() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (hasLocationPermission()) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                LocationListener locationListener = new CurrentLocationListener();
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                String locationProvider = locationManager.getBestProvider(criteria, true);
                locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);
            } else {
                tvLocation.setText(mUser.getmLocation().toString());
            }
        } else tvLocation.setText(mUser.getmLocation().toString());
    }

    private boolean hasLocationPermission() {
        String LocationPermission = android.Manifest.permission.ACCESS_FINE_LOCATION;
        int status = ContextCompat.checkSelfPermission(getActivity(), LocationPermission);
        if (status == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        for (int i = 0; i < 4; i = i + 2) {
            TableRow row = (TableRow) tlEmojis.getChildAt(i);
            for (int j = 0; j < 4; j++) {
                ImageView iv = (ImageView) row.getChildAt(j);
                iv.setBackgroundResource(R.color.colorWhite);
            }
        }
        v.setBackgroundResource(R.color.colorLightGreen);
    }

    private class CurrentLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            currentLocation = location;
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            try {
                List<Address> nearAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                Address address = nearAddresses.get(0);
                currentCity = address.getLocality();
                currentState = address.getAdminArea();
                currentCountry = address.getCountryName();
                tvLocation.setText(currentCity + ", " + currentState + ", " + currentCountry);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof InsertListener)
        {
            this.mInsertListener = (InsertListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.mInsertListener=null;
    }

    public interface InsertListener{
        void onInsertPost();
    }
}
