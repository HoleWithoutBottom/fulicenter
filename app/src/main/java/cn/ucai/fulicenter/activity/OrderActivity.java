package cn.ucai.fulicenter.activity;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

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

public class OrderActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
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
                if (true) {
                    // 删除结算的商品

                } else {

                }
                break;
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
                                if (result != null && result.isSuccess()) {
                                }else {
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
}
