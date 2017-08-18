package hr.ferit.kstefancic.pollenalert;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kristijan on 31.5.2017..
 */

public class LogInFragment extends Fragment {

    private static final String EMPTY_FIELDS = "Please enter username and password to log in!";
    private static final String URL_LOGIN = "http://pollenalert.000webhostapp.com/login.php";
    private static final String LOGIN_SUCCESS = "You were successfully logged in!";
    public static final String USER = "user";
    private Button btnLogIn;
    TextView tvRegister, tvCreateAcc;
    private EditText etUsername, etPassword;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.log_in_fragment,null);
        setUI(layout);
        return layout;
    }

    private void setUI(View layout) {
        this.etUsername = (EditText) layout.findViewById(R.id.logInFr_etUsername);
        this.etPassword = (EditText) layout.findViewById(R.id.logInFr_etPassword);
        this.tvCreateAcc = (TextView) layout.findViewById(R.id.logInFr_tvCreateAcc);
        this.tvCreateAcc.setOnClickListener(new View.OnClickListener() {

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
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                if(username.isEmpty()||password.isEmpty()){
                    Toast.makeText(getActivity().getApplicationContext(),EMPTY_FIELDS,Toast.LENGTH_SHORT).show();
                }
                else {
                    new Login(getActivity().getApplicationContext()).execute(username,password);
                }

            }
        });
        this.tvRegister = (TextView) layout.findViewById(R.id.logInFr_tvRegister);
        this.tvRegister.setOnClickListener(new View.OnClickListener() {

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

    class Login extends AsyncTask<String,Void,User> {

        Context mContext;
        ProgressDialog progressDialog;
        User mUser;

        public Login(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setTitle("Logging in");
            progressDialog.setMessage("Please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected User doInBackground(String... params) {
            String tag_str_req = "req_login";
            final String username = params[0];
            final String password = params[1];

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("RESPONSE",response.toString());
                    mUser = parseJSON(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("RESPONSE","Login error: "+ error.getMessage());
                    Toast.makeText(mContext,error.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String,String>();
                    params.put("username", username);
                    params.put("password",password);

                    return params;
                }
            };

            AppController.getInstance().addToRequestQueue(stringRequest,tag_str_req);
            return mUser;
        }

        @Override
        protected void onPostExecute(User user) {
            progressDialog.dismiss();
            if(mUser!=null){
                Toast.makeText(mContext,LOGIN_SUCCESS,Toast.LENGTH_SHORT).show();
                Intent mainIntent = new Intent(mContext,MainActivity.class);
                mainIntent.putExtra(USER, mUser);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }
        }

        private User parseJSON(String response) {
            try {
                JSONObject jObj = new JSONObject(response);
                boolean error = jObj.getBoolean("error");

                if(!error){
                    User user = new User(jObj.getString("username"),jObj.getString("unique_id"),jObj.getString("full_name"),jObj.getString("email"),jObj.getInt("id"));
                    return user;
                }
                else{
                    String errorMsg = jObj.getString("error_msg");
                    Toast.makeText(mContext,errorMsg,Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

    }
    
}
