package hr.ferit.kstefancic.pollenalert;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static hr.ferit.kstefancic.pollenalert.LogInFragment.USER;

/**
 * Created by Kristijan on 31.5.2017..
 */

public class SignUpFragment3 extends Fragment {

    private Button btnBack, btnFinish;
    private FinishListener mFinishListener;

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
                ArrayList<Pollen> pollens = new ArrayList<Pollen>();
                mFinishListener.onFinish(pollens);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof FinishListener)
        {
            this.mFinishListener = (FinishListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.mFinishListener=null;
    }

    public interface FinishListener{
        void onFinish(ArrayList<Pollen> pollenList);
    }
}
