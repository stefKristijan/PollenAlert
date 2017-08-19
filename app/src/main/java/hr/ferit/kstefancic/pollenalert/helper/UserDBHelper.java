package hr.ferit.kstefancic.pollenalert.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import hr.ferit.kstefancic.pollenalert.User;

/**
 * Created by Kristijan on 19.8.2017..
 */

public class UserDBHelper extends SQLiteOpenHelper {

    private static final String CREATE_TABLE_USER = "CREATE TABLE " + Schema.TABLE_USER + " (" + Schema.USER_ID+" INTEGER," +
            Schema.USER_UNIQID+" VARCHAR(23),"+Schema.USERNAME+" VARCHAR(50),"+Schema.EMAIL+" VARCHAR(150),"+Schema.FULL_NAME+" VARCHAR(100));";
    private static final String DROP_TABLE_USER = "DROP TABLE IF EXISTS"+Schema.TABLE_USER;
    private static final String SELECT_USER = "SELECT * FROM " + Schema.TABLE_USER;
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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_USER);
        this.onCreate(db);
    }

    public void insertUser(User user){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Schema.USER_ID,user.getId());
        contentValues.put(Schema.USER_UNIQID,user.getmUniqueId());
        contentValues.put(Schema.USERNAME,user.getmUsername());
        contentValues.put(Schema.EMAIL,user.getmEmail());
        contentValues.put(Schema.FULL_NAME,user.getmFullName());
        SQLiteDatabase wdb = this.getWritableDatabase();
        wdb.insert(Schema.TABLE_USER,Schema.USER_ID,contentValues);
        wdb.close();
    }

    public User getUser(){
        User user = null;
        SQLiteDatabase wdb = this.getWritableDatabase();
        Cursor userCursor = wdb.rawQuery(SELECT_USER,null);
        if(userCursor.moveToFirst()){
            int id = userCursor.getInt(0);
            String uniqId = userCursor.getString(1);
            String username = userCursor.getString(2);
            String email = userCursor.getString(3);
            String full_name = userCursor.getString(4);
            user = new User(username,email);
            user.setId(id);
            user.setmUniqueId(uniqId);
            user.setmFullName(full_name);
        }
        userCursor.close();
        wdb.close();
        return user;
    }

    public void deleteUser(){
        SQLiteDatabase wdb = this.getWritableDatabase();
        wdb.delete(Schema.TABLE_USER,null,null);
        wdb.close();
    }

    public static class Schema{
        private static final int SCHEMA_VERSION = 1;
        private static final String DATABASE_NAME = "user_data.db";
        //user table
        static final String TABLE_USER = "user";
        static final String USER_ID = "id";
        static final String USER_UNIQID = "unique_id";
        static final String USERNAME = "username";
        static final String EMAIL = "email";
        static final String FULL_NAME = "full_name";
    }
}
