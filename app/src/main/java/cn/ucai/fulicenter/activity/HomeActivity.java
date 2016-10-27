package cn.ucai.fulicenter.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.MainActivity;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.fragment.BoutiqueFragment;
import cn.ucai.fulicenter.fragment.CartFragment;
import cn.ucai.fulicenter.fragment.CategoryFragment;
import cn.ucai.fulicenter.fragment.NewGoodsFragment;
import cn.ucai.fulicenter.fragment.PersonalFragment;
import cn.ucai.fulicenter.utils.L;
import cn.ucai.fulicenter.utils.MFGT;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    RadioButton mRbCart, mRbNewGoods, mRbCategory, mRbBotique, mRbPersonal;
    TextView mTvCartHint;
    FragmentManager manager;
    NewGoodsFragment newGoodsFragment;
    BoutiqueFragment boutiqueFragment;
    CategoryFragment categoryFragment;
    CartFragment cartFragment;
    PersonalFragment personalFragment;
    ArrayList<Fragment> mFragmentList;
    ViewPagerAdapter mAdapter;
    ViewPager mViewPagerFragment;
    int index;
    RadioButton[] rbs;
    CountCart receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
        initReceiver();
        initFragment();
        setListener();
    }

    private void initReceiver() {
        receiver = new CountCart();
        IntentFilter filter = new IntentFilter("countCart");
        registerReceiver(receiver, filter);
    }

    private void initFragment() {
        mFragmentList = new ArrayList<>();
        manager = getSupportFragmentManager();
        newGoodsFragment = new NewGoodsFragment();
        boutiqueFragment = new BoutiqueFragment();
        categoryFragment = new CategoryFragment();
        cartFragment = new CartFragment();
        personalFragment = new PersonalFragment();
        mFragmentList.add(newGoodsFragment);
        mFragmentList.add(boutiqueFragment);
        mFragmentList.add(categoryFragment);
        mFragmentList.add(cartFragment);
        mFragmentList.add(personalFragment);
        // 开始默认进入新品界面
        mRbNewGoods.setChecked(true);
        mAdapter = new ViewPagerAdapter(manager);
        mViewPagerFragment.setAdapter(mAdapter);
    }

    private void setListener() {
        mRbBotique.setOnClickListener(this);
        mRbCategory.setOnClickListener(this);
        mRbCart.setOnClickListener(this);
        mRbNewGoods.setOnClickListener(this);
        mRbPersonal.setOnClickListener(this);
        mViewPagerFragment.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                index = position;
                setCheck();
                if (index == 3) {
                    if (FuLiCenterApplication.userAvatar == null) {
                        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                        MFGT.startActivityForResult(HomeActivity.this, intent, I.REQUEST_CODE_CART);
                        return;
                    }
                }
                if (index == 4) {
                    if (FuLiCenterApplication.userAvatar == null) {
                        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                        MFGT.startActivityForResult(HomeActivity.this, intent, I.REQUEST_CODE_SUCCESS_LOGIN);
                        return;
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initView() {
        mRbBotique = (RadioButton) findViewById(R.id.rbBotique);
        mRbCart = (RadioButton) findViewById(R.id.rbCart);
        mRbNewGoods = (RadioButton) findViewById(R.id.rbNewGoods);
        mRbCategory = (RadioButton) findViewById(R.id.rbCategory);
        mRbPersonal = (RadioButton) findViewById(R.id.rbPersonal);
        mTvCartHint = (TextView) findViewById(R.id.tvCartHint);
        mViewPagerFragment = (ViewPager) findViewById(R.id.fragment_viewPager);
        rbs = new RadioButton[]{mRbNewGoods, mRbBotique, mRbCategory, mRbCart, mRbPersonal};
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rbBotique:
                index = 1;
                setCheck();
                mViewPagerFragment.setCurrentItem(index);
                break;
            case R.id.rbCart:
                if (FuLiCenterApplication.userAvatar == null) {
                    Intent intent = new Intent(this, MainActivity.class);
                    mRbPersonal.setChecked(false);
                    MFGT.startActivityForResult(this, intent, I.REQUEST_CODE_CART);
                    return;
                }
                index = 3;
                setCheck();
                mViewPagerFragment.setCurrentItem(index);
                break;
            case R.id.rbCategory:
                index = 2;
                setCheck();
                mViewPagerFragment.setCurrentItem(index);
                break;
            case R.id.rbNewGoods:
                index = 0;
                setCheck();
                mViewPagerFragment.setCurrentItem(index);
                break;
            case R.id.rbPersonal:
                if (FuLiCenterApplication.userAvatar == null) {
                    Intent intent = new Intent(this, MainActivity.class);
                    mRbPersonal.setChecked(false);
                    MFGT.startActivityForResult(this, intent, I.REQUEST_CODE_SUCCESS_LOGIN);
                    return;
                }
                index = 4;
                setCheck();
                mViewPagerFragment.setCurrentItem(index);
                break;
        }
    }


    public void setCheck() {
        for (int i = 0; i < rbs.length; i++) {
            if (i == index) {
                rbs[i].setChecked(true);
            } else {
                rbs[i].setChecked(false);
            }
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (index == 4 && FuLiCenterApplication.userAvatar == null) {
            index = 0;
        }
        mViewPagerFragment.setCurrentItem(index);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (FuLiCenterApplication.userAvatar != null) {
            if (requestCode == I.REQUEST_CODE_SUCCESS_LOGIN) {
                index = 4;
            }
            if (requestCode == I.REQUEST_CODE_CART) {
                index = 3;
            }
        }
    }

    public void onBackPressed() {
        if (FuLiCenterApplication.userAvatar == null) {
            MFGT.finish(this);
        }
    }

    class CountCart extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int count = intent.getIntExtra("count", 0);
            mTvCartHint.setText(count + "");
        }
    }
}
