package cn.ucai.fulicenter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.pingplusplus.android.PingppLog;
import com.pingplusplus.libone.PaymentHandler;
import com.pingplusplus.libone.PingppOne;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.GoodsDetailBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.bean.UserAvatar;
import cn.ucai.fulicenter.utils.CommonUtils;
import cn.ucai.fulicenter.utils.L;
import cn.ucai.fulicenter.utils.MFGT;
import cn.ucai.fulicenter.utils.OkHttpUtils;

public class OrderActivity extends AppCompatActivity implements PaymentHandler {

    @Bind(R.id.iv_order_back)
    ImageView ivOrderBack;
    @Bind(R.id.et_order_receiver)
    EditText etOrderReceiver;
    @Bind(R.id.et_order_phoneNumber)
    EditText etOrderPhoneNumber;
    @Bind(R.id.spinner_order_address)
    Spinner spinnerOrderAddress;
    @Bind(R.id.et_street_address)
    EditText etStreetAddress;
    @Bind(R.id.btn_order_confirm)
    Button btnOrderConfirm;
    UserAvatar user;
    ArrayList<CartBean> cartList;
    int total;
    @Bind(R.id.tv_order_total)
    TextView tvOrderTotal;
    private static String URL = "http://218.244.151.190/demo/charge";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        //设置需要使用的支付方式
        PingppOne.enableChannels(new String[]{"wx", "alipay", "upacp", "bfb", "jdpay_wap"});
        // 提交数据的格式，默认格式为json
        // PingppOne.CONTENT_TYPE = "application/x-www-form-urlencoded";
        PingppOne.CONTENT_TYPE = "application/json";
        PingppLog.DEBUG = true;

        cartList = new ArrayList<>();
        user = FuLiCenterApplication.userAvatar;
        downloadCarts();

    }

    private void sumPrice() {
        if (cartList != null && cartList.size() > 0) {
            total = 0;
            for (CartBean cart : cartList) {
                if (cart.isChecked()) {
                    int count = cart.getCount();
                    GoodsDetailBean goods = (GoodsDetailBean) cart.getGoods();
                    int price = Integer.parseInt(goods.getCurrencyPrice().substring(1));
                    total += count * price;
                }
            }
        }
        tvOrderTotal.setText("合计：￥" + total);
    }

    private void downloadCarts() {
        final OkHttpUtils<CartBean[]> utils = new OkHttpUtils<CartBean[]>(OrderActivity.this);
        utils.setRequestUrl(I.REQUEST_FIND_CARTS)
                .addParam(I.Cart.USER_NAME, FuLiCenterApplication.userAvatar.getMuserName())
                .targetClass(CartBean[].class)
                .execute(new OkHttpUtils.OnCompleteListener<CartBean[]>() {
                    @Override
                    public void onSuccess(CartBean[] result) {
                        if (result != null) {
                            ArrayList<CartBean> list = utils.array2List(result);
                            cartList = list;
                            sumPrice();
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
    }


    @OnClick({R.id.iv_order_back, R.id.btn_order_confirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_order_back:
                MFGT.finish(OrderActivity.this);
                break;
            case R.id.btn_order_confirm:
                if (total <= 0) {
                    CommonUtils.showShortToast("您还没有选择商品");
                    return;
                }
                String receiver = etOrderReceiver.getText().toString().trim();
                String phone = etOrderPhoneNumber.getText().toString().trim();
                String city = spinnerOrderAddress.getSelectedItem().toString().trim();
                String street = etStreetAddress.getText().toString().trim();
                if (receiver == null || receiver.length() == 0) {
                    etOrderReceiver.setError("请填写收货人");
                    etOrderReceiver.requestFocus();
                    return;
                }
                if (phone == null || phone.length() == 0) {
                    etOrderPhoneNumber.setError("请填写手机号码");
                    etOrderPhoneNumber.requestFocus();
                    return;
                }
                if (!phone.matches("[\\d]{11}")) {
                    etOrderPhoneNumber.setError("手机号码格式错误");
                    etOrderPhoneNumber.requestFocus();
                    return;
                }
                if (street == null || street.length() == 0) {
                    etStreetAddress.setError("请填写街道地址");
                    etStreetAddress.requestFocus();
                }
                // 产生个订单号
                String orderNo = new SimpleDateFormat("yyyyMMddhhmmss")
                        .format(new Date());
                // 构建账单json对象
                JSONObject bill = new JSONObject();

                // 自定义的额外信息 选填
                JSONObject extras = new JSONObject();
                try {
                    extras.put("extra1", "extra1");
                    extras.put("extra2", "extra2");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    bill.put("order_no", orderNo);
                    bill.put("amount", total * 100);
                    bill.put("extras", extras);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //壹收款: 创建支付通道的对话框
                PingppOne.showPaymentChannels(getSupportFragmentManager(), bill.toString(), URL, this);

        }
    }

    public void deleteCart() {
        OkHttpUtils<MessageBean> utils = new OkHttpUtils<>(OrderActivity.this);
        for (int i = 0; i < cartList.size(); i++) {
            if (cartList.get(i).isChecked()) {
                int cartId = cartList.get(i).getId();
                utils.setRequestUrl(I.REQUEST_DELETE_CART)
                        .addParam(I.Cart.ID, cartId + "")
                        .targetClass(MessageBean.class)
                        .execute(new OkHttpUtils.OnCompleteListener<MessageBean>() {
                            @Override
                            public void onSuccess(MessageBean result) {
                                if (result == null || !result.isSuccess()) {
                                    CommonUtils.showShortToast("从购物车删除失败");
                                }
                            }

                            @Override
                            public void onError(String error) {

                            }
                        });
            }
        }
    }

    @Override
    public void handlePaymentResult(Intent data) {
        if (data != null) {

            // result：支付结果信息
            // code：支付结果码
            //-2:用户自定义错误
            //-1：失败
            // 0：取消
            // 1：成功
            // 2:应用内快捷支付支付结果

            if (data.getExtras().getInt("code") == 1) {
               deleteCart();
                MFGT.finish(OrderActivity.this);
            } else {
                String result = data.getStringExtra("result");
                try {
                    JSONObject resultJson = new JSONObject(result);
                    if (resultJson.has("error")) {
                        result = resultJson.optJSONObject("error").toString();
                    } else if (resultJson.has("success")) {
                        result = resultJson.optJSONObject("success").toString();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
