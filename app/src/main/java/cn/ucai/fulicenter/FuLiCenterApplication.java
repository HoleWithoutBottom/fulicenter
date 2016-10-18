package cn.ucai.fulicenter;

import android.app.Application;

/**
 * Created by Administrator on 2016/10/17.
 */
public class FuLiCenterApplication extends Application {
    public static FuLiCenterApplication application;
    private static FuLiCenterApplication instance;

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
}
