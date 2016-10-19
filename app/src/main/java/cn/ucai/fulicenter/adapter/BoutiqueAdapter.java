package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.BoutiqueCategoryActivity;
import cn.ucai.fulicenter.bean.BoutiqueBean;
import cn.ucai.fulicenter.utils.ImageLoader;
import cn.ucai.fulicenter.utils.MFGT;

/**
 * Created by Administrator on 2016/10/18.
 */
public class BoutiqueAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    ArrayList<BoutiqueBean> mList;
    RecyclerView parent;
    String footText;
    boolean isMore;

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean more) {
        isMore = more;
    }

    public int getmNewState() {
        return mNewState;
    }

    public void setmNewState(int mNewState) {
        this.mNewState = mNewState;
    }

    int mNewState;

    public BoutiqueAdapter(Context context, ArrayList<BoutiqueBean> mList) {
        this.context = context;
        this.mList = mList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = (RecyclerView) parent;
        RecyclerView.ViewHolder holder = null;
        View layout;
        if (viewType == I.TYPE_FOOTER) {
            layout = LayoutInflater.from(context).inflate(R.layout.item_foot, parent, false);
            holder = new FootHolder(layout);
        } else {
            layout = View.inflate(context, R.layout.boutique_item, null);
            holder = new BoutiqueHolder(layout);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == getItemCount() - 1) {
            FootHolder footHolder = (FootHolder) holder;
            footHolder.tvFoot.setText(this.footText);
            return;
        }
        BoutiqueHolder boutiqueHolder = (BoutiqueHolder) holder;
        final BoutiqueBean boutiqueBean = mList.get(position);
        boutiqueHolder.tvDesc.setText(boutiqueBean.getDescription());
        boutiqueHolder.tvName.setText(boutiqueBean.getName());
        boutiqueHolder.tvTitle.setText(boutiqueBean.getTitle());
        ImageLoader.build(I.DOWNLOAD_IMG_URL + boutiqueBean.getImageurl())
                .listener(parent)
                .defaultPicture(R.drawable.nopic)
                .imageView(boutiqueHolder.ivBoutique)
                .showImage(context);
        boutiqueHolder.linearLayoutBotique.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BoutiqueCategoryActivity.class);
                intent.putExtra("BoutiqueBeanId",boutiqueBean.getId());
                intent.putExtra("BoutiqueBeanTitle",boutiqueBean.getName());
                MFGT.startActivity(context,intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return I.TYPE_FOOTER;
        } else {
            return I.TYPE_ITEM;
        }
    }

    public void setFootText(String footText) {
        this.footText = footText;
        notifyDataSetChanged();
    }

    public void initBoutiqueList(ArrayList<BoutiqueBean> boutiqueList) {
        mList.clear();
        mList.addAll(boutiqueList);
        notifyDataSetChanged();
    }
}

class BoutiqueHolder extends RecyclerView.ViewHolder {
    ImageView ivBoutique;
    TextView tvTitle, tvDesc, tvName;
    LinearLayout linearLayoutBotique;

    public BoutiqueHolder(View itemView) {
        super(itemView);
        ivBoutique = (ImageView) itemView.findViewById(R.id.iv_boutique_picture);
        tvName = (TextView) itemView.findViewById(R.id.tv_boutique_name);
        tvDesc = (TextView) itemView.findViewById(R.id.tv_boutique_desc);
        tvTitle = (TextView) itemView.findViewById(R.id.tv_boutique_title);
        linearLayoutBotique = (LinearLayout) itemView.findViewById(R.id.linear_boutique);
    }
}
