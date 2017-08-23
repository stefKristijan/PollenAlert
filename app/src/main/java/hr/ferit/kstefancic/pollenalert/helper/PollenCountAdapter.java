package hr.ferit.kstefancic.pollenalert.helper;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import hr.ferit.kstefancic.pollenalert.LocationPollenFragment;
import hr.ferit.kstefancic.pollenalert.R;

/**
 * Created by Kristijan on 23.8.2017..
 */

public class PollenCountAdapter extends RecyclerView.Adapter<PollenCountAdapter.ViewHolder>{

    private static final String LOW = "Low";
    private static final String GOOD = "Good";
    private static final String MODERATE = "Moderate";
    private static final String HIGH = "High";
    private static final String HAZARDOUS = "Hazardous";
    private static final String UNHEALTHY = "Unhealthy";
    private ArrayList<AccuPollenForecast> mPollenData = new ArrayList<>();
    private Context mContext;

    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");

    public PollenCountAdapter(ArrayList<AccuPollenForecast> mPollenData, Context mContext) {
        this.mPollenData = mPollenData;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View pollenItemView = inflater.inflate(R.layout.pollen_item,parent,false);
        ViewHolder pollenDataViewHolder = new ViewHolder(pollenItemView);
        return pollenDataViewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AccuPollenForecast accuPollenForecast = this.mPollenData.get(position);
        setDates(holder);
        switch (position){
            case LocationPollenFragment.GRASS:
                holder.ivPollen.setImageResource(R.mipmap.grass);
                break;
            case LocationPollenFragment.MOLD:
                holder.ivPollen.setImageResource(R.mipmap.mold);
                break;
            case LocationPollenFragment.TREE:
                holder.ivPollen.setImageResource(R.mipmap.tree);
                break;
            case LocationPollenFragment.WEED:
                holder.ivPollen.setImageResource(R.mipmap.weed);
                break;
        }
        holder.tvPollen.setText(accuPollenForecast.getmName());
        setCategoryImageAndText(accuPollenForecast.getCategory(0),holder.ivValue1,holder.tvAmount1);
        setCategoryImageAndText(accuPollenForecast.getCategory(1),holder.ivValue2,holder.tvAmount2);
        setCategoryImageAndText(accuPollenForecast.getCategory(2),holder.ivValue3,holder.tvAmount3);
        setCategoryImageAndText(accuPollenForecast.getCategory(3),holder.ivValue4,holder.tvAmount4);
        setCategoryImageAndText(accuPollenForecast.getCategory(4),holder.ivValue5,holder.tvAmount5);
    }

    @TargetApi(Build.VERSION_CODES.M) //zbog getColor
    private void setCategoryImageAndText(String category, ImageView iv, TextView tv) {
        switch (category){
            case LOW:
                iv.setImageResource(R.mipmap.pollen_low);
                tv.setText(LOW.toUpperCase());
                tv.setTextColor(mContext.getColor(R.color.colorLowPollenText));
                break;
            case MODERATE:
                iv.setImageResource(R.mipmap.pollen_moderate);
                tv.setText(MODERATE.toUpperCase());
                tv.setTextColor(mContext.getColor(R.color.colorModeratePollenText));
                break;
            case HIGH:
                iv.setImageResource(R.mipmap.pollen_high);
                tv.setText(HIGH.toUpperCase());
                tv.setTextColor(mContext.getColor(R.color.colorHighPollenText));
                break;
            case GOOD:
                iv.setImageResource(R.mipmap.pollen_good);
                tv.setText(GOOD.toUpperCase());
                tv.setTextColor(mContext.getColor(R.color.colorGoodPollenText));
                break;
            case UNHEALTHY:
                iv.setImageResource(R.mipmap.unhealthy);
                tv.setText(UNHEALTHY.toUpperCase());
                tv.setTextColor(mContext.getColor(R.color.colorHighPollenText));
                break;
            case HAZARDOUS:
                iv.setImageResource(R.mipmap.hazardous);
                tv.setText(HAZARDOUS.toUpperCase());
                tv.setTextColor(mContext.getColor(R.color.colorHighPollenText));
                break;
        }
    }


    private void setDates(ViewHolder holder) {
        String [] dates = new String [5];
        for(int i=0; i<5; i++){
            Calendar calendar = new GregorianCalendar();
            calendar.add(Calendar.DATE,i);
            dates[i] = sdf.format(calendar.getTime());
        }
        holder.tvDate1.setText(dates[0]);
        holder.tvDate2.setText(dates[1]);
        holder.tvDate3.setText(dates[2]);
        holder.tvDate4.setText(dates[3]);
        holder.tvDate5.setText(dates[4]);
    }

    @Override
    public int getItemCount() {
        return mPollenData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPollen, tvDate1, tvDate2, tvDate3, tvDate4, tvDate5,
                        tvAmount1, tvAmount2, tvAmount3, tvAmount4, tvAmount5;
        private ImageView ivPollen, ivValue1,ivValue2,ivValue3,ivValue4,ivValue5;

        public ViewHolder(View itemView){
            super(itemView);
            this.tvPollen = (TextView) itemView.findViewById(R.id.tvTitle);
            this.tvAmount1 = (TextView) itemView.findViewById(R.id.frLPrvpitvAmount);
            this.tvAmount2 = (TextView) itemView.findViewById(R.id.frLPrvpitvAmount2);
            this.tvAmount3 = (TextView) itemView.findViewById(R.id.frLPrvpitvAmount3);
            this.tvAmount4 = (TextView) itemView.findViewById(R.id.frLPrvpitvAmount4);
            this.tvAmount5 = (TextView) itemView.findViewById(R.id.frLPrvpitvAmount5);
            this.tvDate1 = (TextView) itemView.findViewById(R.id.frLPrvpiFirstDate);
            this.tvDate2 = (TextView) itemView.findViewById(R.id.frLPrvpiSecondDate);
            this.tvDate3 = (TextView) itemView.findViewById(R.id.frLPrvpiThirdDate);
            this.tvDate4 = (TextView) itemView.findViewById(R.id.frLPrvpiFourthDate);
            this.tvDate5 = (TextView) itemView.findViewById(R.id.frLPrvpiFifthDate);
            this.ivPollen = (ImageView) itemView.findViewById(R.id.ivThumbnail);
            this.ivValue1 = (ImageView) itemView.findViewById(R.id.frLPrvpiivValue);
            this.ivValue2 = (ImageView) itemView.findViewById(R.id.frLPrvpiivValue2);
            this.ivValue3 = (ImageView) itemView.findViewById(R.id.frLPrvpiivValue3);
            this.ivValue4 = (ImageView) itemView.findViewById(R.id.frLPrvpiivValue4);
            this.ivValue5 = (ImageView) itemView.findViewById(R.id.frLPrvpiivValue5);
        }
    }
}
