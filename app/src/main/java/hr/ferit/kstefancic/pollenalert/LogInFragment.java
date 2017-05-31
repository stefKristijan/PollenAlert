package hr.ferit.kstefancic.pollenalert;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Kristijan on 31.5.2017..
 */

public class LogInFragment extends Fragment {

    private Button btnLogIn, btnSignUp, btnCreateAcc;
    private EditText etUsername, etPassword;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.log_in_fragment,null);
        setUI(layout);
        return layout;
    }

    private void setUI(View layout) {
        this.btnCreateAcc = (Button) layout.findViewById(R.id.logInFr_btnCreateAcc);
        this.btnCreateAcc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.activityFirst_fl, new CreateAccFragment());
                fragmentTransaction.commit();
            }
        });
        this.btnLogIn = (Button) layout.findViewById(R.id.logInFr_btnLogIn);
        this.btnLogIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });
        this.btnSignUp = (Button) layout.findViewById(R.id.logInFr_btnSignUp);
        this.btnSignUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.activityFirst_fl, new SignUpFragment1());
                fragmentTransaction.commit();
            }
        });

        this.etPassword = (EditText) layout.findViewById(R.id.logInFr_etPassword);
        this.etUsername = (EditText) layout.findViewById(R.id.logInFr_etUsername);


    }
}
