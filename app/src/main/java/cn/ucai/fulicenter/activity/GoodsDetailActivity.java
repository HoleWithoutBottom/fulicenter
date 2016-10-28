package cn.ucai.fulicenter.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.AlbumsBean;
import cn.ucai.fulicenter.bean.CollectBean;
import cn.ucai.fulicenter.bean.GoodsDetailBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.bean.PropertiesBean;
import cn.ucai.fulicenter.utils.CommonUtils;

import cn.ucai.fulicenter.utils.L;
import cn.ucai.fulicenter.utils.MFGT;
import cn.ucai.fulicenter.utils.OkHttpUtils;
import cn.ucai.fulicenter.views.FlowIndicator;

public class GoodsDetailActivity extends AppCompatActivity {

    @Bind(R.id.iv_goodsDetail_back)
    ImageView ivGoodsDetailBack;
    @Bind(R.id.iv_goodsDetail_cart)
    RadioButton ivGoodsDetailCart;
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
    Handler mHandler;
    AlbumsBean[] albums;
    ArrayList<ImageView> imageViews;
    PictureAdapter myAdapter;
    int mCount = 0;
    GoodsDetailBean goodsDetailBean;
    Boolean isChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_detail);
        ButterKnife.bind(this);
        initData();
        setListener();
        initHandler();

    }

    private void setThread() {
        new Thread() {
            @Override
            public void run() {
                for (int i = 0; i <= mCount - 1; i++) {
                    SystemClock.sleep(1500);
                    Message message = Message.obtain();
                    message.arg1 = i;
                    mHandler.sendMessage(message);
                    if (i == mCount - 1) {
                        i = -1;
                    }
                }
            }
        }.start();
    }


    private void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                int i = msg.arg1;
                mViewPager.setCurrentItem(i);
            }
        };
    }

    private void setListener() {
        // 图片切换
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
        if (goodsDetailBean != null && FuLiCenterApplication.userAvatar != null) {
            syncIsCollect(goodsDetailBean);
        }
    }

    private void syncIsCollect(final GoodsDetailBean goodsDetailBean) {
        OkHttpUtils<MessageBean> utils = new OkHttpUtils<>(GoodsDetailActivity.this);
        utils.setRequestUrl(I.REQUEST_IS_COLLECT)
                .addParam(I.Goods.KEY_GOODS_ID, goodsDetailBean.getGoodsId() + "")
                .addParam(I.Collect.USER_NAME, FuLiCenterApplication.userAvatar.getMuserName())
                .targetClass(MessageBean.class)
                .execute(new OkHttpUtils.OnCompleteListener<MessageBean>() {
                    @Override
                    public void onSuccess(MessageBean result) {
                        if (result != null) {
                            if (result.isSuccess()) {
                                ivGoodsDetailCollect.setImageResource(R.mipmap.bg_collect_out);
                                isChecked = true;
                            } else {
                                ivGoodsDetailCollect.setImageResource(R.mipmap.bg_collect_in);
                                isChecked = false;
                            }
                        }
                    }

                    @Override
                    public void onError(String error) {
                        CommonUtils.showShortToast(error);
                    }
                });
    }

    // 下载商品详情
    private void downloadGoodsDetail(final int id) {
        OkHttpUtils<GoodsDetailBean> utils = new OkHttpUtils<>(this);
        utils.setRequestUrl(I.REQUEST_FIND_GOOD_DETAILS)
                .addParam(I.Goods.KEY_GOODS_ID, id + "")
                .targetClass(GoodsDetailBean.class)
                .execute(new OkHttpUtils.OnCompleteListener<GoodsDetailBean>() {
                    @Override
                    public void onSuccess(GoodsDetailBean result) {
                        if (result != null) {
                            goodsDetailBean = result;
                            L.e(result.toString());
                            if (goodsDetailBean != null && FuLiCenterApplication.userAvatar != null) {
                                syncIsCollect(goodsDetailBean);
                            }
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
                        CommonUtils.showShortToast(error);
                    }
                });
    }

    // 下载图片
    private void downloadPicture() {
        goodsDetailFlowIndicator.setCount(albums.length);
        mCount = albums.length;
        for (int i = 0; i < albums.length; i++) {
            ImageView iv = new ImageView(this);
            Picasso.with(this)
                    .load(I.DOWNLOAD_IMG_URL + albums[i].getImgUrl())
                    .placeholder(R.drawable.nopic)
                    .into(iv);
            imageViews.add(iv);
        }
        myAdapter = new PictureAdapter(imageViews);
        mViewPager.setAdapter(myAdapter);
        setThread();
    }

    @OnClick({R.id.iv_goodsDetail_back, R.id.iv_goodsDetail_cart, R.id.iv_goodsDetail_collect, R.id.iv_goodsDetail_share})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_goodsDetail_back:
                MFGT.finish(GoodsDetailActivity.this);
                break;
            case R.id.iv_goodsDetail_cart:
                if (FuLiCenterApplication.userAvatar != null) {

                    addCart(goodsDetailBean);
                }
                // L.e("sb"+FuLiCenterApplication.goodsDetailBean.toString());
                break;
            case R.id.iv_goodsDetail_collect:
                if (isChecked) {
                    deleteCollect(goodsDetailBean.getGoodsId(), GoodsDetailActivity.this);
                    syncIsCollect(goodsDetailBean);
                } else {
                    if (FuLiCenterApplication.userAvatar != null) {
                        addCollect(goodsDetailBean);
                    }
                }
                break;
            case R.id.iv_goodsDetail_share:
                showShare();
                break;
        }
    }

    public void addCart(final GoodsDetailBean goodsBean) {
        OkHttpUtils<MessageBean> utils = new OkHttpUtils<>(GoodsDetailActivity.this);
        utils.setRequestUrl(I.REQUEST_ADD_CART)
                .addParam(I.Cart.GOODS_ID, goodsDetailBean.getGoodsId() + "")
                .addParam(I.Cart.USER_NAME, FuLiCenterApplication.userAvatar.getMuserName())
                .addParam(I.Cart.COUNT, 1 + "")
                .addParam(I.Cart.IS_CHECKED, isChecked + "")
                .targetClass(MessageBean.class)
                .execute(new OkHttpUtils.OnCompleteListener<MessageBean>() {
                    @Override
                    public void onSuccess(MessageBean result) {
                        if (result != null && result.isSuccess()) {
                            CommonUtils.showShortToast("已添加到购物车");
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
    }

    // 添加收藏
    private void addCollect(final GoodsDetailBean goodsDetailBean) {
        OkHttpUtils<MessageBean> utils = new OkHttpUtils<>(GoodsDetailActivity.this);
        utils.setRequestUrl(I.REQUEST_ADD_COLLECT)
                .addParam(I.Collect.GOODS_ID, goodsDetailBean.getGoodsId() + "")
                .addParam(I.Collect.USER_NAME, FuLiCenterApplication.userAvatar.getMuserName())
                .targetClass(MessageBean.class)
                .execute(new OkHttpUtils.OnCompleteListener<MessageBean>() {
                    @Override
                    public void onSuccess(MessageBean result) {
                        if (result != null && result.isSuccess()) {
                            CommonUtils.showShortToast(result.getMsg());
                            syncIsCollect(goodsDetailBean);
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
    }

    // 删除收藏
    public void deleteCollect(int goodsId, Context context) {
        OkHttpUtils<MessageBean> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_DELETE_COLLECT)
                .addParam(I.Collect.GOODS_ID, goodsId + "")
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

    @Override
    protected void onResume() {
        super.onResume();
        if (goodsDetailBean != null && FuLiCenterApplication.userAvatar != null) {
            syncIsCollect(goodsDetailBean);
        }
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

    public void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("标题");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
        oks.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite("ShareSDK");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");
        // 启动分享GUI
        oks.show(this);
    }
}
