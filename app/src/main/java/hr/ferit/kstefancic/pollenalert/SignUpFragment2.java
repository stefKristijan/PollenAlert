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
import android.widget.ImageView;

/**
 * Created by Kristijan on 31.5.2017..
 */

public class SignUpFragment2 extends Fragment {

    private Button btnBack, btnNext, btnChoosePic, btnShowMap;
    private EditText etStreet, etNumber, etState, etCity, etCountry;
    private ImageView ivUserPic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.sign_up_fragment2,null);
        setUI(layout);
        return layout;
    }

    private void setUI(View layout) {
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
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.activityFirst_fl, new SignUpFragment3());
                fragmentTransaction.commit();
            }
        });



    }



}
