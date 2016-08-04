package cn.ucai.fulicenter.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.Utils;
import cn.ucai.fulicenter.adapter.CategroyAdapter;
import cn.ucai.fulicenter.bean.CategoryChildBean;
import cn.ucai.fulicenter.bean.CategoryGroupBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends Fragment {
    private static final String TAG = CategoryFragment.class.getSimpleName();
    Context mContext;
    ExpandableListView melvLayout;
    CategroyAdapter mAdapter;
    ArrayList<CategoryGroupBean> mGroupList;
    ArrayList<ArrayList<CategoryChildBean>> mChildList;

    public CategoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_category, container, false);
        mContext = layout.getContext();
        initView(layout);
        setListener();
        return layout;
    }

    private void setListener() {
        getGroupList();


    }


    int i=0;
    private void getGroupList() {
        final OkHttpUtils2<String> utils = new OkHttpUtils2<>();
        utils.setRequestUrl(I.REQUEST_FIND_CATEGORY_GROUP);
        utils.targetClass(String.class);
        utils.execute(new OkHttpUtils2.OnCompleteListener<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "result02=" + result);
                Gson gson = new Gson();
                final CategoryGroupBean[] array = gson.fromJson(result, CategoryGroupBean[].class);
                ArrayList<CategoryGroupBean> group = utils.array2List(array);
                mAdapter.addGroup(group);
                Log.e(TAG, "group=" + group);
                for(int i=0;i<group.size();i++) {
                    final OkHttpUtils2<String> utils2 = new OkHttpUtils2<String>();
                    utils2.setRequestUrl(I.REQUEST_FIND_CATEGORY_CHILDREN)
                            .addParam("parent_id", String.valueOf(  group.get(i).getId()))
                            .addParam(I.PAGE_ID, "1")
                            .addParam(I.PAGE_SIZE, "100")
                            .targetClass(String.class)
                            .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                                @Override
                                public void onSuccess(String result) {
                                    Gson gson = new Gson();
                                    CategoryChildBean[] categoryChildBeen = gson.fromJson(result, CategoryChildBean[].class);
                                    ArrayList<CategoryChildBean> categoryChildBeen1 = utils2.array2List(categoryChildBeen);
                                    ArrayList<ArrayList<CategoryChildBean>> child = new ArrayList<ArrayList<CategoryChildBean>>();
                                    child.add(categoryChildBeen1);
                                    Log.e(TAG, "child=" + child);
                                    mAdapter.addChild(child);
                                }

                                @Override
                                public void onError(String error) {

                                }
                            });
                }


                }

            @Override
            public void onError(String error) {

            }
        });
    }

    private void initView(View layout) {
        mGroupList = new ArrayList<>();
        mChildList = new ArrayList<>();
        mAdapter = new CategroyAdapter(mContext,mGroupList,mChildList);
        melvLayout = (ExpandableListView) layout.findViewById(R.id.elvLayout);
        melvLayout.setAdapter(mAdapter);
    }

}
