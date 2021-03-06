package cn.ucai.fulicenter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;

import cn.ucai.fulicenter.activity.HomeActivity;
import cn.ucai.fulicenter.activity.RegisterActivity;
import cn.ucai.fulicenter.bean.Result;
import cn.ucai.fulicenter.bean.UserAvatar;
import cn.ucai.fulicenter.dao.UserDao;
import cn.ucai.fulicenter.utils.CommonUtils;
import cn.ucai.fulicenter.utils.L;
import cn.ucai.fulicenter.utils.MD5;
import cn.ucai.fulicenter.utils.MFGT;
import cn.ucai.fulicenter.utils.OkHttpUtils;
import cn.ucai.fulicenter.utils.ResultUtils;

public class MainActivity extends AppCompatActivity {
    EditText mEtUserName, mEtPassword;
    Button btnLogin, btnGoToRegister;
    ImageView mIvBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        setListener();
    }

    private void initData() {
    }

    private void setListener() {
        //跳转到注册界面
        btnGoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                MFGT.startActivityForResult(MainActivity.this, intent, I.REQUEST_CODE_REGISTER);
            }
        });
        // 登录
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = mEtUserName.getText().toString().trim();
                String password = MD5.getMessageDigest(mEtPassword.getText().toString().trim());
                if (userName == null || userName.length() == 0) {
                    CommonUtils.showShortToast("请输入账号");
                    mEtUserName.requestFocus();
                    return;
                }
                if (password == null || password.length() == 0) {
                    CommonUtils.showShortToast("请输入密码");
                    mEtPassword.requestFocus();
                    return;
                }
                OkHttpUtils<String> utils = new OkHttpUtils<String>(MainActivity.this);
                utils.setRequestUrl(I.REQUEST_LOGIN)
                        .addParam(I.User.USER_NAME, userName)
                        .addParam(I.User.PASSWORD, password)
                        .targetClass(String.class)
                        .execute(new OkHttpUtils.OnCompleteListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                Result result = ResultUtils.getResultFromJson(s, UserAvatar.class);
                                if (result != null) {
                                    switch (result.getRetCode()) {
                                        case 0:
                                            CommonUtils.showShortToast("登录成功！");
                                            UserAvatar userAvatar = (UserAvatar) result.getRetData();
                                            UserDao dao = new UserDao(MainActivity.this);
                                            if (dao.saveUser(userAvatar)) {
                                                SharedPreferences sp = getSharedPreferences("UserInfo", MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sp.edit();
                                                editor.putString("userName", userAvatar.getMuserName());
                                                editor.commit();
                                                FuLiCenterApplication.setUserAvatar(userAvatar);
                                            }
                                            /*Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                            MFGT.startActivity(MainActivity.this, intent);*/
                                            MFGT.finish(MainActivity.this);
                                            break;
                                        default:
                                            CommonUtils.showShortToast("登录失败");
                                            break;
                                    }

                                }
                            }

                            @Override
                            public void onError(String error) {

                            }
                        });
            }
        });
        onBack();
    }

    private void onBack() {
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                MFGT.startActivity(MainActivity.this, intent);
            }
        });
    }

    private void initView() {
        mEtUserName = (EditText) findViewById(R.id.etUserName);
        mEtPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnGoToRegister = (Button) findViewById(R.id.btnGoToRegister);
        mIvBack = (ImageView) findViewById(R.id.iv_login_back);
    }

    // 接收注册界面发来的用户名
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == I.REQUEST_CODE_REGISTER && resultCode == RESULT_OK) {
            mEtUserName.setText(data.getStringExtra("userName"));
        }
    }

}
