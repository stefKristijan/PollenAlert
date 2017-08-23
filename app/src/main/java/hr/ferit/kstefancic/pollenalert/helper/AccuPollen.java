package hr.ferit.kstefancic.pollenalert.helper;

import android.widget.Toast;

/**
 * Created by Kristijan on 23.8.2017..
 */

public class AccuPollen {
    private int GRASS = 0, MOLD = 1, WEED=2, TREE=3;
    private int [] values, categoryValues;
    private String [] categories;

    public AccuPollen(){
        this.values=new int[4];
        this.categories=new String[4];
        this.categoryValues = new int[4];
    }

    @Override
    public String toString() {
        return categories[GRASS]+" "+categories[MOLD]+" "+categories[WEED]+" "+categories[TREE]+" ";
    }

    public void addValue(int value, int index){
        this.values[index]=value;
    }
    public void addCategoryValue(int value, int index){
        this.categoryValues[index]=value;
    }
    public void addCategory(String value, int index){
        this.categories[index]=value;
    }

    public int getGRASS() {
        return GRASS;
    }

    public int getMOLD() {
        return MOLD;
    }

    public int getWEED() {
        return WEED;
    }

    public int getTREE() {
        return TREE;
    }

    public int[] getValues() {
        return values;
    }

    public int[] getCategoryValues() {
        return categoryValues;
    }

    public String[] getCategories() {
        return categories;
    }
}
