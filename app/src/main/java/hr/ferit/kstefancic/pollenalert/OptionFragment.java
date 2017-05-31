package hr.ferit.kstefancic.pollenalert;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by Kristijan on 31.5.2017..
 */

public class OptionFragment extends Fragment {

    //private static final String LOG_IN_FRAGMENT = "log_in_fragment";
    //private static final String SIGNUP_FRAGMENT = "signup_fragment";
    private Button btnLogIn, btnSignUp, btnCreateAcc;
    private ImageView ivSignUpInfo, ivCreateAccInfo;
    //public static final String CREATE_ACC_FRAGMENT = "create_acc_fragment" ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.option_fragment,null);
        setUI(layout);
        return layout;
    }

    private void setUI(View layout) {
        this.btnCreateAcc = (Button) layout.findViewById(R.id.optionFr_btnCreateAcc);
        this.btnCreateAcc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.activityFirst_fl, new CreateAccFragment());
                fragmentTransaction.commit();
            }
        });
        this.btnLogIn = (Button) layout.findViewById(R.id.optionFr_btnLogIn);
        this.btnLogIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.activityFirst_fl, new LogInFragment());
                fragmentTransaction.commit();
            }
        });
        this.btnSignUp = (Button) layout.findViewById(R.id.optionFr_btnSignUp);
        this.btnSignUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.activityFirst_fl, new SignUpFragment1());
                fragmentTransaction.commit();
            }
        });

        this.ivCreateAccInfo = (ImageView) layout.findViewById(R.id.optionFr_ivCreateAccInfo);
        this.ivSignUpInfo = (ImageView) layout.findViewById(R.id.optionFr_ivSignUpInfo);


    }
}
