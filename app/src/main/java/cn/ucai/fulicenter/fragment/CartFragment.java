package cn.ucai.fulicenter.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.MainActivity;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.CartAdapter;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.utils.L;
import cn.ucai.fulicenter.utils.MFGT;
import cn.ucai.fulicenter.utils.OkHttpUtils;

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
    ArrayList<CartBean> goodsList;
    LinearLayoutManager manager;
    @Bind(R.id.iv_cart_none)
    ImageView ivCartNone;
    @Bind(R.id.tv_spare)
    TextView tvSpare;
    UpdateCartReceiver receiver;

    public CartFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        ButterKnife.bind(this, view);
        initData();
        if (FuLiCenterApplication.userAvatar != null) {
            downloadCart();
        }
        setListener();
        return view;
    }

    private void setListener() {
        // 下拉刷新
        swipeCart.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeCart.setRefreshing(true);
                swipeCart.setEnabled(true);
                tvCartRefresh.setVisibility(View.VISIBLE);
                downloadCart();
                tvCartRefresh.setVisibility(View.GONE);
                swipeCart.setRefreshing(false);
            }

        });
    }

    private void downloadCart() {
        final OkHttpUtils<CartBean[]> utils = new OkHttpUtils<CartBean[]>(getActivity());
        utils.setRequestUrl(I.REQUEST_FIND_CARTS)
                .addParam(I.Cart.USER_NAME, FuLiCenterApplication.userAvatar.getMuserName())
                .targetClass(CartBean[].class)
                .execute(new OkHttpUtils.OnCompleteListener<CartBean[]>() {
                    @Override
                    public void onSuccess(CartBean[] result) {
                        if (result == null || result.length == 0) {
                            ivCartNone.setVisibility(View.VISIBLE);
                        } else {
                            ivCartNone.setVisibility(View.GONE);
                        }
                        if (result != null) {
                            ArrayList<CartBean> list = utils.array2List(result);
                            goodsList = list;
                            mAdapter.initList(list);
                            mAdapter.getTotal();
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
    }


    private void initData() {
        goodsList = new ArrayList<>();
        if (FuLiCenterApplication.userAvatar != null) {
            downloadCart();
            if (goodsList == null || goodsList.size() == 0) {
                ivCartNone.setVisibility(View.VISIBLE);
            } else {
                ivCartNone.setVisibility(View.GONE);
            }
        }
        mAdapter = new CartAdapter(goodsList, getActivity());
        receiver = new UpdateCartReceiver();
        IntentFilter filter = new IntentFilter("updateCart");
        getActivity().registerReceiver(receiver, filter);
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


    @Override
    public void onResume() {
        super.onResume();
        if (FuLiCenterApplication.userAvatar != null) {
            downloadCart();
            if (goodsList == null || goodsList.size() == 0) {
                ivCartNone.setVisibility(View.VISIBLE);
            } else {
                ivCartNone.setVisibility(View.GONE);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    class UpdateCartReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int total = intent.getIntExtra("total", 0);
            int spare = intent.getIntExtra("spare", 0);
            tvTotal.setText("合计:" + total);
            tvSpare.setText("节省:" + spare);
            int count = intent.getIntExtra("count", 0);
            getActivity().sendBroadcast(new Intent("countCart").putExtra("count",count));
        }
    }
}
