package cn.ucai.fulicenter.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.SettingsActivity;
import cn.ucai.fulicenter.bean.UserAvatar;
import cn.ucai.fulicenter.utils.ImageLoader;
import cn.ucai.fulicenter.utils.MFGT;


public class PersonalFragment extends Fragment {


    UserAvatar userAvatar;
    @Bind(R.id.iv_personal_message)
    ImageView ivPersonalMessage;
    @Bind(R.id.tv_personal_settings)
    TextView tvPersonalSettings;
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
    @Bind(R.id.iv_personal_toPay)
    ImageView ivPersonalToPay;
    @Bind(R.id.iv_personal_toSend)
    ImageView ivPersonalToSend;
    @Bind(R.id.iv_personal_toReceive)
    ImageView ivPersonalToReceive;
    @Bind(R.id.iv_personal_toEvaluate)
    ImageView ivPersonalToEvaluate;
    @Bind(R.id.iv_personal_refundAndAfterSales)
    ImageView ivPersonalRefundAndAfterSales;
    @Bind(R.id.tv_personal_myCardBag)
    TextView tvPersonalMyCardBag;

    public PersonalFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_personal, container, false);
        ButterKnife.bind(this, view);
        initData();
        return view;
    }

    private void initData() {
        userAvatar = FuLiCenterApplication.userAvatar;
        if (userAvatar != null) {
            tvPersonalUserName.setText(userAvatar.getMuserNick());
            ImageLoader.setAvatar(ImageLoader.getUrl(userAvatar), getActivity(), ivPersonalAvatar);
        }
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

    @OnClick(R.id.tv_personal_settings)
    public void onClick() {
        Intent intent = new Intent(getActivity(), SettingsActivity.class);
        MFGT.startActivity(getActivity(), intent);
    }

}
