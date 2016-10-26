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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.CollectsAdapter;
import cn.ucai.fulicenter.bean.CollectBean;
import cn.ucai.fulicenter.bean.NewGoodsBean;
import cn.ucai.fulicenter.bean.UserAvatar;
import cn.ucai.fulicenter.utils.CommonUtils;
import cn.ucai.fulicenter.utils.ImageLoader;
import cn.ucai.fulicenter.utils.MFGT;
import cn.ucai.fulicenter.utils.OkHttpUtils;

public class CollectsActivity extends AppCompatActivity {

    @Bind(R.id.iv_myCollections_back)
    ImageView ivMyCollectionsBack;
    @Bind(R.id.tv_collections_refresh)
    TextView tvCollectionsRefresh;
    @Bind(R.id.recycler_collections)
    RecyclerView recyclerCollections;
    @Bind(R.id.swipe_collections)
    SwipeRefreshLayout swipeCollections;
    UserAvatar userAvatar;
    CollectsAdapter mAdapter;
    ArrayList<CollectBean> collectsList;
    GridLayoutManager glm;
    int mNewState;
    int mPageId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collects);
        ButterKnife.bind(this);
        initData();
        setListener();
    }

    private void setListener() {
        glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == mAdapter.getItemCount() - 1 ? 2 : 1;
            }
        });
        setPullDown();
        setPullUp();
    }

    private void setPullUp() {
        recyclerCollections.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                mNewState = newState;
                mAdapter.setmNewState(newState);
                if (mAdapter.isMore() && newState == recyclerView.SCROLL_STATE_IDLE) {
                    mPageId++;
                    downloadCollectsList(I.ACTION_PULL_UP, mPageId);
                }
            }
        });
    }

    private void setPullDown() {
        swipeCollections.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeCollections.setRefreshing(true);
                swipeCollections.setEnabled(true);
                tvCollectionsRefresh.setVisibility(View.VISIBLE);
                mPageId = 1;
                downloadCollectsList(I.ACTION_PULL_DOWN, mPageId);
            }
        });
    }

    private void initData() {
        userAvatar = FuLiCenterApplication.userAvatar;
        collectsList = new ArrayList<>();
        mAdapter = new CollectsAdapter(collectsList, CollectsActivity.this);
        glm = new GridLayoutManager(CollectsActivity.this, 2);
        recyclerCollections.setAdapter(mAdapter);
        recyclerCollections.setLayoutManager(glm);
        downloadCollectsList(I.ACTION_DOWNLOAD,mPageId);
    }

    private void downloadCollectsList(final int action,int pageId) {
        final OkHttpUtils<CollectBean[]> utils = new OkHttpUtils<>(CollectsActivity.this);
        utils.setRequestUrl(I.REQUEST_FIND_COLLECTS)
                .addParam(I.Collect.USER_NAME, userAvatar.getMuserName())
                .addParam(I.PAGE_ID, pageId + "")
                .addParam(I.PAGE_SIZE, I.PAGE_SIZE_DEFAULT + "")
                .targetClass(CollectBean[].class)
                .execute(new OkHttpUtils.OnCompleteListener<CollectBean[]>() {
                    @Override
                    public void onSuccess(CollectBean[] result) {
                        mAdapter.setMore(result != null && result.length > 0);
                        if (action == I.ACTION_PULL_UP) {
                            if (!mAdapter.isMore()) {
                                mAdapter.setFoot("没有更多数据了");
                            }
                            return;
                        }
                        ArrayList<CollectBean> list = utils.array2List(result);
                        mAdapter.setFoot("上拉加载更多数据...");
                        if (result != null) {
                            switch (action) {
                                case I.ACTION_DOWNLOAD:
                                    mAdapter.initCollectsList(list);
                                    break;
                                case I.ACTION_PULL_DOWN:
                                    mAdapter.initCollectsList(list);
                                    swipeCollections.setRefreshing(false);
                                    ImageLoader.release();
                                    tvCollectionsRefresh.setVisibility(View.GONE);
                                    break;
                                case I.ACTION_PULL_UP:
                                    mAdapter.addCollectsList(list);
                                    break;
                            }

                        }
                    }

                    @Override
                    public void onError(String error) {
                        swipeCollections.setRefreshing(false);
                        tvCollectionsRefresh.setVisibility(View.GONE);
                        mAdapter.setMore(false);
                        CommonUtils.showShortToast(error);
                    }
                });
    }

    @OnClick(R.id.iv_myCollections_back)
    public void onClick() {
        MFGT.finish(CollectsActivity.this);
    }

}
