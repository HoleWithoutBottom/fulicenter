package cn.ucai.fulicenter.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.UserAvatar;



public class PersonalFragment extends Fragment {

    UserAvatar userAvatar;
    @Bind(R.id.tv_personal_settings)
    TextView tvPersonalSettings;
    @Bind(R.id.iv_personal_message)
    ImageView ivPersonalMessage;
    @Bind(R.id.iv_personal_avatar)
    ImageView ivPersonalAvatar;
    @Bind(R.id.tv_personal_userName)
    TextView tvPersonalUserName;
    @Bind(R.id.iv_personal_qrCode)
    ImageView ivPersonalQrCode;
    @Bind(R.id.tv_personal_countOfCollections)
    TextView tvPersonalCountOfCollections;
    @Bind(R.id.tv_personal_countOfShops)
    TextView tvPersonalCountOfShops;
    @Bind(R.id.tv_personal_MyFoot)
    TextView tvPersonalMyFoot;
    @Bind(R.id.tv_personal_whatHaveBought)
    TextView tvPersonalWhatHaveBought;
    @Bind(R.id.rb_personal_toPay)
    RadioButton rbPersonalToPay;
    @Bind(R.id.rb_personal_toSend)
    RadioButton rbPersonalToSend;
    @Bind(R.id.rb_personal_toReceive)
    RadioButton rbPersonalToReceive;
    @Bind(R.id.rb_personal_toEvaluate)
    RadioButton rbPersonalToEvaluate;
    @Bind(R.id.rb_personal_refundAndAfterSales)
    RadioButton rbPersonalRefundAndAfterSales;
    @Bind(R.id.tv_personal_myCardBag)
    TextView tvPersonalMyCardBag;

    public PersonalFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_personal, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
