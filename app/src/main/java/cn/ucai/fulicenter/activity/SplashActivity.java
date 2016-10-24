package cn.ucai.fulicenter.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.MainActivity;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.UserAvatar;
import cn.ucai.fulicenter.dao.UserDao;
import cn.ucai.fulicenter.utils.L;
import cn.ucai.fulicenter.utils.MFGT;

public class SplashActivity extends AppCompatActivity {
    static final long SLEEPTIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new Thread() {
            @Override
            public void run() {
                long time = System.currentTimeMillis();
                // 连接数据库
                UserDao dao = new UserDao(SplashActivity.this);
                if (FuLiCenterApplication.userAvatar == null) {
                    // 获取首选项保存的用户名和密码，比较，相同直接进入HomeActivity
                    SharedPreferences sp = getSharedPreferences("UserInfo", MODE_PRIVATE);
                    String userName = sp.getString("userName", "sb");
                    UserAvatar user = dao.getUser(userName);
                    if (user != null && !userName.equals("sb")) {
                        FuLiCenterApplication.setUserAvatar(user);
                        dao.closeDB();
                    }
                }
                long cost = System.currentTimeMillis() - time;
                if (SLEEPTIME - cost > 0) {
                    try {
                        Thread.sleep(SLEEPTIME - cost);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                MFGT.startActivity(SplashActivity.this, HomeActivity.class);
            }
        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MFGT.finish(SplashActivity.this);
    }
}
