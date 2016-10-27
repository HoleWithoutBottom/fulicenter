package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.GoodsDetailBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.utils.CommonUtils;
import cn.ucai.fulicenter.utils.ImageLoader;
import cn.ucai.fulicenter.utils.L;
import cn.ucai.fulicenter.utils.OkHttpUtils;

/**
 * Created by Administrator on 2016/10/27.
 */
public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {
    ArrayList<CartBean> mList;
    Context context;

    public CartAdapter(ArrayList<CartBean> mList, Context context) {
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
    public void onBindViewHolder(final CartViewHolder holder, final int position) {
        final CartBean cartBean = mList.get(position);
        final GoodsDetailBean goodsDetailBean = (GoodsDetailBean) cartBean.getGoods();
        holder.tvCartPrice.setText(goodsDetailBean.getCurrencyPrice());
        holder.tvCartGoodsName.setText(goodsDetailBean.getGoodsName());
        holder.tvCartCount.setText("(" + cartBean.getCount() + ")");
        holder.chkCart.setChecked(cartBean.isChecked());
        ImageLoader.downloadImg(context, holder.ivCart, goodsDetailBean.getGoodsThumb());
        // 增加商品数量
        holder.ivAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String goodsCount = holder.tvCartCount.getText().toString();
                String substring = goodsCount.substring(1, goodsCount.length() - 1);
                int count = Integer.parseInt(substring) + 1;
                holder.tvCartCount.setText("(" + count + ")");
                updateCart(cartBean.getId(), count, holder.chkCart.isChecked());
                mList.get(position).setCount(count);
                getTotal();
            }
        });
        // 减少商品数量
        holder.ivCartDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String goodsCount = holder.tvCartCount.getText().toString();
                String substring = goodsCount.substring(1, goodsCount.length() - 1);
                int count = Integer.parseInt(substring) - 1;
                holder.tvCartCount.setText("(" + count + ")");
                if (count == 0) {
                    deleteCart(cartBean.getId());
                    decCount(position);
                    getTotal();
                    return;
                }
                updateCart(cartBean.getId(), count, holder.chkCart.isChecked());
                mList.get(position).setCount(count);
                getTotal();
            }
        });
        // 取消选择
        holder.chkCart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                updateCart(cartBean.getId(), cartBean.getCount(), b);
                mList.get(position).setChecked(b);
                getTotal();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public void getTotal() {
        int total = 0;
        int spare = 0;
        int count = 0;
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).isChecked()) {
                GoodsDetailBean bean = (GoodsDetailBean) mList.get(i).getGoods();
                total += mList.get(i).getCount() * Integer.parseInt(bean.getCurrencyPrice().substring(1));
                spare += mList.get(i).getCount() * Integer.parseInt(bean.getRankPrice().substring(1));
                count += mList.get(i).getCount();
            }
        }
        Intent intent = new Intent("updateCart");
        intent.putExtra("total", total);
        intent.putExtra("spare", total - spare);
        intent.putExtra("count",count);
        context.sendBroadcast(intent);
    }

    public void decCount(int position) {
        mList.remove(position);
        notifyDataSetChanged();
    }

    public void initList(ArrayList<CartBean> list) {
        mList = list;
        getTotal();
        notifyDataSetChanged();
    }

    public void deleteCart(int cartId) {
        OkHttpUtils<MessageBean> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_DELETE_CART)
                .addParam(I.Cart.ID, cartId + "")
                .targetClass(MessageBean.class)
                .execute(new OkHttpUtils.OnCompleteListener<MessageBean>() {
                    @Override
                    public void onSuccess(MessageBean result) {
                        if (result != null && result.isSuccess()) {
                            CommonUtils.showShortToast("删除成功");
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
    }

    public void updateCart(int cartId, int count, boolean isChecked) {
        OkHttpUtils<MessageBean> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_UPDATE_CART)
                .addParam(I.Cart.ID, cartId + "")
                .addParam(I.Cart.COUNT, count + "")
                .addParam(I.Cart.IS_CHECKED, isChecked + "")
                .targetClass(MessageBean.class)
                .execute(new OkHttpUtils.OnCompleteListener<MessageBean>() {
                    @Override
                    public void onSuccess(MessageBean result) {
                        if (result == null || !result.isSuccess()) {
                            CommonUtils.showShortToast("修改购物车失败");
                        }
                    }

                    @Override
                    public void onError(String error) {
                        CommonUtils.showShortToast(error);
                    }
                });
    }
}

class CartViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.chk_cart)
    CheckBox chkCart;
    @Bind(R.id.iv_cart)
    ImageView ivCart;
    @Bind(R.id.tv_cart_goodsName)
    TextView tvCartGoodsName;
    @Bind(R.id.iv_add_cart)
    ImageView ivAddCart;
    @Bind(R.id.tv_cart_count)
    TextView tvCartCount;
    @Bind(R.id.iv_cart_del)
    ImageView ivCartDel;
    @Bind(R.id.tv_cart_price)
    TextView tvCartPrice;
    @Bind(R.id.linearLayout_cart)
    LinearLayout linearLayoutCart;

    CartViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);

    }
}