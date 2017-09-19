package hr.ferit.kstefancic.pollenalert.helper;

import android.content.ContentValues;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import hr.ferit.kstefancic.pollenalert.R;
import hr.ferit.kstefancic.pollenalert.User;

/**
 * Created by Kristijan on 18.9.2017..
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder>{

    private ArrayList<News> mNews;
    private Context mContext;

    public NewsAdapter(ArrayList<News> mNews, Context mContext) {
        this.mNews = mNews;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View postView = inflater.inflate(R.layout.post_item,parent,false);
        ViewHolder postViewHolder = new ViewHolder(postView);
        return postViewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        News post = this.mNews.get(position);
        holder.tvUser.setText(post.getmUsername());
        Picasso.with(mContext)
                .load("http://pollenalert.000webhostapp.com/"+post.getmAvatarPath())
                .into(holder.ivAvatar);
        holder.tvFeeling.setText(post.getmFeeling());
        switch (post.getmFeeling()){
            case "Very good":
                holder.ivFeeling.setImageResource(R.mipmap.laugh_emoji);
                break;
            case "Good":
                holder.ivFeeling.setImageResource(R.mipmap.smiley_emoji);
                break;
            case "Neutral":
                holder.ivFeeling.setImageResource(R.mipmap.not_good_emoji);
                break;
            case "Bad":
                holder.ivFeeling.setImageResource(R.mipmap.sad_emoji);
                break;
            case "Very bad":
                holder.ivFeeling.setImageResource(R.mipmap.feeling_bad_emoji);
                break;
            case "Sick":
                holder.ivFeeling.setImageResource(R.mipmap.feeling_sick_emoji);
                break;
            case "Very sick":
                holder.ivFeeling.setImageResource(R.mipmap.very_sick_emoji);
                break;
            case "A cold":
                holder.ivFeeling.setImageResource(R.mipmap.sneezing_emoji);
                break;
        }
        holder.tvLocation.setText("in "+post.getmCity()+", "+post.getmState()+", "+post.getmCountry()+".");
        holder.tvSymptoms.setText("Symptoms:"+post.getmSymptoms());
    }

    @Override
    public int getItemCount() {
        return mNews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView ivAvatar, ivFeeling;
        TextView tvUser, tvFeeling, tvSymptoms, tvLocation;
        public ViewHolder(View itemView) {
            super(itemView);
            this.ivAvatar = (ImageView) itemView.findViewById(R.id.rvNewsIvAvatar);
            this.ivFeeling = (ImageView) itemView.findViewById(R.id.rvNewsIvFeeling);
            this.tvUser = (TextView) itemView.findViewById(R.id.rvNewsTvUser);
            this.tvLocation = (TextView) itemView.findViewById(R.id.rvNewsTvAtLocation);
            this.tvFeeling = (TextView) itemView.findViewById(R.id.rvNewsTvFeeling);
            this.tvSymptoms = (TextView) itemView.findViewById(R.id.rvNewsTvSymptoms);
        }
    }
}
