package hr.ferit.kstefancic.pollenalert;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Kristijan on 31.5.2017..
 */

public class SignUpFragment3 extends Fragment {

    private Button btnBack, btnFinish;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.sign_up_fragment3,null);
        setUI(layout);
        return layout;
    }

    private void setUI(View layout) {
        this.btnBack = (Button) layout.findViewById(R.id.signUpFr3_btnBack);
        this.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.activityFirst_fl, new SignUpFragment2());
                fragmentTransaction.commit();
            }
        });
        this.btnFinish= (Button) layout.findViewById(R.id.signUpFr3_btnFinish);
        this.btnFinish.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(),"Successfully signed up",Toast.LENGTH_SHORT).show();
            }
        });



    }
}
