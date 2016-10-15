package cn.ucai.fulicenter.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.NewGoodsBean;
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
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    int mNewState;

    public NewGoodsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_new_goods, container, false);
        initView();
        setListener();
        return view;
    }

    private void setListener() {
        setPullDown();
        mRecyclerNewGoods.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                mNewState = newState;
                if (myAdapter.isMore && newState == recyclerView.SCROLL_STATE_IDLE) {
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
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(I.COLUM_NUM, StaggeredGridLayoutManager.VERTICAL);
        myAdapter = new NewGoodsAdapter(mGoodsList, getActivity());
        mRecyclerNewGoods.setAdapter(myAdapter);
        mRecyclerNewGoods.setLayoutManager(staggeredGridLayoutManager);

    }

    private void downLoadNewGoods(final int action, int pageid) {
        final OkHttpUtils<NewGoodsBean[]> utils = new OkHttpUtils<>(getActivity());
        utils.setRequestUrl(I.REQUEST_FIND_NEW_BOUTIQUE_GOODS)
                .addParam(I.GoodsDetails.KEY_CAT_ID, I.CAT_ID + "")
                .addParam(I.PAGE_ID, pageid + "")
                .addParam(I.PAGE_SIZE, 10 + "")
                .targetClass(NewGoodsBean[].class)
                .execute(new OkHttpUtils.OnCompleteListener<NewGoodsBean[]>() {
                    @Override
                    public void onSuccess(NewGoodsBean[] result) {
                        myAdapter.setMore(result != null && result.length > 0);
                        if (action == I.ACTION_PULL_UP) {
                            if (myAdapter.isMore()) {
                                myAdapter.setFoot("没有更多数据了");
                            }
                            return;
                        }
                        ArrayList<NewGoodsBean> goodslist = utils.array2List(result);
                        myAdapter.setFoot("下拉加载更多数据...");
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

                    }
                });
    }

    class NewGoodsViewHolder extends RecyclerView.ViewHolder {
        ImageView ivNewGoods;
        TextView tvName, tvPrice;

        public NewGoodsViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_newGoods_name);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_newGoods_price);
            ivNewGoods = (ImageView) itemView.findViewById(R.id.iv_newGoods);
        }
    }

    class FootHolder extends RecyclerView.ViewHolder {
        TextView tvFoot;

        public FootHolder(View itemView) {
            super(itemView);
            tvFoot = (TextView) itemView.findViewById(R.id.tvFoot);
        }
    }

    class NewGoodsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        ArrayList<NewGoodsBean> mlist;
        Context context;
        RecyclerView parent;
        String footText;
        boolean isMore;

        public boolean isMore() {
            return isMore;
        }

        public void setMore(boolean more) {
            isMore = more;
        }

        public NewGoodsAdapter(ArrayList<NewGoodsBean> mlist, Context context) {
            this.mlist = mlist;
            this.context = context;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder holder = null;
            this.parent = (RecyclerView) parent;
            View layout;
            switch (viewType) {
                case I.TYPE_FOOTER:
                    layout = View.inflate(context, R.layout.item_foot, null);
                    holder = new FootHolder(layout);

                    break;
                case I.TYPE_ITEM:
                    layout = View.inflate(context, R.layout.newgoods_item, null);
                    holder = new NewGoodsViewHolder(layout);
                    break;
            }
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (position == getItemCount() - 1) {
                FootHolder footHolder = (FootHolder) holder;
                footHolder.tvFoot.setText(this.footText);
                return;
            } else {
                NewGoodsViewHolder newGoodsHolder = (NewGoodsViewHolder) holder;
                NewGoodsBean goodsBean = mlist.get(position);
                newGoodsHolder.tvName.setText(goodsBean.getGoodsName());
                newGoodsHolder.tvPrice.setText(goodsBean.getPromotePrice());
                String url = I.SERVER_ROOT + I.REQUEST_DOWNLOAD_IMAGE;
                ImageLoader.build(url)
                        .addParam(I.IMAGE_URL, goodsBean.getGoodsThumb())
                        .listener(parent)
                        .imageView(newGoodsHolder.ivNewGoods)
                        .setDragging(mNewState!=RecyclerView.SCROLL_STATE_DRAGGING)
                        .showImage(context);
            }
        }


        private void initGoodsList(ArrayList<NewGoodsBean> goodslist) {
            mlist.clear();
            mlist.addAll(goodslist);
            notifyDataSetChanged();
        }

        private void addGoodsList(ArrayList<NewGoodsBean> goodslist) {
            mlist.addAll(goodslist);
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return mlist == null ? 0 : mlist.size() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == getItemCount() - 1) {
                return I.TYPE_FOOTER;
            } else {
                return I.TYPE_ITEM;
            }
        }

        public void setFoot(String footText) {
            this.footText = footText;
            notifyDataSetChanged();
        }
    }
}
