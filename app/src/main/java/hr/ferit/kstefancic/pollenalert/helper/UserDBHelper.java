package hr.ferit.kstefancic.pollenalert.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import hr.ferit.kstefancic.pollenalert.Location;
import hr.ferit.kstefancic.pollenalert.Pollen;
import hr.ferit.kstefancic.pollenalert.User;

/**
 * Created by Kristijan on 19.8.2017..
 */

public class UserDBHelper extends SQLiteOpenHelper {

    private static final String CREATE_TABLE_USER = "CREATE TABLE IF NOT EXISTS " + Schema.TABLE_USER + " (" + Schema.USER_ID+" INTEGER," +
            Schema.USER_UNIQID+" VARCHAR(23),"+Schema.USERNAME+" VARCHAR(50),"+Schema.EMAIL+" VARCHAR(150),"+Schema.FULL_NAME+" VARCHAR(100),"+Schema.AVATAR_PATH+" VARCHAR(150))";

    private static final String CREATE_TABLE_LOCATION =  "CREATE TABLE IF NOT EXISTS " + Schema.TABLE_LOCATION + " (" + Schema.LOCATION_ID+" INTEGER," +
            Schema.STREET+" VARCHAR(100),"+Schema.STREET_NUM+" VARCHAR(10),"+Schema.CITY+" VARCHAR(50),"+Schema.STATE+" VARCHAR(50),"+Schema.COUNTRY+" VARCHAR(50),"+Schema.LOCATION_KEY+" VARCHAR(10));";
    private static final String CREATE_TABLE_ALLERGIES =  "CREATE TABLE IF NOT EXISTS " + Schema.TABLE_ALLERGIES+ " (" + Schema.POLLEN_ID+" INTEGER," +
            Schema.POLLEN_NAME+" VARCHAR(50),"+Schema.POLLEN_CATEGORY+" VARCHAR(20));";
    private static final String SELECT_POLLEN = "SELECT * FROM " + Schema.TABLE_ALLERGIES;
    private static final String SELECT_LOCATION = "SELECT * FROM " + Schema.TABLE_LOCATION;
    private static final String SELECT_USER = "SELECT * FROM " + Schema.TABLE_USER;
    private static final String DROP_TABLE_USER = "DROP TABLE IF EXISTS "+Schema.TABLE_USER;
    private static final String DROP_TABLE_LOCATION = "DROP TABLE IF EXISTS "+Schema.TABLE_LOCATION;
    private static final String DROP_TABLE_ALLERGIES = "DROP TABLE IF EXISTS "+Schema.TABLE_ALLERGIES;

    private static UserDBHelper mUserDBHelper = null;

    public UserDBHelper(Context context) {
        super(context.getApplicationContext(),Schema.DATABASE_NAME,null,Schema.SCHEMA_VERSION);
    }

