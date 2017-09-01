package hr.ferit.kstefancic.pollenalert.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import hr.ferit.kstefancic.pollenalert.Location;
import hr.ferit.kstefancic.pollenalert.User;

/**
 * Created by Kristijan on 19.8.2017..
 */

public class UserDBHelper extends SQLiteOpenHelper {

    private static final String CREATE_TABLE_USER = "CREATE TABLE " + Schema.TABLE_USER + " (" + Schema.USER_ID+" INTEGER," +
            Schema.USER_UNIQID+" VARCHAR(23),"+Schema.USERNAME+" VARCHAR(50),"+Schema.EMAIL+" VARCHAR(150),"+Schema.FULL_NAME+" VARCHAR(100),"+Schema.AVATAR_PATH+" VARCHAR(150))";
    private static final String DROP_TABLE_USER = "DROP TABLE IF EXISTS "+Schema.TABLE_USER;
    private static final String SELECT_USER = "SELECT * FROM " + Schema.TABLE_USER;
    private static final String CREATE_TABLE_LOCATION =  "CREATE TABLE " + Schema.TABLE_LOCATION + " (" + Schema.LOCATION_ID+" INTEGER," +
            Schema.STREET+" VARCHAR(100),"+Schema.STREET_NUM+" VARCHAR(10),"+Schema.CITY+" VARCHAR(50),"+Schema.STATE+" VARCHAR(50),"+Schema.COUNTRY+" VARCHAR(50));";
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

    public void dropAll(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(DROP_TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS "+Schema.TABLE_LOCATION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USER);
        //db.execSQL(CREATE_TABLE_LOCATION);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_USER);
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
        ContentValues contentValues = new ContentValues();
        contentValues.put(Schema.LOCATION_ID,location.getId());
        contentValues.put(Schema.STREET,location.getmStreet());
        contentValues.put(Schema.STREET_NUM,location.getmNumber());
        contentValues.put(Schema.CITY,location.getmCity());
        contentValues.put(Schema.STATE,location.getmState());
        contentValues.put(Schema.COUNTRY,location.getmCountry());
        SQLiteDatabase wdb = this.getWritableDatabase();
        wdb.insert(Schema.TABLE_LOCATION,Schema.LOCATION_ID,contentValues);
        wdb.close();
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

    public void deleteLocations(){
        SQLiteDatabase wdb = this.getWritableDatabase();
        wdb.delete(Schema.TABLE_LOCATION,null,null);
        wdb.close();
    }

    public void deleteUser(){
        SQLiteDatabase wdb = this.getWritableDatabase();
        wdb.delete(Schema.TABLE_USER,null,null);
        wdb.close();
    }

    public static class Schema{
        private static final int SCHEMA_VERSION = 5;
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
    }
}
