package cn.ucai.fulicenter.activity;

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

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.fragment.BoutiqueFragment;
import cn.ucai.fulicenter.fragment.NewGoodsFragment;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    RadioButton mRbCart, mRbNewGoods, mRbCategory, mRbBotique, mRbPersonal;
    TextView mTvCartHint;
    FragmentManager manager;
    NewGoodsFragment newGoodsFragment;
    BoutiqueFragment boutiqueFragment;
    ArrayList<Fragment> mFragmentList;
    ViewPagerAdapter mAdapter;
    ViewPager mViewPagerFragment;
    int index;
    RadioButton[] rbs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
        initFragment();
        setListener();
    }

    private void initFragment() {
        mFragmentList = new ArrayList<>();
        manager = getSupportFragmentManager();
        newGoodsFragment = new NewGoodsFragment();
        boutiqueFragment = new BoutiqueFragment();
        mFragmentList.add(newGoodsFragment);
        mFragmentList.add(boutiqueFragment);
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
                index = 3;
                setCheck();
                break;
            case R.id.rbCategory:
                index = 2;
                setCheck();
                break;
            case R.id.rbNewGoods:
                index = 0;
                setCheck();
                mViewPagerFragment.setCurrentItem(index);
                break;
            case R.id.rbPersonal:
                index = 4;
                setCheck();
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
}
