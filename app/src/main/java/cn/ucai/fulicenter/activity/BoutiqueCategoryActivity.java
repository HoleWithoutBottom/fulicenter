package cn.ucai.fulicenter.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.Serializable;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.BoutiqueBean;
import cn.ucai.fulicenter.fragment.NewGoodsFragment;
import cn.ucai.fulicenter.utils.MFGT;

public class BoutiqueCategoryActivity extends AppCompatActivity {

    @Bind(R.id.iv_boutique_category_back)
    ImageView ivBoutiqueCategoryBack;
    @Bind(R.id.boutique_category_title)
    TextView boutiqueCategoryTitle;
    @Bind(R.id.fragment_boutique_category)
    RelativeLayout fragmentBoutiqueCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boutique_category);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        int id = getIntent().getIntExtra("BoutiqueBeanId", 1);
        String title = getIntent().getStringExtra("BoutiqueBeanTitle");
        NewGoodsFragment boutiqueCategoryFragment = new NewGoodsFragment();
        boutiqueCategoryFragment.setCat_id(id);
        boutiqueCategoryTitle.setText(title);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_boutique_category, boutiqueCategoryFragment);
        transaction.commit();
    }

    @OnClick(R.id.iv_boutique_category_back)
    public void onClick() {
        MFGT.finish(this);
    }
}
