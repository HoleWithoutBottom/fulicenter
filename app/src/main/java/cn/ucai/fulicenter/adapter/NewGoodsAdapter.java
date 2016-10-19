package cn.ucai.fulicenter.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.MainActivity;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.GoodsDetailActivity;
import cn.ucai.fulicenter.activity.HomeActivity;
import cn.ucai.fulicenter.bean.NewGoodsBean;
import cn.ucai.fulicenter.utils.ImageLoader;
import cn.ucai.fulicenter.utils.MFGT;

/**
 * Created by Administrator on 2016/10/17.
 */


public class NewGoodsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<NewGoodsBean> mlist;
    Context context;
    RecyclerView parent;
    String footText;
    boolean isMore;
    int mNewState;

    public void setmNewState(int mNewState) {
        this.mNewState = mNewState;
    }

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
            final NewGoodsViewHolder newGoodsHolder = (NewGoodsViewHolder) holder;
            final NewGoodsBean goodsBean = mlist.get(position);
            newGoodsHolder.tvName.setText(goodsBean.getGoodsName());
            newGoodsHolder.tvPrice.setText(goodsBean.getPromotePrice());
            String url = I.SERVER_ROOT + I.REQUEST_DOWNLOAD_IMAGE;
            ImageLoader.build(url)
                    .addParam(I.IMAGE_URL, goodsBean.getGoodsThumb())
                    .listener(parent)
                    .defaultPicture(R.mipmap.defaultload)
                    .imageView(newGoodsHolder.ivNewGoods)
                    .setDragging(mNewState != RecyclerView.SCROLL_STATE_DRAGGING)
                    .showImage(context);
            newGoodsHolder.reNewGoods.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int goodsId = goodsBean.getGoodsId();
                    Intent intent = new Intent(context, GoodsDetailActivity.class);
                    intent.putExtra("goodsId", goodsId);
                    MFGT.startActivity(context, intent);
                }
            });
        }
    }

    public void initGoodsList(ArrayList<NewGoodsBean> goodslist) {
        mlist.clear();
        mlist.addAll(goodslist);
        notifyDataSetChanged();
    }

    public void addGoodsList(ArrayList<NewGoodsBean> goodslist) {
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


class NewGoodsViewHolder extends RecyclerView.ViewHolder {
    ImageView ivNewGoods;
    TextView tvName, tvPrice;
    RelativeLayout reNewGoods;

    public NewGoodsViewHolder(View itemView) {
        super(itemView);
        tvName = (TextView) itemView.findViewById(R.id.tv_newGoods_name);
        tvPrice = (TextView) itemView.findViewById(R.id.tv_newGoods_price);
        ivNewGoods = (ImageView) itemView.findViewById(R.id.iv_newGoods);
        reNewGoods = (RelativeLayout) itemView.findViewById(R.id.newGoods_relative);
    }
}

class FootHolder extends RecyclerView.ViewHolder {
    TextView tvFoot;

    public FootHolder(View itemView) {
        super(itemView);
        tvFoot = (TextView) itemView.findViewById(R.id.tvFoot);
    }
}
