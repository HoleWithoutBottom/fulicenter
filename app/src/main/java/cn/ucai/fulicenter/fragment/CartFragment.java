package cn.ucai.fulicenter.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.GoodsDetailBean;
import cn.ucai.fulicenter.utils.ImageLoader;

/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment {


    @Bind(R.id.tv_total)
    TextView tvTotal;
    @Bind(R.id.btn_cart_buy)
    Button btnCartBuy;
    @Bind(R.id.tv_cart_refresh)
    TextView tvCartRefresh;
    @Bind(R.id.recycler_cart)
    RecyclerView recyclerCart;
    @Bind(R.id.swipe_cart)
    SwipeRefreshLayout swipeCart;
    CartAdapter mAdapter;
    ArrayList<GoodsDetailBean> goodsList;
    LinearLayoutManager manager;
    @Bind(R.id.iv_cart_none)
    ImageView ivCartNone;

    public CartFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        ButterKnife.bind(this, view);
        initData();
        return view;
    }


    private void initData() {
        goodsList = new ArrayList<>();
        goodsList = FuLiCenterApplication.detailBeenList;
        if (goodsList == null || goodsList.size() == 0) {
            ivCartNone.setVisibility(View.VISIBLE);
        }
        mAdapter = new CartAdapter(goodsList, getActivity());
        recyclerCart.setAdapter(mAdapter);
        manager = new LinearLayoutManager(getActivity());
        recyclerCart.setLayoutManager(manager);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.btn_cart_buy)
    public void onClick() {
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        ImageView ivCart, ivAdd, ivDec;
        TextView tvGoodsName, tvGoodsCount, tvGoodsPrice;
        LinearLayout linearLayoutCart;

        public CartViewHolder(View itemView) {
            super(itemView);
            checkBox = (CheckBox) itemView.findViewById(R.id.chk_cart);
            ivCart = (ImageView) itemView.findViewById(R.id.iv_cart);
            ivAdd = (ImageView) itemView.findViewById(R.id.iv_add_cart);
            ivDec = (ImageView) itemView.findViewById(R.id.iv_cart_del);
            tvGoodsName = (TextView) itemView.findViewById(R.id.tv_cart_goodsName);
            tvGoodsCount = (TextView) itemView.findViewById(R.id.tv_cart_count);
            tvGoodsPrice = (TextView) itemView.findViewById(R.id.tv_cart_price);
            linearLayoutCart = (LinearLayout) itemView.findViewById(R.id.linearLayout_cart);
            // 增加商品数量
            ivAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String goodsCount = tvGoodsCount.getText().toString();
                    String substring = goodsCount.substring(1, goodsCount.length() - 1);
                    int count = Integer.parseInt(substring) + 1;
                    tvGoodsCount.setText("(" + count + ")");
                }
            });
            // 减少商品数量
            ivDec.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String goodsCount = tvGoodsCount.getText().toString();
                    String substring = goodsCount.substring(1, goodsCount.length() - 1);
                    int count = Integer.parseInt(substring) - 1;
                    if (count == 0) {
                        mAdapter.decCount(mAdapter.getPosition());
                        return;
                    }
                    tvGoodsCount.setText("(" + count + ")");
                }
            });
        }

    }

    class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {
        ArrayList<GoodsDetailBean> mList;
        Context context;
        int position;

        public CartAdapter(ArrayList<GoodsDetailBean> mList, Context context) {
            this.mList = mList;
            this.context = context;
        }

        @Override
        public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View layout = View.inflate(context, R.layout.cart_item, null);
            CartViewHolder holder = new CartViewHolder(layout);
            return holder;
        }

        @Override
        public void onBindViewHolder(CartViewHolder holder, final int position) {
            GoodsDetailBean detailBean = mList.get(position);
            holder.tvGoodsPrice.setText(detailBean.getCurrencyPrice());
            holder.tvGoodsName.setText(detailBean.getGoodsName());
            holder.tvGoodsCount.setText("(" + 1 + ")");
            ImageLoader.downloadImg(context, holder.ivCart, detailBean.getGoodsThumb());
            holder.linearLayoutCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAdapter.position = position;
                }
            });

        }

        @Override
        public int getItemCount() {
            return mList != null ? mList.size() : 0;
        }

        public int getPosition() {
            return position;
        }

        public void decCount(int position) {
            mList.remove(position);
            notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (goodsList == null || goodsList.size() == 0) {
            ivCartNone.setVisibility(View.VISIBLE);
        } else {
            ivCartNone.setVisibility(View.GONE);
        }
        mAdapter.notifyDataSetChanged();
    }
}
