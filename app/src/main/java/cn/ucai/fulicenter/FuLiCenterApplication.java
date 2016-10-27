package cn.ucai.fulicenter;

import android.app.Application;

import java.util.ArrayList;

import cn.ucai.fulicenter.bean.GoodsDetailBean;
import cn.ucai.fulicenter.bean.UserAvatar;

/**
 * Created by Administrator on 2016/10/17.
 */
public class FuLiCenterApplication extends Application {
    public static FuLiCenterApplication application;
    private static FuLiCenterApplication instance;
    public static UserAvatar userAvatar;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        application = this;
    }

    public static FuLiCenterApplication getInstance() {
        if (instance == null) {
            instance = new FuLiCenterApplication();
        }
        return instance;
    }

    public static void setUserAvatar(UserAvatar userAvatar) {
        FuLiCenterApplication.userAvatar = userAvatar;
    }
}
