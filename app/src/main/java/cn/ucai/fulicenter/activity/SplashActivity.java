package cn.ucai.fulicenter.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cn.ucai.fulicenter.MainActivity;
import cn.ucai.fulicenter.R;
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
                long cost = System.currentTimeMillis() - time;
                if (SLEEPTIME - cost > 0) {
                    try {
                        Thread.sleep(SLEEPTIME - cost);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                MFGT.startActivity(SplashActivity.this,HomeActivity.class);
            }
        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
       MFGT.finish(SplashActivity.this);
    }
}