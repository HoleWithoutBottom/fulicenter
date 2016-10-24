package cn.ucai.fulicenter.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.UserAvatar;
import cn.ucai.fulicenter.utils.ImageLoader;
import cn.ucai.fulicenter.utils.MFGT;

public class SettingsActivity extends AppCompatActivity {

    @Bind(R.id.iv_settings_back)
    ImageView ivSettingsBack;
    @Bind(R.id.iv_settings_avatar)
    ImageView ivSettingsAvatar;
    @Bind(R.id.btn_settings_exitLogin)
    Button btnSettingsExitLogin;
    @Bind(R.id.tv_settings_nick)
    TextView tvSettingsNick;
    @Bind(R.id.tv_settings_update_nick)
    TextView tvSettingsUpdateNick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        UserAvatar userAvatar = FuLiCenterApplication.userAvatar;
        if (userAvatar != null) {
            tvSettingsNick.setText(userAvatar.getMuserNick());
            tvSettingsUpdateNick.setText(userAvatar.getMuserNick());
            ImageLoader.setAvatar(ImageLoader.getUrl(userAvatar), SettingsActivity.this, ivSettingsAvatar);
        }
    }

    @OnClick(R.id.btn_settings_exitLogin)
    public void onClick() {
        SharedPreferences sp = getSharedPreferences("userInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
        FuLiCenterApplication.userAvatar = null;
        Intent intent=new Intent(this,HomeActivity.class);
        MFGT.startActivity(this,intent);
        MFGT.finish(this);
    }
}
