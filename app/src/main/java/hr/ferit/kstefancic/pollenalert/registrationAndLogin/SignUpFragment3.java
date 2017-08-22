package hr.ferit.kstefancic.pollenalert.registrationAndLogin;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import hr.ferit.kstefancic.pollenalert.AppController;
import hr.ferit.kstefancic.pollenalert.Pollen;
import hr.ferit.kstefancic.pollenalert.R;
import hr.ferit.kstefancic.pollenalert.helper.ExpandableListAdapter;

/**
 * Created by Kristijan on 31.5.2017..
 */

public class SignUpFragment3 extends Fragment {

    private static final String URL_POLLEN_DATA ="http://pollenalert.000webhostapp.com/get_pollen_data.php" ;
    private Button btnBack, btnFinish;
    private FinishListener mFinishListener;
    private ExpandableListView mExpListView;
    private ArrayList<Pollen> mPollenList;
    private ArrayList<Pollen> mUserPollen;
    private ExpandableListAdapter mListAdapter;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.sign_up_fragment3,null);
        setUI(layout);
        return layout;
    }



    private void setUI(View layout) {
        this.progressDialog = new ProgressDialog(getActivity());
        this.mUserPollen = new ArrayList<>();
        this.mExpListView = (ExpandableListView) layout.findViewById(R.id.signUpFr3_elvPlants);
        this.mExpListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Pollen pollen = null;
                int index;
                if (groupPosition == 0) {
                    index = childPosition;
                    pollen = mPollenList.get(index);
                } else if (groupPosition == 1) {
                    index = childPosition + mListAdapter.getChildrenCount(0);
                    pollen = mPollenList.get(index);
                } else {
                    index = childPosition + mListAdapter.getChildrenCount(0) + mListAdapter.getChildrenCount(1);
                    pollen = mPollenList.get(index);
                }

                if (pollen.isChecked()) {
                    for (int i = 0; i < mUserPollen.size(); i++) {
                        if (mUserPollen.get(i).getName().equals(pollen.getName())) {
                            mUserPollen.remove(i);
                        }
                        mPollenList.get(index).setChecked(false);
                        mListAdapter.setPollen(index, false);
                    }
                } else {
                    mUserPollen.add(pollen);
                    mPollenList.get(index).setChecked(true);
                    mListAdapter.setPollen(index, true);
                }
                return false;
            }
        });
        this.mPollenList=new ArrayList<>();
        setExpListView();
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
                mFinishListener.onFinish(mUserPollen);
            }
        });
    }

    private void setExpListView() {
        showDialog();
        String tag_str_req = "req_pollen_data";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_POLLEN_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("RESPONSE",response.toString());
                parseJSON(response);
                mListAdapter = new ExpandableListAdapter(getActivity(),mPollenList);
                mExpListView.setAdapter(mListAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("RESPONSE","Data error: "+ error.getMessage());
            }
        });

        AppController.getInstance().addToRequestQueue(stringRequest,tag_str_req);

    }

    private void parseJSON(String response) {
        JSONObject jObj = null;
        JSONArray jArray = null;
        try {
            jObj = new JSONObject(response);
            boolean error = jObj.getBoolean("error");

            if(!error){
                hideDialog();
                jArray = jObj.getJSONArray("pollen_data");
                for(int i=0;i<jArray.length();i++){
                    JSONObject jo = jArray.getJSONObject(i);
                    Pollen pollen = new Pollen(jo.getInt("id"),jo.getString("name"),jo.getString("category"));
                    mPollenList.add(pollen);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showDialog(){
        progressDialog.setTitle("Fetching pollen data");
        progressDialog.setMessage("Please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    private void hideDialog(){
        progressDialog.dismiss();
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
