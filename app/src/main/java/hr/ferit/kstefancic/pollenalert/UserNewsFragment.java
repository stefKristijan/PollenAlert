package hr.ferit.kstefancic.pollenalert;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import hr.ferit.kstefancic.pollenalert.helper.AccuPollenForecast;
import hr.ferit.kstefancic.pollenalert.helper.News;
import hr.ferit.kstefancic.pollenalert.helper.NewsAdapter;
import hr.ferit.kstefancic.pollenalert.helper.PollenCountAdapter;

/**
 * Created by Kristijan on 22.8.2017..
 */

public class UserNewsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String USER_KEY = "user";
    private ImageView ivAvatar;
    private TextView tvUser, tvLocation;
    private User mUser;
    private RecyclerView rvNews;
    private NewsAdapter mNewsAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.ItemDecoration mItemDecoration;
    private ArrayList<News> mNews;
    SwipeRefreshLayout swipeRecyclerView;
    private static final String URL_GET_NEWS = "http://pollenalert.000webhostapp.com/get_all_posts.php";

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
        this.mNews = new ArrayList<>();
        this.rvNews = (RecyclerView) layout.findViewById(R.id.frNewsrvNews);
        this.ivAvatar = (ImageView) layout.findViewById(R.id.frNewsivAvatar);
        Picasso.with(getActivity())
                .load(mUser.getmAvatarPath())
                .into(this.ivAvatar);
        this.tvUser = (TextView) layout.findViewById(R.id.frNewstvUser);
        this.tvUser.setText(mUser.getmUsername());
        this.tvLocation = (TextView) layout.findViewById(R.id.frNewstvLocation);
        this.tvLocation.setText(mUser.getmLocation().toString());
        this.swipeRecyclerView= (SwipeRefreshLayout) layout.findViewById(R.id.swipeRecyclerView);
        swipeRecyclerView.setOnRefreshListener(this);

        getNews();
    }

    private void getNews() {
        String tag_str_req = "news_req";
        JsonObjectRequest getRequest = new JsonObjectRequest
                (Request.Method.GET, URL_GET_NEWS, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mNews.clear();
                        parseJSONNews(response);
                        setRecyclerView();
                        if(swipeRecyclerView.isRefreshing()){
                            swipeRecyclerView.setRefreshing(false);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response",error.toString());
                    }
                });
        AppController.getInstance().addToRequestQueue(getRequest,tag_str_req);
    }

    private void setRecyclerView() {
        Context context = getActivity();
        this.rvNews.setAdapter(null);
        this.mNewsAdapter = new NewsAdapter(mNews, context);
        this.mLayoutManager = new LinearLayoutManager(context);
        this.mItemDecoration = new DividerItemDecoration(context,DividerItemDecoration.VERTICAL);
        this.rvNews.addItemDecoration(this.mItemDecoration);
        this.rvNews.setLayoutManager(mLayoutManager);
        this.rvNews.setAdapter(mNewsAdapter);
    }

    private void parseJSONNews(JSONObject jObj) {
        try {
            JSONArray jsonArray= jObj.getJSONArray("posts");
            for(int i=0;i<jsonArray.length();i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String date = jsonObject.getString("date");
                String feeling = jsonObject.getString("feeling");
                String symptoms = jsonObject.getString("symptoms");
                String city = jsonObject.getString("city");
                String state = jsonObject.getString("state");
                String country = jsonObject.getString("country");
                JSONObject userObj = jsonObject.getJSONObject("user");
                String username = userObj.getString("username");
                String avatar_path = userObj.getString("avatar_path");

                News post = new News(symptoms,feeling,date,city,state,country,username,avatar_path);
                mNews.add(post);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRefresh() {
        getNews();
    }
}
