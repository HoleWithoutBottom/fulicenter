package cn.ucai.fulicenter.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.NewGoodsAdapter;
import cn.ucai.fulicenter.bean.NewGoodsBean;
import cn.ucai.fulicenter.utils.CommonUtils;
import cn.ucai.fulicenter.utils.L;
import cn.ucai.fulicenter.utils.OkHttpUtils;

public class CategoryActivity extends AppCompatActivity {

    @Bind(R.id.iv_category_back)
    ImageView ivCategoryBack;
    @Bind(R.id.tv_category_title)
    TextView tvCategoryTitle;
    @Bind(R.id.tv_category_price_orderUp)
    TextView tvCategoryPriceOrderUp;
    @Bind(R.id.tv_category_time_orderDown)
    TextView tvCategoryTimeOrderDown;

    ArrayList<NewGoodsBean> mNewGoodsBeanList;
    int mPageID = 1;
    @Bind(R.id.tv_category_refresh)
    TextView tvCategoryRefresh;
    @Bind(R.id.recycler_Category)
    RecyclerView recyclerCategory;
    GridLayoutManager gridLayoutManager;
    NewGoodsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        ButterKnife.bind(this);
        initData();

    }

    private void initData() {
        mNewGoodsBeanList = new ArrayList<>();
        int catId = getIntent().getIntExtra("goodsId", 1);
        if (catId != 1) {
            downloadGoodsDetailsBeanList(catId, I.ACTION_DOWNLOAD, mPageID);
        } else {
            CommonUtils.showShortToast("没有该物品");
        }
    }

    private void initAdapter() {
        gridLayoutManager = new GridLayoutManager(this, 2);
        mAdapter = new NewGoodsAdapter(mNewGoodsBeanList, this);
        recyclerCategory.setAdapter(mAdapter);
        recyclerCategory.setLayoutManager(gridLayoutManager);
    }

    private void downloadGoodsDetailsBeanList(int catId, int action, int pageId) {
        final OkHttpUtils<NewGoodsBean[]> utils = new OkHttpUtils<>(this);
        utils.setRequestUrl(I.REQUEST_FIND_GOODS_DETAILS)
                .addParam(I.GoodsDetails.KEY_CAT_ID, catId + "")
                .addParam(I.PAGE_ID, pageId + "")
                .addParam(I.PAGE_SIZE, I.PAGE_SIZE_DEFAULT + "")
                .targetClass(NewGoodsBean[].class)
                .execute(new OkHttpUtils.OnCompleteListener<NewGoodsBean[]>() {
                    @Override
                    public void onSuccess(NewGoodsBean[] result) {
                        if (result != null && result.length > 0) {
                            mNewGoodsBeanList = utils.array2List(result);
                            initAdapter();
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });

    }

    @OnClick({R.id.iv_category_back, R.id.tv_category_title, R.id.tv_category_price_orderUp, R.id.tv_category_time_orderDown})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_category_back:
                break;
            case R.id.tv_category_title:
                break;
            case R.id.tv_category_price_orderUp:
                break;
            case R.id.tv_category_time_orderDown:
                break;
        }
    }
}
