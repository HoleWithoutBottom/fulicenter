package cn.ucai.fulicenter.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
    @Bind(R.id.goodsDetail_viewpager)
    ViewPager mViewPager;
    @Bind(R.id.goodsDetail_flowIndicator)
    FlowIndicator goodsDetailFlowIndicator;
    @Bind(R.id.tv_goodsDetails_Brief)
    TextView tvGoodsDetailsBrief;
    int mFocus = -1;
    int mCount;
    AlbumsBean[] albums;
    ArrayList<ImageView> imageViews;
    PictureAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_detail);
        ButterKnife.bind(this);
        initData();
        setListener();
    }

    private void setListener() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                goodsDetailFlowIndicator.setFocus(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initData() {
        int id = getIntent().getIntExtra("goodsId", 0);
        downloadGoodsDetail(id);
        imageViews = new ArrayList<>();
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
                            albums = properties[0].getAlbums();
                            downloadPicture();
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
    }

    private void downloadPicture() {
        goodsDetailFlowIndicator.setCount(albums.length);
        mCount = albums.length;
        for (int i = 0; i < albums.length; i++) {
            ImageView iv = new ImageView(this);
            Picasso.with(this)
                    .load(I.DOWNLOAD_IMG_URL+albums[i].getImgUrl())
                    .placeholder(R.drawable.nopic)
                    .into(iv);
            imageViews.add(iv);
        }
        myAdapter = new PictureAdapter(imageViews);
        mViewPager.setAdapter(myAdapter);
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


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    class PictureAdapter extends PagerAdapter {
        ArrayList<ImageView> list;

        public PictureAdapter(ArrayList<ImageView> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return object == view;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(list.get(position));
            return list.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(list.get(position));
        }
    }
}
