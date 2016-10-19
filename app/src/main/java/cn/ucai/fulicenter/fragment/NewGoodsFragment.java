package cn.ucai.fulicenter.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.NewGoodsAdapter;
import cn.ucai.fulicenter.bean.NewGoodsBean;
import cn.ucai.fulicenter.utils.CommonUtils;
import cn.ucai.fulicenter.utils.ImageLoader;
import cn.ucai.fulicenter.utils.OkHttpUtils;


public class NewGoodsFragment extends Fragment {
    View view;
    SwipeRefreshLayout mSwipe;
    TextView mTvNewgoodsRefresh;
    RecyclerView mRecyclerNewGoods;
    ArrayList<NewGoodsBean> mGoodsList;
    int mPageId = 1;
    NewGoodsAdapter myAdapter;
    GridLayoutManager gridLayoutManager;
    int mNewState;
    int cat_id = 0;

    public NewGoodsFragment() {
    }

    public void setCat_id(int cat_id) {
        this.cat_id = cat_id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_new_goods, container, false);
        initView();
        setListener();
        return view;
    }

    // 动态变化recyclerView的布局
    private void setListener() {
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == myAdapter.getItemCount() - 1 ? 2 : 1;
            }
        });
        setPullDown();
        setPullUp();


    }

    private void setPullUp() {
        mRecyclerNewGoods.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                mNewState = newState;
                myAdapter.setmNewState(newState);
                if (myAdapter.isMore() && newState == recyclerView.SCROLL_STATE_IDLE) {
                    mPageId++;
                    downLoadNewGoods(I.ACTION_PULL_UP, mPageId);
                }
            }
        });
    }

    private void setPullDown() {
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipe.setRefreshing(true);
                mSwipe.setEnabled(true);
                mTvNewgoodsRefresh.setVisibility(View.VISIBLE);
                mPageId = 1;
                downLoadNewGoods(I.ACTION_PULL_DOWN, mPageId);
            }
        });
    }

    private void initView() {
        mSwipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe);
        mRecyclerNewGoods = (RecyclerView) view.findViewById(R.id.recyclerNewGoods);
        mTvNewgoodsRefresh = (TextView) view.findViewById(R.id.tvNewGoodsRefresh);
        downLoadNewGoods(I.ACTION_DOWNLOAD, mPageId);
        mGoodsList = new ArrayList<>();
        // 每行显示2个
        gridLayoutManager = new GridLayoutManager(getActivity(), I.COLUM_NUM, LinearLayoutManager.VERTICAL, false);
        myAdapter = new NewGoodsAdapter(mGoodsList, getActivity());
        mRecyclerNewGoods.setAdapter(myAdapter);
        mRecyclerNewGoods.setLayoutManager(gridLayoutManager);

    }

    private void downLoadNewGoods(final int action, int pageid) {
        final OkHttpUtils<NewGoodsBean[]> utils = new OkHttpUtils<>(getActivity());
        utils.setRequestUrl(I.REQUEST_FIND_NEW_BOUTIQUE_GOODS)
                .addParam(I.GoodsDetails.KEY_CAT_ID, cat_id + "")
                .addParam(I.PAGE_ID, pageid + "")
                .addParam(I.PAGE_SIZE, 10 + "")
                .targetClass(NewGoodsBean[].class)
                .execute(new OkHttpUtils.OnCompleteListener<NewGoodsBean[]>() {
                    @Override
                    public void onSuccess(NewGoodsBean[] result) {
                        myAdapter.setMore(result != null && result.length > 0);
                        if (action == I.ACTION_PULL_UP) {
                            if (!myAdapter.isMore()) {
                                myAdapter.setFoot("没有更多数据了");
                            }
                            return;
                        }
                        ArrayList<NewGoodsBean> goodslist = utils.array2List(result);
                        myAdapter.setFoot("上拉加载更多数据...");
                        if (result != null) {
                            switch (action) {
                                case I.ACTION_DOWNLOAD:
                                    myAdapter.initGoodsList(goodslist);
                                    break;
                                case I.ACTION_PULL_DOWN:
                                    myAdapter.initGoodsList(goodslist);
                                    mSwipe.setRefreshing(false);
                                    ImageLoader.release();
                                    mTvNewgoodsRefresh.setVisibility(View.GONE);
                                    break;
                                case I.ACTION_PULL_UP:
                                    myAdapter.addGoodsList(goodslist);
                                    break;
                            }

                        }
                    }

                    @Override
                    public void onError(String error) {
                        mSwipe.setRefreshing(false);
                        mTvNewgoodsRefresh.setVisibility(View.GONE);
                        myAdapter.setMore(false);
                        CommonUtils.showShortToast(error);
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
