package hr.ferit.kstefancic.pollenalert;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class FirstActivity extends AppCompatActivity {

    private static final String OPTION_FRAGMENT = "option_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        setUpFragment();

    }

    private void setUpFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.activityFirst_fl, new OptionFragment(), this.OPTION_FRAGMENT);
        fragmentTransaction.commit();
    }
}
