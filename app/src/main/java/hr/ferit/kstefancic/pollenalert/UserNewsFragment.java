package hr.ferit.kstefancic.pollenalert;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

/**
 * Created by Kristijan on 22.8.2017..
 */

public class UserNewsFragment extends Fragment {

    private static final String USER_KEY = "user";
    private ImageView ivAvatar;
    private TextView tvUser, tvLocation;
    private User mUser;

    public static UserNewsFragment newInstance(User user){
        UserNewsFragment fragment = new UserNewsFragment();
        Bundle bundle = new Bundle();

        bundle.putSerializable(USER_KEY,user);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mUser = (User) getArguments().getSerializable(USER_KEY);
        View layout = inflater.inflate(R.layout.fragment_user_news, container, false);
        setUpUI(layout);
        return layout;
    }

    private void setUpUI(View layout) {
        Log.d("NEWInstance",mUser.getmAvatarPath());
        this.ivAvatar = (ImageView) layout.findViewById(R.id.frNewsivAvatar);
        Picasso.with(getActivity())
                .load(mUser.getmAvatarPath())
                .into(this.ivAvatar);
        this.tvUser = (TextView) layout.findViewById(R.id.frNewstvUser);
        this.tvUser.setText(mUser.getmUsername());

    }

}
