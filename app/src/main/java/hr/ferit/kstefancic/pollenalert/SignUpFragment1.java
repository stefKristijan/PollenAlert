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
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Kristijan on 31.5.2017..
 */

public class SignUpFragment1 extends Fragment {

    Button btnNext;
    EditText etUsername, etPassword, etConfirmPassword, etEmail;
    TextView tvLogIn, tvCreateAcc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.sign_up_fragment1,null);
        setUI(layout);
        return layout;
    }

    private void setUI(View layout) {
        this.tvCreateAcc = (TextView) layout.findViewById(R.id.signUpFr1_tvOfflineAccount);
        this.tvCreateAcc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.activityFirst_fl, new CreateAccFragment());
                fragmentTransaction.commit();
            }
        });
        this.tvLogIn = (TextView) layout.findViewById(R.id.signUpFr1_tvLogin);
        this.tvLogIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.activityFirst_fl, new LogInFragment());
                fragmentTransaction.commit();
            }
        });
        this.btnNext= (Button) layout.findViewById(R.id.signUpFr1_btnNext);
        this.btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.activityFirst_fl, new SignUpFragment2());
                fragmentTransaction.commit();
            }
        });

        this.etConfirmPassword = (EditText) layout.findViewById(R.id.signUpFr1_etConfirmPassword);
        this.etPassword = (EditText) layout.findViewById(R.id.signUpFr1_etPassword);
        this.etEmail = (EditText) layout.findViewById(R.id.signUpFr1_etEmail);
        this.etUsername = (EditText) layout.findViewById(R.id.signUpFr1_etUsername);

    }



}