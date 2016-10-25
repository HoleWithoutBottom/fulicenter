package cn.ucai.fulicenter.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.Result;
import cn.ucai.fulicenter.bean.UserAvatar;
import cn.ucai.fulicenter.dao.UserDao;
import cn.ucai.fulicenter.utils.CommonUtils;
import cn.ucai.fulicenter.utils.ImageLoader;
import cn.ucai.fulicenter.utils.L;
import cn.ucai.fulicenter.utils.MFGT;
import cn.ucai.fulicenter.utils.OkHttpUtils;
import cn.ucai.fulicenter.utils.OnSetAvatarListener;

public class SettingsActivity extends AppCompatActivity {


    @Bind(R.id.iv_settings_back)
    ImageView ivSettingsBack;
    @Bind(R.id.iv_settings_avatar)
    ImageView ivSettingsAvatar;
    @Bind(R.id.tv_settings_update_username)
    TextView tvSettingsUpdateUsername;
    @Bind(R.id.tv_settings_nick)
    TextView tvSettingsNick;
    @Bind(R.id.btn_settings_exitLogin)
    Button btnSettingsExitLogin;
    UserAvatar userAvatar;
    OnSetAvatarListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        userAvatar = FuLiCenterApplication.userAvatar;
        if (userAvatar != null) {
            tvSettingsNick.setText(userAvatar.getMuserNick());
            tvSettingsUpdateUsername.setText(userAvatar.getMuserName());
            ImageLoader.setAvatar(ImageLoader.getUrl(userAvatar), SettingsActivity.this, ivSettingsAvatar);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @OnClick(R.id.btn_settings_exitLogin)
    public void onClick() {
        SharedPreferences sp = getSharedPreferences("userInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
        FuLiCenterApplication.userAvatar = null;
        MFGT.finish(this);
    }

    @OnClick({R.id.iv_settings_back, R.id.iv_settings_avatar, R.id.tv_settings_update_username, R.id.tv_settings_nick})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_settings_back:
                MFGT.finish(this);
                break;
            case R.id.iv_settings_avatar:
                // 父容器是整个activity
                listener = new OnSetAvatarListener(SettingsActivity.this, R.id.line_settings,
                        userAvatar.getMuserName(), I.AVATAR_TYPE_USER_PATH);

                break;
            case R.id.tv_settings_update_username:
                CommonUtils.showShortToast("用户名不能更改");
                break;
            case R.id.tv_settings_nick:
                View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_item, null, false);
                final EditText etUpdateNick = (EditText) inflate.findViewById(R.id.et_settings_update_nick);
                etUpdateNick.setText(userAvatar.getMuserNick());
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("是否更改昵称")
                        .setView(inflate)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String nick = etUpdateNick.getText().toString();
                                if (nick == null || nick.length() == 0) {
                                    etUpdateNick.setError("昵称不能为空!");
                                    etUpdateNick.requestFocus();
                                    return;
                                }
                                if (nick.equals(userAvatar.getMuserNick())) {
                                    etUpdateNick.setError("昵称没有改变");
                                    return;
                                } else {
                                    // 请求数据库更改昵称
                                    OkHttpUtils<Result> utils = new OkHttpUtils<Result>(SettingsActivity.this);
                                    utils.setRequestUrl(I.REQUEST_UPDATE_USER_NICK)
                                            .addParam(I.User.USER_NAME, userAvatar.getMuserName())
                                            .addParam(I.User.NICK, nick)
                                            .targetClass(Result.class)
                                            .execute(new OkHttpUtils.OnCompleteListener<Result>() {
                                                @Override
                                                public void onSuccess(Result result) {
                                                    if (result != null) {
                                                        if (result.getRetCode() == 0) {
                                                            String json = result.getRetData().toString();
                                                            Gson gson = new Gson();
                                                            UserAvatar user = gson.fromJson(json, UserAvatar.class);
                                                            FuLiCenterApplication.setUserAvatar(user);
                                                            UserDao dao = new UserDao(SettingsActivity.this);
                                                            if (dao.updateUser(user)) {
                                                                CommonUtils.showShortToast("更改成功！");
                                                                tvSettingsNick.setText(user.getMuserNick());
                                                            }
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onError(String error) {

                                                }
                                            });
                                }
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).create()
                        .show();

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        listener.setAvatar(requestCode, data, ivSettingsAvatar);
        if (requestCode == OnSetAvatarListener.REQUEST_CROP_PHOTO) {
            updateAvatar();
        }
    }

    public void updateAvatar() {
        File dir = OnSetAvatarListener.getAvatarFile(SettingsActivity.this, I.AVATAR_TYPE_USER_PATH);
        File file = new File(dir, userAvatar.getMuserName() + ".jpg");
        OkHttpUtils<Result> utils = new OkHttpUtils<>(SettingsActivity.this);
        utils.setRequestUrl(I.REQUEST_UPDATE_AVATAR)
                .addParam(I.NAME_OR_HXID, userAvatar.getMuserName())
                .addParam(I.AVATAR_TYPE, I.AVATAR_TYPE_USER_PATH)
                .targetClass(Result.class)
                .addFile2(file)
                .post()
                .execute(new OkHttpUtils.OnCompleteListener<Result>() {
                    @Override
                    public void onSuccess(Result result) {
                        if (result != null) {
                            if (result.getRetCode() == 0) {
                                String json = result.getRetData().toString();
                                Gson gson = new Gson();
                                UserAvatar user = gson.fromJson(json, UserAvatar.class);
                                FuLiCenterApplication.setUserAvatar(user);
                                userAvatar = user;
                                // 清除之前的头像缓存
                                ImageLoader.release();
                                ImageLoader.setAvatar(ImageLoader.getUrl(user), SettingsActivity.this, ivSettingsAvatar);
                            }
                        }
                    }

                    @Override
                    public void onError(String error) {
                        L.e(error);
                    }
                });
    }
}
