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

/**
 * Created by Kristijan on 31.5.2017..
 */

public class CreateAccFragment extends Fragment {

    Button btnCreate, btnLogIn, btnSignUp;

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

        this.btnLogIn = (Button) layout.findViewById(R.id.createAccFr_btnLogIn);
        this.btnLogIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.activityFirst_fl, new LogInFragment());
                fragmentTransaction.commit();
            }
        });

        this.btnSignUp = (Button) layout.findViewById(R.id.createAccFr_btnSignUp);
        this.btnSignUp.setOnClickListener(new View.OnClickListener() {

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
