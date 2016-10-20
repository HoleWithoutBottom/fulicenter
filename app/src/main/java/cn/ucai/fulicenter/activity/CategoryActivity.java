package cn.ucai.fulicenter.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.NewGoodsAdapter;
import cn.ucai.fulicenter.bean.NewGoodsBean;
import cn.ucai.fulicenter.utils.CommonUtils;
import cn.ucai.fulicenter.utils.L;
import cn.ucai.fulicenter.utils.MFGT;
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
    int catId = 1;
    ArrayList<NewGoodsBean> mNewGoodsBeanList;
    int mPageID = 1;
    @Bind(R.id.tv_category_refresh)
    TextView tvCategoryRefresh;
    @Bind(R.id.recycler_Category)
    RecyclerView recyclerCategory;
    GridLayoutManager gridLayoutManager;
    NewGoodsAdapter mAdapter;
    @Bind(R.id.swipe_category)
    SwipeRefreshLayout swipeCategory;
    Comparator<NewGoodsBean> comparatorPrice;
    Comparator<NewGoodsBean> comparatorAddTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        ButterKnife.bind(this);
        initData();
        initAdapter();
        initComparator();
        setListener();
    }

    private void initComparator() {
        comparatorPrice = new Comparator<NewGoodsBean>() {
            @Override
            public int compare(NewGoodsBean newGoodsBean1, NewGoodsBean newGoodsBean2) {
                String shopPrice2 = newGoodsBean2.getShopPrice();
                String shopPrice1 = newGoodsBean1.getShopPrice();
                int i =0;
                int j =0;
                Pattern pattern = Pattern.compile("[0-9]");
                Matcher matcher = pattern.matcher(shopPrice1);
                if (matcher.find()){
                    i=shopPrice1.indexOf(matcher.group());
                }
                matcher=pattern.matcher(shopPrice2);
                if (matcher.find()){
                    j=shopPrice2.indexOf(matcher.group());
                }
                return Integer.parseInt(shopPrice1.substring(i)) - Integer.parseInt(shopPrice2.substring(j));
            }
        };
        comparatorAddTime=new Comparator<NewGoodsBean>() {
            @Override
            public int compare(NewGoodsBean newGoodsBean1, NewGoodsBean newGoodsBean2) {
                //L.e(newGoodsBean1.getAddTime()+":"+newGoodsBean2.getAddTime());
                long l = newGoodsBean2.getAddTime() - newGoodsBean1.getAddTime();
                return (int)l;
            }
        };
    }

    private void setListener() {
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == mAdapter.getItemCount() - 1 ? 2 : 1;
            }
        });
        swipeCategory.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeCategory.setRefreshing(true);
                swipeCategory.setEnabled(true);
                mPageID = 1;
                downloadNewGoodsBeanList(catId, I.ACTION_PULL_DOWN, mPageID);
                tvCategoryRefresh.setVisibility(View.VISIBLE);
            }
        });
        recyclerCategory.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastPosition;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                lastPosition = gridLayoutManager.findLastVisibleItemPosition();
                mAdapter.setmNewState(newState);
                if (mAdapter.isMore() && newState == recyclerView.SCROLL_STATE_IDLE && mAdapter.getItemCount() - 1 == lastPosition) {
                    mPageID++;
                    downloadNewGoodsBeanList(catId, I.ACTION_PULL_UP, mPageID);
                }
            }
        });
    }

    private void initData() {
        mNewGoodsBeanList = new ArrayList<>();
        catId = getIntent().getIntExtra("goodsId", 1);
        String name = getIntent().getStringExtra("name");
        tvCategoryTitle.setText(name);
        if (catId != 1) {
            downloadNewGoodsBeanList(catId, I.ACTION_DOWNLOAD, mPageID);
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

    private void downloadNewGoodsBeanList(int catId, final int action, int pageId) {
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
                            mAdapter.setMore(true);
                            if (action == I.ACTION_PULL_UP) {
                                if (!mAdapter.isMore()) {
                                    mAdapter.setFoot("没有多余数据");
                                }
                                return;
                            }
                            mAdapter.setFoot("下拉加载更多数据");
                            switch (action) {
                                case I.ACTION_DOWNLOAD:
                                    mAdapter.initGoodsList(utils.array2List(result));
                                    break;
                                case I.ACTION_PULL_DOWN:
                                    mAdapter.initGoodsList(utils.array2List(result));
                                    swipeCategory.setRefreshing(false);
                                    tvCategoryRefresh.setVisibility(View.GONE);
                                    break;
                                case I.ACTION_PULL_UP:
                                    mAdapter.addGoodsList(utils.array2List(result));
                                    break;
                            }
                        } else {
                            mAdapter.setMore(false);
                            mAdapter.setFoot("没有多余数据");
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
                MFGT.finish(this);
                break;
            case R.id.tv_category_title:
                MFGT.finish(this);
                break;
            case R.id.tv_category_price_orderUp:
                Collections.sort(mNewGoodsBeanList, comparatorPrice);
                mAdapter.initGoodsList(mNewGoodsBeanList);
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.tv_category_time_orderDown:
                L.e("sb");
                Collections.sort(mNewGoodsBeanList, comparatorAddTime);
                mAdapter.initGoodsList(mNewGoodsBeanList);
                mAdapter.notifyDataSetChanged();
                break;
        }
    }
}
