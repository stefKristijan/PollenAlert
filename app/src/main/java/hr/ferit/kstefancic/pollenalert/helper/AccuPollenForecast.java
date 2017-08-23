package hr.ferit.kstefancic.pollenalert.helper;

import android.widget.Toast;

/**
 * Created by Kristijan on 23.8.2017..
 */

public class AccuPollenForecast {
    private int [] values; //categoryValues;
    private String [] categories;
    private String mName;

    public AccuPollenForecast(){
        this.values=new int[5];
        this.categories=new String[5];
        //this.categoryValues = new int[4];
    }

    public void setValues(int[] values) {
        this.values = values;
    }

    public void setCategories(String[] categories) {
        this.categories = categories;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public void addValue(int value, int index){
        this.values[index]=value;
    }
   /* public void addCategoryValue(int value, int index){
        this.categoryValues[index]=value;
    }*/
    public void addCategory(String value, int index){
        this.categories[index]=value;
    }

    public int getValue(int index){
        return this.values[index];
    }

    public String getCategory (int index){
        return this.categories[index];
    }


    public int[] getValues() {
        return values;
    }

  /*  public int[] getCategoryValues() {
        return categoryValues;
    }*/

    public String[] getCategories() {
        return categories;
    }
}
