package cn.ucai.fulicenter.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.CollectsActivity;
import cn.ucai.fulicenter.activity.SettingsActivity;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.bean.Result;
import cn.ucai.fulicenter.bean.UserAvatar;
import cn.ucai.fulicenter.dao.UserDao;
import cn.ucai.fulicenter.utils.CommonUtils;
import cn.ucai.fulicenter.utils.ImageLoader;
import cn.ucai.fulicenter.utils.MFGT;
import cn.ucai.fulicenter.utils.OkHttpUtils;
import cn.ucai.fulicenter.utils.ResultUtils;


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
    @Bind(R.id.rl_personal_collects)
    RelativeLayout rlPersonalCollects;
    @Bind(R.id.rl_personal_stores)
    RelativeLayout rlPersonalStores;
    @Bind(R.id.rl_personal_steps)
    RelativeLayout rlPersonalSteps;
    int count = 0;

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
            syncCollections();
            tvPersonalUserName.setText(userAvatar.getMuserNick());
            ImageLoader.setAvatar(ImageLoader.getUrl(userAvatar), getActivity(), ivPersonalAvatar);
        } else {
            tvPersonalUserName.setText("nick");
            ImageLoader.setAvatar(ImageLoader.getUrl(userAvatar), getActivity(), ivPersonalAvatar);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (FuLiCenterApplication.userAvatar != null) {
            syncUserInfo();
        }
        if (FuLiCenterApplication.userAvatar != null) {
            initData();
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

    //用户更新
    public void syncUserInfo() {
        OkHttpUtils<String> utils = new OkHttpUtils<>(getActivity());
        utils.setRequestUrl(I.REQUEST_FIND_USER)
                .addParam(I.User.USER_NAME, userAvatar.getMuserName())
                .targetClass(String.class)
                .execute(new OkHttpUtils.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Result result = ResultUtils.getResultFromJson(s, UserAvatar.class);
                        if (result != null) {
                            UserAvatar user = (UserAvatar) result.getRetData();
                            boolean b = userAvatar.equals(user);
                            if (!b) {
                                userAvatar = user;
                                FuLiCenterApplication.setUserAvatar(user);
                                UserDao dao = new UserDao(getActivity());
                                dao.updateUser(user);/*
                                tvPersonalUserName.setText(user.getMuserNick());
                                ImageLoader.setAvatar(ImageLoader.getUrl(user), getActivity(), ivPersonalAvatar);*/
                            }
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
    }

    @OnClick({R.id.rl_personal_collects, R.id.rl_personal_stores, R.id.rl_personal_steps})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_personal_collects:
                if (count != 0) {
                    Intent intent = new Intent(getActivity(), CollectsActivity.class);
                    MFGT.startActivity(getActivity(), intent);
                } else {
                    CommonUtils.showShortToast("你还没有收藏宝贝");
                }
                break;
            case R.id.rl_personal_stores:
                break;
            case R.id.rl_personal_steps:
                break;
        }
    }

    public void syncCollections() {
        OkHttpUtils<MessageBean> utils = new OkHttpUtils<>(getActivity());
        utils.setRequestUrl(I.REQUEST_FIND_COLLECT_COUNT)
                .addParam(I.Collect.USER_NAME, userAvatar.getMuserName())
                .targetClass(MessageBean.class)
                .execute(new OkHttpUtils.OnCompleteListener<MessageBean>() {
                    @Override
                    public void onSuccess(MessageBean result) {
                        if (result != null) {
                            boolean success = result.isSuccess();
                            if (success) {
                                count = Integer.parseInt(result.getMsg());
                                tvPersonalCountOfCollections.setText(count + "");
                            } else {
                                count = 0;
                                tvPersonalCountOfCollections.setText(count + "");
                            }
                        }
                    }

                    @Override
                    public void onError(String error) {
                        CommonUtils.showShortToast(error);
                    }
                });
    }
}
