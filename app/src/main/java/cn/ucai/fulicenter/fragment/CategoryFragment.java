package cn.ucai.fulicenter.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.CategoryActivity;
import cn.ucai.fulicenter.activity.GoodsDetailActivity;
import cn.ucai.fulicenter.bean.CategoryChildBean;
import cn.ucai.fulicenter.bean.CategoryGroupBean;
import cn.ucai.fulicenter.utils.CommonUtils;
import cn.ucai.fulicenter.utils.ImageLoader;
import cn.ucai.fulicenter.utils.L;
import cn.ucai.fulicenter.utils.MFGT;
import cn.ucai.fulicenter.utils.OkHttpUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends Fragment {

    ArrayList<CategoryGroupBean> mGroupList;
    ArrayList<ArrayList<CategoryChildBean>> mChildList;
    CategoryAdapter mAdapter;
    View view;
    @Bind(R.id.category_elv)
    ExpandableListView categoryElv;

    public CategoryFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_category, container, false);
        ButterKnife.bind(this, view);
        initData();
        setListener();
        return view;

    }

    private void setListener() {

        categoryElv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                CategoryChildBean child = mAdapter.getChild(i, i1);
                ArrayList<CategoryChildBean> childs = mAdapter.childList.get(i);
                Intent intent = new Intent(getActivity(), CategoryActivity.class);
                intent.putExtra("goodsId", child.getId());
                intent.putExtra("name", mAdapter.getGroup(i).getName());
                intent.putExtra("childList", childs);
                MFGT.startActivity(getActivity(), intent);
                return false;
            }
        });
        /*categoryElv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });*/
    }


    private void initData() {
        mGroupList = new ArrayList<>();
        mChildList = new ArrayList<>();
        downloadGroupList();
        // downloadChildList(mGroupList);
        // L.e(mGroupList.toString());
        // mAdapter = new CategoryAdapter(getActivity(), mGroupList, mChildList); 此时new Adapter mGroupList和 mChildList还是空的
        // categoryElv.setAdapter(mAdapter);
    }

    private void downloadChildList(int parentId, final int index) {
        final OkHttpUtils<CategoryChildBean[]> utils = new OkHttpUtils<>(getActivity());
        // SystemClock.sleep(100);
        utils.setRequestUrl(I.REQUEST_FIND_CATEGORY_CHILDREN)
                .addParam(I.CategoryChild.PARENT_ID, parentId + "")
                .addParam(I.PAGE_ID, I.PAGE_ID_DEFAULT + "")
                .addParam(I.PAGE_SIZE, 20 + "")
                .targetClass(CategoryChildBean[].class)
                .execute(new OkHttpUtils.OnCompleteListener<CategoryChildBean[]>() {
                    @Override
                    public void onSuccess(CategoryChildBean[] result) {
                        if (result != null && result.length > 0) {
                            mChildList.set(index, utils.array2List(result));
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });

        mAdapter = new CategoryAdapter(getActivity(), mGroupList, mChildList);
        categoryElv.setAdapter(mAdapter);
    }

    public void downloadGroupList() {
        final OkHttpUtils<CategoryGroupBean[]> utils = new OkHttpUtils<>(getActivity());
        utils.setRequestUrl(I.REQUEST_FIND_CATEGORY_GROUP)
                .targetClass(CategoryGroupBean[].class)
                .execute(new OkHttpUtils.OnCompleteListener<CategoryGroupBean[]>() {
                    @Override
                    public void onSuccess(CategoryGroupBean[] result) {
                        if (result != null && result.length > 0) {
                            ArrayList<CategoryGroupBean> categoryList = utils.array2List(result);
                            mGroupList = categoryList;
                            for (int i = 0; i < mGroupList.size(); i++) {
                                CategoryGroupBean bean = mGroupList.get(i);
                                mChildList.add(new ArrayList<CategoryChildBean>());
                                downloadChildList(bean.getId(), i);
                            }
                        }
                    }

                    @Override
                    public void onError(String error) {
                        CommonUtils.showShortToast(error);
                    }
                });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    class CategoryAdapter extends BaseExpandableListAdapter {
        Context context;
        ArrayList<CategoryGroupBean> groupList;
        ArrayList<ArrayList<CategoryChildBean>> childList;


        public CategoryAdapter(Context context, ArrayList<CategoryGroupBean> groupList, ArrayList<ArrayList<CategoryChildBean>> childList) {
            this.context = context;
            this.groupList = groupList;
            this.childList = childList;
        }

        @Override
        public int getGroupCount() {
            return groupList != null ? groupList.size() : 0;
        }

        @Override
        public int getChildrenCount(int i) {
            return childList != null && childList.get(i) != null ? childList.get(i).size() : 0;

        }

        @Override
        public CategoryGroupBean getGroup(int i) {
            return groupList != null ? groupList.get(i) : null;
        }

        @Override
        public CategoryChildBean getChild(int i, int i1) {
            return childList != null && childList.get(i) != null ? childList.get(i).get(i1) : null;
        }

        @Override
        public long getGroupId(int i) {
            return i;
        }

        @Override
        public long getChildId(int i, int i1) {
            return i1;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        class GroupViewHolder {
            ImageView ivGroup, ivExpand;
            TextView tvGroupName;
        }

        class ChildViewHolder {
            ImageView ivChild;
            TextView tvChildName;
            LinearLayout lineLayout;
        }

        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
            GroupViewHolder holder = null;
            if (view == null) {
                view = View.inflate(context, R.layout.category_group, null);
                holder = new GroupViewHolder();
                holder.ivGroup = (ImageView) view.findViewById(R.id.iv_category_group);
                holder.tvGroupName = (TextView) view.findViewById(R.id.tv_category_group_name);
                holder.ivExpand = (ImageView) view.findViewById(R.id.iv_category_expand);
                view.setTag(holder);
            } else {
                holder = (GroupViewHolder) view.getTag();
            }
            CategoryGroupBean group = getGroup(i);
            if (group != null) {
                ImageLoader.downloadImg(context, holder.ivGroup, group.getImageUrl());
                holder.tvGroupName.setText(group.getName());
                if (b) {
                    holder.ivExpand.setImageResource(R.mipmap.expand_off);
                } else {
                    holder.ivExpand.setImageResource(R.mipmap.expand_on);
                }
            }
            return view;
        }

        @Override
        public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
            ChildViewHolder holder = null;
            if (view == null) {
                view = View.inflate(context, R.layout.category_child, null);
                holder = new ChildViewHolder();
                holder.ivChild = (ImageView) view.findViewById(R.id.iv_category_child);
                holder.tvChildName = (TextView) view.findViewById(R.id.tv_category_child_name);
                holder.lineLayout = (LinearLayout) view.findViewById(R.id.line_category_child);
                view.setTag(holder);
            } else {
                holder = (ChildViewHolder) view.getTag();
            }
            CategoryChildBean child = getChild(i, i1);
            if (child != null) {
                holder.tvChildName.setText(child.getName());
                ImageLoader.downloadImg(context, holder.ivChild, child.getImageUrl());
            }
            return view;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }
    }
}
