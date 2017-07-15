package hr.ferit.kstefancic.pollenalert;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Kristijan on 31.5.2017..
 */

public class CreateAccFragment extends Fragment {

    Button btnCreate;
    TextView tvLogin, tvRegister;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.create_acc_fragment,null);
        setUI(layout);
        return layout;
    }

    private void setUI(View layout) {
        this.btnCreate = (Button) layout.findViewById(R.id.createAccFr_btnCreate);
        this.btnCreate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //opening second activity
                Intent mainActIntent = new Intent(getActivity(),MainActivity.class);
                startActivity(mainActIntent);
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
}
