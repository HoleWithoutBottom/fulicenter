package cn.ucai.fulicenter.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.ucai.fulicenter.MainActivity;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.RegisterActivity;
import cn.ucai.fulicenter.utils.MFGT;


public class PersonalFragment extends Fragment {

    Boolean flag=false;
    public PersonalFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_personal, container, false);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!flag) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            MFGT.startActivity(getActivity(), intent);
        }
    }
}
