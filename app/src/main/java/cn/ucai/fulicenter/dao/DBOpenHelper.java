package cn.ucai.fulicenter.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cn.ucai.fulicenter.I;

/**
 * Created by Administrator on 2016/10/24.
 */
public class DBOpenHelper extends SQLiteOpenHelper {
    private static int VERSION = 1;
    private static String DB_NAME = "FuLiCenter_db";
    private static DBOpenHelper instance;
    private static final String FULICENTER_USER_TABLE_CREATE="create table "+
            UserDao.USER_TABLE_NAME+" ("+
            UserDao.USER_COLUMN_NAME+" text primary key, "+
            UserDao.USER_COLUMN_NICK+" text, "+
            UserDao.USER_COLUMN_AVATAR_ID+" INTEGER, "+
            UserDao.USER_COLUMN_AVATAR_PATH+" text, "+
            UserDao.USER_COLUMN_AVATAR_TYPE+" INTEGER, "+
            UserDao.USER_COLUMN_AVATAR_SUFFIX+" text, "+
            UserDao.USER_COLUMN_AVATAR_LASTUPDATE_TIME+" text);";


    public DBOpenHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    public static DBOpenHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBOpenHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public String getDatabaseName() {
        return I.User.TABLE_NAME + "_demo.db";
    }



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(FULICENTER_USER_TABLE_CREATE );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void closeDB() {
        if (instance != null) {
            SQLiteDatabase db = instance.getWritableDatabase();
            db.close();
            instance = null;
        }
    }
}
