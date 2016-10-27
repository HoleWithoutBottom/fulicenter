package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.GoodsDetailBean;
import cn.ucai.fulicenter.utils.ImageLoader;

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
        CartBean cartBean = mList.get(position);
        GoodsDetailBean goodsDetailBean = (GoodsDetailBean) cartBean.getGoods();
        holder.tvCartPrice.setText(goodsDetailBean.getCurrencyPrice());
        holder.tvCartGoodsName.setText(goodsDetailBean.getGoodsName());
        holder.tvCartCount.setText("(" + 1 + ")");
        ImageLoader.downloadImg(context, holder.ivCart, goodsDetailBean.getGoodsThumb());

        // 增加商品数量
        holder.ivAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String goodsCount = holder.tvCartCount.getText().toString();
                String substring = goodsCount.substring(1, goodsCount.length() - 1);
                int count = Integer.parseInt(substring) + 1;
                holder.tvCartCount.setText("(" + count + ")");
            }
        });
        // 减少商品数量
        holder.ivCartDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String goodsCount = holder.tvCartCount.getText().toString();
                String substring = goodsCount.substring(1, goodsCount.length() - 1);
                int count = Integer.parseInt(substring) - 1;
                if (count == 0) {
                    decCount(position);
                    return;
                }
                holder.tvCartCount.setText("(" + count + ")");
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }


    public void decCount(int position) {
        mList.remove(position);
        notifyDataSetChanged();
    }

    public void initList(ArrayList<CartBean> list) {
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
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