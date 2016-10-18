package cn.ucai.fulicenter.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.BoutiqueAdapter;
import cn.ucai.fulicenter.bean.BoutiqueBean;
import cn.ucai.fulicenter.utils.L;
import cn.ucai.fulicenter.utils.OkHttpUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class BoutiqueFragment extends Fragment {
    ArrayList<BoutiqueBean> mBoutiqueList;
    BoutiqueAdapter mAdapter;
    @Bind(R.id.tv_boutique_refresh)
    TextView tvBoutiqueRefresh;
    @Bind(R.id.recyclerBoutique)
    RecyclerView recyclerBoutique;
    @Bind(R.id.swipe_Boutiqu)
    SwipeRefreshLayout swipeBoutique;
    LinearLayoutManager linearLayoutManger;

    public BoutiqueFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_boutique, container, false);
        ButterKnife.bind(this, view);
        initData();
        downloadBoutiqueList(I.ACTION_DOWNLOAD);
        setListener();
        return view;
    }

    private void setListener() {
        swipeBoutique.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeBoutique.setRefreshing(true);
                swipeBoutique.setEnabled(true);
                tvBoutiqueRefresh.setVisibility(View.VISIBLE);
                downloadBoutiqueList(I.ACTION_PULL_DOWN);
            }
        });
    }

    private void downloadBoutiqueList(final int action) {
        final OkHttpUtils<BoutiqueBean[]> utils = new OkHttpUtils<>(getActivity());
        utils.setRequestUrl(I.REQUEST_FIND_BOUTIQUES)
                .targetClass(BoutiqueBean[].class)
                .execute(new OkHttpUtils.OnCompleteListener<BoutiqueBean[]>() {
                    @Override
                    public void onSuccess(BoutiqueBean[] result) {
                        if (result != null && result.length > 0) {
                            ArrayList<BoutiqueBean> boutiqueList = utils.array2List(result);
                            mAdapter.setFootText("没有数据可加载");
                            switch (action){
                                case I.ACTION_PULL_DOWN:
                                    tvBoutiqueRefresh.setVisibility(View.GONE);
                                    swipeBoutique.setRefreshing(false);
                                    break;
                            }
                            mAdapter.initBoutiqueList(boutiqueList);
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });

    }

    private void initData() {
        mBoutiqueList = new ArrayList<>();
        mAdapter = new BoutiqueAdapter(getActivity(), mBoutiqueList);
        recyclerBoutique.setAdapter(mAdapter);
        linearLayoutManger = new LinearLayoutManager(getActivity());
        recyclerBoutique.setLayoutManager(linearLayoutManger);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
