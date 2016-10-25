package cn.ucai.fulicenter.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import cn.ucai.fulicenter.bean.UserAvatar;

/**
 * Created by Administrator on 2016/10/24.
 */
public class DBManager {
    private static DBOpenHelper dbHelper;
    private static DBManager dbManager = new DBManager();

    public static synchronized DBManager getInstance() {
        return dbManager;
    }

    public void onInit(Context context) {
        dbHelper = DBOpenHelper.getInstance(context);
    }

    public synchronized UserAvatar getUserAvatar(String userName) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "select * from " + UserDao.USER_TABLE_NAME + " where "
                + UserDao.USER_COLUMN_NAME + " =?";
        UserAvatar user = null;
        Cursor cursor = db.rawQuery(sql, new String[]{userName});
        if (cursor.moveToNext()) {
            user = new UserAvatar();
            user.setMuserName(userName);
            user.setMavatarId(cursor.getInt(cursor.getColumnIndex(UserDao.USER_COLUMN_AVATAR_ID)));
            user.setMuserNick(cursor.getString(cursor.getColumnIndex(UserDao.USER_COLUMN_NICK)));
            user.setMavatarType(cursor.getInt(cursor.getColumnIndex(UserDao.USER_COLUMN_AVATAR_TYPE)));
            user.setMavatarPath(cursor.getString(cursor.getColumnIndex(UserDao.USER_COLUMN_AVATAR_PATH)));
            user.setMavatarSuffix(cursor.getString(cursor.getColumnIndex(UserDao.USER_COLUMN_AVATAR_SUFFIX)));
            user.setMavatarLastUpdateTime(cursor.getString(cursor.getColumnIndex(UserDao.USER_COLUMN_AVATAR_LASTUPDATE_TIME)));
            return user;
        }
        return null;
    }

    public synchronized boolean saveUserAvatar(UserAvatar userAvatar) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserDao.USER_COLUMN_NAME, userAvatar.getMuserName());
        values.put(UserDao.USER_COLUMN_NICK, userAvatar.getMuserNick());
        values.put(UserDao.USER_COLUMN_AVATAR_ID, userAvatar.getMavatarId());
        values.put(UserDao.USER_COLUMN_AVATAR_TYPE, userAvatar.getMavatarType());
        values.put(UserDao.USER_COLUMN_AVATAR_PATH, userAvatar.getMavatarPath());
        values.put(UserDao.USER_COLUMN_AVATAR_SUFFIX, userAvatar.getMavatarSuffix());
        values.put(UserDao.USER_COLUMN_AVATAR_LASTUPDATE_TIME, userAvatar.getMavatarLastUpdateTime());
        if (db.isOpen()) {
            return db.replace(UserDao.USER_TABLE_NAME, null, values) != -1;
        }
        return false;
    }

    public synchronized boolean updateUserAvatar(UserAvatar userAvatar) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserDao.USER_COLUMN_NAME, userAvatar.getMuserName());
        values.put(UserDao.USER_COLUMN_NICK, userAvatar.getMuserNick());
        values.put(UserDao.USER_COLUMN_AVATAR_ID, userAvatar.getMavatarId());
        values.put(UserDao.USER_COLUMN_AVATAR_TYPE, userAvatar.getMavatarType());
        values.put(UserDao.USER_COLUMN_AVATAR_PATH, userAvatar.getMavatarPath());
        values.put(UserDao.USER_COLUMN_AVATAR_SUFFIX, userAvatar.getMavatarSuffix());
        values.put(UserDao.USER_COLUMN_AVATAR_LASTUPDATE_TIME, userAvatar.getMavatarLastUpdateTime());
        if (db.isOpen()) {
            return db.update(UserDao.USER_TABLE_NAME, values, UserDao.USER_COLUMN_NAME + "=?", new String[]{userAvatar.getMuserName()}) != -1;
        }
        return false;
    }

    public synchronized void closeDB() {
        if (dbHelper != null) {
            dbHelper.closeDB();
        }
    }
}
