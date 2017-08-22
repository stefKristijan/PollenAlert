package hr.ferit.kstefancic.pollenalert.helper;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import hr.ferit.kstefancic.pollenalert.Pollen;
import hr.ferit.kstefancic.pollenalert.R;

/**
 * Created by Kristijan on 21.8.2017..
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    public static final String TREES = "Trees";
    public static final String GRASS = "Grasses";
    public static final String WEEDS = "Weeds";
    private Context mContext;
    private List<String> mHeader;
    private HashMap<String, List<String>> mChildren;
    private ArrayList<Pollen> mPollenList;

    public ExpandableListAdapter(Context mContext, ArrayList<Pollen> mPollenList) {
        this.mPollenList = mPollenList;
        this.mContext = mContext;
        this.mHeader = new ArrayList<>();
        this.mChildren = new HashMap<>();
        List<String> trees = new ArrayList<>();
        List<String> grass = new ArrayList<>();
        List<String> weeds = new ArrayList<>();
        HashSet<String> hashSet = new HashSet<>();
        for(int i=0;i<mPollenList.size();i++){
            if(mPollenList.get(i).getCategory().equals("tree")){
                this.mHeader.add(TREES);
                trees.add(mPollenList.get(i).getName());
            }
            else  if(mPollenList.get(i).getCategory().equals("grass")){
                this.mHeader.add(GRASS);
                grass.add(mPollenList.get(i).getName());
            }
            else if(mPollenList.get(i).getCategory().equals("weed")){
                this.mHeader.add(WEEDS);
                weeds.add(mPollenList.get(i).getName());
            }
        }
        hashSet.addAll(this.mHeader);
        this.mHeader.clear();
        this.mHeader.addAll(hashSet);
        this.mChildren.put(mHeader.get(0),trees);
        this.mChildren.put(mHeader.get(1),grass);
        this.mChildren.put(mHeader.get(2),weeds);
    }

    @Override
    public int getGroupCount() {
        return this.mHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.mChildren.get(this.mHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.mHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.mChildren.get(this.mHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if(convertView==null){
            LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.exp_header_item,null);
        }
        TextView tvHeader = (TextView) convertView.findViewById(R.id.tvElvHeaderItem);
        tvHeader.setText((String) getGroup(groupPosition));
        return convertView;
    }

    public void setPollen(int index, boolean checked){
        this.mPollenList.get(index).setChecked(checked);
        notifyDataSetChanged();
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater= (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.exp_list_view_item,null);
        }

        TextView tvChildItem = (TextView) convertView.findViewById(R.id.tvElvChildItem);
        tvChildItem.setText(getChild(groupPosition,childPosition).toString());
        Pollen pollen = null;
        if(groupPosition==0){
            pollen = mPollenList.get(childPosition);
        }
        else if(groupPosition==1){
            pollen = mPollenList.get(childPosition+getChildrenCount(0));
        }
        else pollen = mPollenList.get(childPosition+getChildrenCount(0)+getChildrenCount(1));

        if(!pollen.isChecked()){
            convertView.setBackgroundColor(Color.WHITE);
            tvChildItem.setTextColor(Color.GRAY);
        }
        else{
            convertView.setBackgroundColor(Color.parseColor("#0F83AA"));
            tvChildItem.setTextColor(Color.WHITE);
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
