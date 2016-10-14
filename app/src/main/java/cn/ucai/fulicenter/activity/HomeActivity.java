package cn.ucai.fulicenter.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import cn.ucai.fulicenter.R;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    RadioButton mRbCart, mRbNewGoods, mRbCategory, mRbBotique, mRbPersonal;
    TextView mTvCartHint;
    boolean isCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
        mRbNewGoods.setChecked(true);
        setListener();
    }

    private void setListener() {
        mRbBotique.setOnClickListener(this);
        mRbCategory.setOnClickListener(this);
        mRbCart.setOnClickListener(this);
        mRbNewGoods.setOnClickListener(this);
        mRbPersonal.setOnClickListener(this);
    }

    private void initView() {
        mRbBotique = (RadioButton) findViewById(R.id.rbBotique);
        mRbCart = (RadioButton) findViewById(R.id.rbCart);
        mRbNewGoods = (RadioButton) findViewById(R.id.rbNewGoods);
        mRbCategory = (RadioButton) findViewById(R.id.rbCategory);
        mRbPersonal = (RadioButton) findViewById(R.id.rbPersonal);
        mTvCartHint = (TextView) findViewById(R.id.tvCartHint);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rbBotique:
                setCart();
                break;
            case R.id.rbCart:
                setOtherRb();

                break;
            case R.id.rbCategory:
                setCart();
                break;
            case R.id.rbNewGoods:
                setCart();
                break;
            case R.id.rbPersonal:
                setCart();
                break;
        }
    }

    private void setOtherRb() { // 如果上次点了不是购物车，就把他们设为false
        if (!isCart) {
            isCart = true;
            mRbBotique.setChecked(false);
            mRbPersonal.setChecked(false);
            mRbNewGoods.setChecked(false);
            mRbCategory.setChecked(false);
        }
    }

    private void setCart() { //如果上次点了购物车，就把购物车的状态改为false
        if (isCart) {
            mRbCart.setChecked(false);
            isCart = false;
        }
    }

}
