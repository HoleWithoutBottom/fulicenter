package cn.ucai.fulicenter;

import android.app.Application;

import java.util.ArrayList;

import cn.ucai.fulicenter.bean.GoodsDetailBean;

/**
 * Created by Administrator on 2016/10/17.
 */
public class FuLiCenterApplication extends Application {
    public static FuLiCenterApplication application;
    private static FuLiCenterApplication instance;
    public static ArrayList<GoodsDetailBean> detailBeenList;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        application = this;
        detailBeenList = new ArrayList<>();
    }

    public static FuLiCenterApplication getInstance() {
        if (instance == null) {
            instance = new FuLiCenterApplication();
        }
        return instance;
    }
}
