package cn.ucai.fulicenter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import cn.ucai.fulicenter.activity.RegisterActivity;
import cn.ucai.fulicenter.utils.MFGT;

public class MainActivity extends AppCompatActivity {
    EditText mEtUserName,mEtPassword;
    Button btnLogin,btnGoToRegister;
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
        String userName = getIntent().getStringExtra("userName");
        mEtUserName.setText(userName);
    }

    private void setListener() {
        btnGoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, RegisterActivity.class);
                MFGT.startActivity(MainActivity.this,intent);
            }
        });
        
        onBack();
    }

    private void onBack() {
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initView() {
        mEtUserName= (EditText) findViewById(R.id.etUserName);
        mEtPassword= (EditText) findViewById(R.id.etPassword);
        btnLogin= (Button) findViewById(R.id.btnLogin);
        btnGoToRegister= (Button) findViewById(R.id.btnGoToRegister);
        mIvBack= (ImageView) findViewById(R.id.iv_login_back);
    }

}
