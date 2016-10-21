package cn.ucai.fulicenter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.MainActivity;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.Result;
import cn.ucai.fulicenter.utils.CommonUtils;
import cn.ucai.fulicenter.utils.L;
import cn.ucai.fulicenter.utils.MFGT;
import cn.ucai.fulicenter.utils.OkHttpUtils;

public class RegisterActivity extends AppCompatActivity {

    @Bind(R.id.iv_register_back)
    ImageView ivRegisterBack;
    @Bind(R.id.relativeLogin)
    LinearLayout relativeLogin;
    @Bind(R.id.etUserName_register)
    EditText etUserNameRegister;
    @Bind(R.id.etNickName_register)
    EditText etNickNameRegister;
    @Bind(R.id.etPassword_register)
    EditText etPasswordRegister;
    @Bind(R.id.etConfirmPassword_register)
    EditText etConfirmPasswordRegister;
    @Bind(R.id.btn_register)
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
    }


    @OnClick({R.id.iv_register_back, R.id.btn_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_register_back:
                MFGT.finish(this);
                break;
            case R.id.btn_register:
                final String userName = etUserNameRegister.getText().toString().trim();
                String nickName = etNickNameRegister.getText().toString().trim();
                String password = etPasswordRegister.getText().toString().trim();
                String confirmPassword = etConfirmPasswordRegister.getText().toString().trim();
                if (userName == null || userName.length() == 0) {
                    CommonUtils.showShortToast("请输入账号");
                    etUserNameRegister.requestFocus();
                    return;
                }
                if (!userName.matches("[a-zA-Z][a-zA-Z0-9_]{5,16}")) {
                    CommonUtils.showShortToast("账号由字母和数字组成，数字不能开头，长度6-16");
                    etUserNameRegister.requestFocus();
                    return;
                }
                if (nickName == null || nickName.length() == 0) {
                    CommonUtils.showShortToast("请输入昵称");
                    etNickNameRegister.requestFocus();
                    return;
                }
                if (password == null || password.length() == 0) {
                    CommonUtils.showShortToast("请输入密码");
                    etPasswordRegister.requestFocus();
                    return;
                }
                if (confirmPassword == null || confirmPassword.length() == 0) {
                    CommonUtils.showShortToast("请确认密码");
                    etConfirmPasswordRegister.requestFocus();
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    CommonUtils.showShortToast("密码不匹配");
                    etConfirmPasswordRegister.requestFocus();
                    return;
                }
                OkHttpUtils<Result> utils = new OkHttpUtils<>(this);
                utils.setRequestUrl(I.REQUEST_REGISTER)
                        .post()
                        .addParam(I.User.USER_NAME, userName)
                        .addParam(I.User.NICK, nickName)
                        .addParam(I.User.PASSWORD, password)
                        .targetClass(Result.class)
                        .execute(new OkHttpUtils.OnCompleteListener<Result>() {
                            @Override
                            public void onSuccess(Result result) {
                                if (result != null) {
                                    if (result.getRetCode() == 0) {
                                        CommonUtils.showShortToast("注册成功!");
                                        setResult(RESULT_OK,new Intent().putExtra("userName",userName));
                                        MFGT.finish(RegisterActivity.this);
                                    }else if (result.getRetCode()==102){
                                        CommonUtils.showShortToast("该账号已存在！");
                                    }else {
                                        CommonUtils.showShortToast("注册失败！");
                                    }
                                }
                            }

                            @Override
                            public void onError(String error) {
                                CommonUtils.showShortToast(error);
                            }
                        });
                break;
        }
    }
}
