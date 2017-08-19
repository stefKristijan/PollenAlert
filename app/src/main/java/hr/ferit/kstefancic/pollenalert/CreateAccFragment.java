package hr.ferit.kstefancic.pollenalert;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Kristijan on 31.5.2017..
 */

public class CreateAccFragment extends Fragment {

    private static final String EMPTY_NAME = "You must enter your name to countinue!";
    private static final String EMPTY_FIELDS ="Please fill all the required* fields to continue!" ;
    Button btnCreate;
    TextView tvLogin, tvRegister;
    EditText etFullName, etStreet, etStreetNum, etCity, etState, etCountry;
    private OfflineAccountCreatedListener mOfflineAccountCreatedListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.create_acc_fragment,null);
        setUI(layout);
        return layout;
    }

    private void setUI(View layout) {
        this.etCity = (EditText) layout.findViewById(R.id.createAccFr_etCity);
        this.etCountry = (EditText) layout.findViewById(R.id.createAccFr_etCountry);
        this.etFullName = (EditText) layout.findViewById(R.id.createAccFr_etName);
        this.etStreetNum = (EditText) layout.findViewById(R.id.createAccFr_etNum);
        this.etState = (EditText) layout.findViewById(R.id.createAccFr_etState);
        this.etStreet = (EditText) layout.findViewById(R.id.createAccFr_etStreet);
        this.btnCreate = (Button) layout.findViewById(R.id.createAccFr_btnCreate);
        this.btnCreate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                User user = getUser();
                if(!user.getmFullName().equals("")) {
                    Location location = getLocation();
                    if (location != null) {
                        mOfflineAccountCreatedListener.onOfflineAccountCreated(user, location);
                    }
                }
            }
        });

        this.tvLogin = (TextView) layout.findViewById(R.id.createAccFr_tvLogin);
        this.tvLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.activityFirst_fl, new LogInFragment());
                fragmentTransaction.commit();
            }
        });

        this.tvRegister = (TextView) layout.findViewById(R.id.createAccFr_tvRegister);
        this.tvRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.activityFirst_fl, new SignUpFragment1());
                fragmentTransaction.commit();
            }
        });
    }

    private Location getLocation() {
        Location location=null;
        String street = etStreet.getText().toString().trim();
        String streetNum = etStreetNum.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String state = etState.getText().toString().trim();
        String country = etCountry.getText().toString().trim();
        if(city.isEmpty()||country.isEmpty()) {
            Toast.makeText(getActivity(), EMPTY_FIELDS, Toast.LENGTH_SHORT).show();
        }
        else {
            location = new Location(street,city,country,state,streetNum);
        }
        return location;
    }

    private User getUser() {
        User user = new User();
        String fullName = etFullName.getText().toString().trim();
        if(fullName.isEmpty())
            Toast.makeText(getActivity(),EMPTY_NAME,Toast.LENGTH_SHORT).show();
        else {
            user.setmFullName(fullName);
        }
        return user;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OfflineAccountCreatedListener)
        {
            this.mOfflineAccountCreatedListener = (OfflineAccountCreatedListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.mOfflineAccountCreatedListener=null;
    }

    public interface OfflineAccountCreatedListener{
        void onOfflineAccountCreated(User user, Location location);
    }
}
