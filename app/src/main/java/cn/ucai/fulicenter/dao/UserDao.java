package cn.ucai.fulicenter.dao;

import android.content.Context;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.bean.UserAvatar;

/**
 * Created by Administrator on 2016/10/24.
 */
public class UserDao {
    public static String USER_TABLE_NAME = "t_superwechat_user";
    public static String USER_COLUMN_NAME = "m_user_name";
    public static String USER_COLUMN_NICK = "m_user_nick";
    public static String USER_COLUMN_AVATAR_ID = "m_user_avatar_id";
    public static String USER_COLUMN_AVATAR_PATH = "m_user_avatar_path";
    public static String USER_COLUMN_AVATAR_SUFFIX = "m_user_avatar_suffix";
    public static String USER_COLUMN_AVATAR_TYPE = "m_user_avatar_type";
    public static String USER_COLUMN_AVATAR_LASTUPDATE_TIME = "m_user_avatar_lastupdate_time";

    public UserDao(Context context) {
        DBManager.getInstance().onInit(context);
    }


    public boolean saveUser(UserAvatar userAvatar) {
        return DBManager.getInstance().saveUserAvatar(userAvatar);
    }

    public UserAvatar getUser(String userName) {
        return DBManager.getInstance().getUserAvatar(userName);
    }

    public void closeDB() {
        DBManager.getInstance().closeDB();
    }

}
