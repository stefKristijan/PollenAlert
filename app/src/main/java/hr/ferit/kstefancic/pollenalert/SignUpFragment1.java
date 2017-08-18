package hr.ferit.kstefancic.pollenalert;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.text.LoginFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import static hr.ferit.kstefancic.pollenalert.LogInFragment.USER;

/**
 * Created by Kristijan on 31.5.2017..
 */

public class SignUpFragment1 extends Fragment {

    private static final String EMPTY_FIELDS = "Please fill all required fields to continue!";
    private static final String PASSWORD_DISMATCH = "Passwords don't match!";
    private Button btnNext;
    private EditText etUsername, etPassword, etConfirmPassword, etEmail;
    private TextView tvLogIn, tvCreateAcc;
    private UserCreatedListener mUserCreatedListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.sign_up_fragment1,null);
        setUI(layout);
        return layout;
    }

    private void setUI(View layout) {
        this.etConfirmPassword = (EditText) layout.findViewById(R.id.signUpFr1_etConfirmPassword);
        this.etPassword = (EditText) layout.findViewById(R.id.signUpFr1_etPassword);
        this.etEmail = (EditText) layout.findViewById(R.id.signUpFr1_etEmail);
        this.etUsername = (EditText) layout.findViewById(R.id.signUpFr1_etUsername);
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
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String confirmPass = etConfirmPassword.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                User user=null;
                if(checkFields(username,password,confirmPass,email)){
                    user= new User(username,email);
                    user.setmPassword(password);
                    mUserCreatedListener.onUserCreated(user);
                }

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof UserCreatedListener)
        {
            this.mUserCreatedListener = (UserCreatedListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.mUserCreatedListener=null;
    }

    private boolean checkFields(String username, String password, String confirmPass, String email) {
        if(username.isEmpty()||password.isEmpty()||confirmPass.isEmpty()||email.isEmpty()){
            Toast.makeText(getActivity(),EMPTY_FIELDS,Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(!password.equals(confirmPass)){
            Toast.makeText(getActivity(),PASSWORD_DISMATCH,Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public interface UserCreatedListener{
        void onUserCreated(User user);
    }

}
