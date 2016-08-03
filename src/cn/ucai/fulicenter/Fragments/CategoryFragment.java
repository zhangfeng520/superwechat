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

    private void getGroupList() {
        final OkHttpUtils2<String> utils = new OkHttpUtils2<>();
        utils.setRequestUrl(I.REQUEST_FIND_CATEGORY_GROUP)
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Log.e(TAG, "result01=" + result);
                        Gson gson = new Gson();
                        CategoryGroupBean[] array = gson.fromJson(result, CategoryGroupBean[].class);
                        mGroupList = utils.array2List(array);
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
