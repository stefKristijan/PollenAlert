package hr.ferit.kstefancic.pollenalert;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import static hr.ferit.kstefancic.pollenalert.LogInFragment.USER;

/**
 * Created by Kristijan on 31.5.2017..
 */

public class SignUpFragment2 extends Fragment {

    private static final String EMPTY_FIELDS = "Please fill all required fields to continue!";
    private Button btnBack, btnNext;
    private EditText etStreet, etNumber, etState, etCity, etCountry;
    private LocationCreatedListener mLocationCreatedListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.sign_up_fragment2,null);
        setUI(layout);
        return layout;
    }

    private void setUI(View layout) {
        this.etCity = (EditText) layout.findViewById(R.id.signUpFr2_etCity);
        this.etState = (EditText) layout.findViewById(R.id.signUpFr2_etState);
        this.etNumber = (EditText) layout.findViewById(R.id.signUpFr2_etStreetNumber);
        this.etStreet = (EditText) layout.findViewById(R.id.signUpFr2_etStreet);
        this.etCountry = (EditText) layout.findViewById(R.id.signUpFr2_etCountry);
        this.btnBack = (Button) layout.findViewById(R.id.signUpFr2_btnBack);
        this.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.activityFirst_fl, new SignUpFragment1());
                fragmentTransaction.commit();
            }
        });
        this.btnNext= (Button) layout.findViewById(R.id.signUpFr2_btnNext);
        this.btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Location location = checkLocation();
                if(location!=null)
                mLocationCreatedListener.onLocationCreated(location);

            }
        });
    }

    private Location checkLocation() {
        String street = etStreet.getText().toString().trim();
        String country = etCountry.getText().toString().trim();
        String state = etState.getText().toString().trim();
        String number = etNumber.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        if(state.isEmpty()||street.isEmpty()||country.isEmpty()||number.isEmpty()||city.isEmpty()){
            Toast.makeText(getActivity(),EMPTY_FIELDS,Toast.LENGTH_SHORT).show();
            return null;
        }else
        {
            Location location = new Location(street,city,country,state,number);
            return location;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof LocationCreatedListener)
        {
            this.mLocationCreatedListener = (LocationCreatedListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.mLocationCreatedListener = null;
    }

    public interface LocationCreatedListener{
        void onLocationCreated(Location location);
    }
}