    public static synchronized UserDBHelper getInstance (Context context){
        if(mUserDBHelper==null){
            mUserDBHelper=new UserDBHelper(context);
        }
        return mUserDBHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_LOCATION);
        db.execSQL(CREATE_TABLE_ALLERGIES);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_USER);
        db.execSQL(DROP_TABLE_LOCATION);
        db.execSQL(DROP_TABLE_ALLERGIES);
        this.onCreate(db);
    }

    public void insertUser(User user){
        Log.d("SQLITE",user.getmAvatarPath());
        ContentValues contentValues = new ContentValues();
        contentValues.put(Schema.USER_ID,user.getId());
        contentValues.put(Schema.USER_UNIQID,user.getmUniqueId());
        contentValues.put(Schema.USERNAME,user.getmUsername());
        contentValues.put(Schema.EMAIL,user.getmEmail());
        contentValues.put(Schema.FULL_NAME,user.getmFullName());
        contentValues.put(Schema.AVATAR_PATH,user.getmAvatarPath());
        SQLiteDatabase wdb = this.getWritableDatabase();
        wdb.insert(Schema.TABLE_USER,null,contentValues);
        wdb.close();
    }

    public void insertLocation (Location location){
        SQLiteDatabase wdb = getWritableDatabase();
        wdb.execSQL("DELETE FROM "+Schema.TABLE_LOCATION);
        ContentValues contentValues = new ContentValues();
        contentValues.put(Schema.LOCATION_ID,location.getId());
        contentValues.put(Schema.STREET,location.getmStreet());
        contentValues.put(Schema.STREET_NUM,location.getmNumber());
        contentValues.put(Schema.CITY,location.getmCity());
        contentValues.put(Schema.STATE,location.getmState());
        contentValues.put(Schema.COUNTRY,location.getmCountry());
        contentValues.put(Schema.LOCATION_KEY, location.getmKey());
        wdb.insert(Schema.TABLE_LOCATION,null,contentValues);
        wdb.close();
    }

    public void insertAllergies (ArrayList<Pollen> pollen){
        ContentValues contentValues = new ContentValues();
        SQLiteDatabase wdb = this.getWritableDatabase();
        for(int i=0;i<pollen.size();i++) {
            contentValues.put(Schema.POLLEN_ID, pollen.get(i).getId());
            contentValues.put(Schema.POLLEN_NAME, pollen.get(i).getName());
            contentValues.put(Schema.POLLEN_CATEGORY, pollen.get(i).getCategory());
            wdb.insert(Schema.TABLE_ALLERGIES, null, contentValues);
        }
        wdb.close();
    }

    public ArrayList<Pollen> getAllergies(){
        ArrayList<Pollen> allergies = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor pollenCursor = db.rawQuery(SELECT_POLLEN,null);
        if(pollenCursor.moveToFirst()){
            do{
                int id=pollenCursor.getInt(0);
                String name = pollenCursor.getString(1);
                String category = pollenCursor.getString(2);
                allergies.add(new Pollen(id,name,category));
            }while(pollenCursor.moveToNext());
        }
        pollenCursor.close();
        db.close();
        return allergies;
    }

    public Location getLocation(){
        Location location = null;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor locCursor = db.rawQuery(SELECT_LOCATION,null);
        if(locCursor.moveToFirst()){
            String street = locCursor.getString(1);
            String streetNum = locCursor.getString(2);
            String city = locCursor.getString(3);
            String state = locCursor.getString(4);
            String country = locCursor.getString(5);
            String key = locCursor.getString(6);
            location=new Location(street,city,country,state,streetNum);
            location.setmKey(key);
        }
        locCursor.close();
        db.close();
        return location;
    }

    public User getUser(){
        User user = null;
        SQLiteDatabase wdb = this.getWritableDatabase();
        Cursor userCursor = wdb.rawQuery(SELECT_USER,null);
        Log.d("SQLITE","outside if");
        if(userCursor.moveToFirst()){
            Log.d("SQLITE","inside if");
            int id = userCursor.getInt(0);
            String uniqId = userCursor.getString(1);
            String username = userCursor.getString(2);
            String email = userCursor.getString(3);
            String full_name = userCursor.getString(4);
            String avatar_path=userCursor.getString(5);
            user = new User(username,email);
            user.setId(id);
            user.setmUniqueId(uniqId);
            user.setmFullName(full_name);
            user.setmAvatarPath(avatar_path);
        }
        userCursor.close();
        wdb.close();
        Log.d("SQLITE","after getUser");
        return user;
    }

    public void deleteLocation(){
        SQLiteDatabase wdb = this.getWritableDatabase();
        wdb.delete(Schema.TABLE_LOCATION,null,null);
        wdb.close();
    }

    public void deleteUser(){
        SQLiteDatabase wdb = this.getWritableDatabase();
        wdb.delete(Schema.TABLE_USER,null,null);
        wdb.close();
    }

    public void deleteAllergies(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Schema.TABLE_ALLERGIES,null,null);
        db.close();
    }

    public static class Schema{
        private static final int SCHEMA_VERSION = 12;
        private static final String DATABASE_NAME = "user_data.db";
        //user table
        static final String TABLE_USER = "user";
        static final String USER_ID = "id";
        static final String USER_UNIQID = "unique_id";
        static final String USERNAME = "username";
        static final String EMAIL = "email";
        static final String FULL_NAME = "full_name";
        static final String AVATAR_PATH = "avatar";
        //location table
        static final String TABLE_LOCATION = "location";
        static final String LOCATION_ID = "id";
        static final String STREET = "street";
        static final String STREET_NUM = "street_num";
        static final String CITY = "city";
        static final String STATE = "state";
        static final String COUNTRY = "country";
        static final String LOCATION_KEY = "key";
        //allergies table
        static final String TABLE_ALLERGIES="allergies";
        static final String POLLEN_ID="pollen_id";
        static final String POLLEN_NAME ="name";
        static final String POLLEN_CATEGORY="category";
    }
}
