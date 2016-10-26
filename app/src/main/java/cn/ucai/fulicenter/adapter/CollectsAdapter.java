package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.GoodsDetailActivity;
import cn.ucai.fulicenter.bean.CollectBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.utils.CommonUtils;
import cn.ucai.fulicenter.utils.ImageLoader;
import cn.ucai.fulicenter.utils.MFGT;
import cn.ucai.fulicenter.utils.OkHttpUtils;

/**
 * Created by Administrator on 2016/10/26.
 */
public class CollectsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<CollectBean> mlist;
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

    public CollectsAdapter(ArrayList<CollectBean> mlist, Context context) {
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
                layout = View.inflate(context, R.layout.item_collections, null);
                holder = new CollectsViewHolder(layout);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (position == getItemCount() - 1) {
            FootHolder footHolder = (FootHolder) holder;
            footHolder.tvFoot.setText(this.footText);
            return;
        } else {
            final CollectsViewHolder collectsViewHolder = (CollectsViewHolder) holder;
            final CollectBean collectBean = mlist.get(position);
            collectsViewHolder.tvName.setText(collectBean.getGoodsName());
            String url = I.SERVER_ROOT + I.REQUEST_DOWNLOAD_IMAGE;
            ImageLoader.build(url)
                    .addParam(I.IMAGE_URL, collectBean.getGoodsThumb())
                    .listener(parent)
                    .defaultPicture(R.mipmap.defaultload)
                    .imageView(collectsViewHolder.ivCollects)
                    //.setDragging(mNewState != RecyclerView.SCROLL_STATE_DRAGGING)
                    .showImage(context);
            collectsViewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteCollect(collectBean, context);
                    mlist.remove(position);
                    notifyDataSetChanged();
                }
            });
            collectsViewHolder.rlCollects.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int goodsId = collectBean.getGoodsId();
                    Intent intent = new Intent(context, GoodsDetailActivity.class);
                    intent.putExtra("goodsId", goodsId);
                    MFGT.startActivity(context, intent);
                }
            });
        }
    }

    public void initCollectsList(ArrayList<CollectBean> collectslist) {
        mlist.clear();
        mlist.addAll(collectslist);
        notifyDataSetChanged();
    }

    public void addCollectsList(ArrayList<CollectBean> collectslist) {
        mlist.addAll(collectslist);
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

    public void deleteCollect(CollectBean collectBean, Context context) {
        OkHttpUtils<MessageBean> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_DELETE_COLLECT)
                .addParam(I.Collect.GOODS_ID, collectBean.getGoodsId() + "")
                .addParam(I.Collect.USER_NAME, FuLiCenterApplication.userAvatar.getMuserName())
                .targetClass(MessageBean.class)
                .execute(new OkHttpUtils.OnCompleteListener<MessageBean>() {
                    @Override
                    public void onSuccess(MessageBean result) {
                        if (result != null) {
                            CommonUtils.showShortToast(result.getMsg());
                        }
                    }

                    @Override
                    public void onError(String error) {
                        CommonUtils.showShortToast(error);
                    }
                });
    }
}


class CollectsViewHolder extends RecyclerView.ViewHolder {
    ImageView ivCollects, ivDelete;
    TextView tvName;
    RelativeLayout rlCollects;

    public CollectsViewHolder(View itemView) {
        super(itemView);
        tvName = (TextView) itemView.findViewById(R.id.tv_collections_name);
        ivCollects = (ImageView) itemView.findViewById(R.id.iv_collections_picture);
        ivDelete = (ImageView) itemView.findViewById(R.id.iv_collections_delete);
        rlCollects = (RelativeLayout) itemView.findViewById(R.id.rl_collects);
    }
}


