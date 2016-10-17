package cn.ucai.fulicenter.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.AlbumsBean;
import cn.ucai.fulicenter.bean.GoodsDetailBean;
import cn.ucai.fulicenter.bean.PropertiesBean;
import cn.ucai.fulicenter.utils.ImageLoader;
import cn.ucai.fulicenter.utils.OkHttpUtils;
import cn.ucai.fulicenter.views.FlowIndicator;

public class GoodsDetailActivity extends AppCompatActivity {

    @Bind(R.id.iv_goodsDetail_back)
    ImageView ivGoodsDetailBack;
    @Bind(R.id.iv_goodsDetail_cart)
    ImageView ivGoodsDetailCart;
    @Bind(R.id.tv_goodDetail_cart_hint)
    TextView tvGoodDetailCartHint;
    @Bind(R.id.iv_goodsDetail_collect)
    ImageView ivGoodsDetailCollect;
    @Bind(R.id.iv_goodsDetail_share)
    ImageView ivGoodsDetailShare;
    @Bind(R.id.tv_goodsDetail_EnglisheName)
    TextView tvGoodsDetailEnglisheName;
    @Bind(R.id.tv_goodsDetail_Name)
    TextView tvGoodsDetailName;
    @Bind(R.id.tv_goodsDetail_price)
    TextView tvGoodsDetailPrice;
    @Bind(R.id.iv_goodsDetail_goods)
    ImageView ivGoodsDetailGoods;
    @Bind(R.id.goodsDetail_flowIndicator)
    FlowIndicator goodsDetailFlowIndicator;
    @Bind(R.id.tv_goodsDetails_Brief)
    TextView tvGoodsDetailsBrief;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_detail);
        ButterKnife.bind(this);
        int id = getIntent().getIntExtra("goodsId", 0);
        downloadGoodsDetail(id);
        setListener();
    }

    private void setListener() {
    }


    private void downloadGoodsDetail(final int id) {
        OkHttpUtils<GoodsDetailBean> utils = new OkHttpUtils<>(this);
        utils.setRequestUrl(I.REQUEST_FIND_GOOD_DETAILS)
                .addParam(I.Goods.KEY_GOODS_ID, id + "")
                .targetClass(GoodsDetailBean.class)
                .execute(new OkHttpUtils.OnCompleteListener<GoodsDetailBean>() {
                    @Override
                    public void onSuccess(GoodsDetailBean result) {
                        if (result != null) {
                            tvGoodsDetailEnglisheName.setText(result.getGoodsEnglishName());
                            tvGoodsDetailName.setText(result.getGoodsName());
                            tvGoodsDetailPrice.setText(result.getRankPrice());
                            tvGoodsDetailsBrief.setText(result.getGoodsBrief());
                            PropertiesBean[] properties = result.getProperties();
                            AlbumsBean[] albums = properties[0].getAlbums();
                            downloadPicture(albums);
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
    }

    private void downloadPicture(AlbumsBean[] albums) {

        for (int i = 0; i < albums.length; i++) {
            ImageLoader.downloadImg(this, ivGoodsDetailGoods, albums[i].getImgUrl());
        }
        goodsDetailFlowIndicator.setCount(albums.length);
    }

    @OnClick({R.id.iv_goodsDetail_back, R.id.iv_goodsDetail_cart, R.id.iv_goodsDetail_collect, R.id.iv_goodsDetail_share})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_goodsDetail_back:
                finish();
                break;
            case R.id.iv_goodsDetail_cart:
                break;
            case R.id.iv_goodsDetail_collect:
                break;
            case R.id.iv_goodsDetail_share:
                break;
        }
    }

}
