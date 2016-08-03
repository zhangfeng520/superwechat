package cn.ucai.fulicenter.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.ImageLoader;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.NewGoodsAdapter;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewGoodsFragment extends Fragment {
    final static String TAG = NewGoodsFragment.class.getSimpleName();
    Context mContext;
    static final int ACTION_DOWNLOAD=0;
    static final int ACITON_PULL_DOWN=1;
    static final int ACTION_PULL_UP=2;
    static final int PAGE_SIZE=10;
    static final int CAT_ID= 0;
    int mPageId=0;
    NewGoodsAdapter mAdapter;
    RecyclerView mrvNewGoods;
    SwipeRefreshLayout msrl;
    ArrayList<NewGoodBean> mGoodsList;
    TextView mtvRefreshHint;
    GridLayoutManager mLayoutManager;

    public NewGoodsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_new_goods, container, false);
        mContext = layout.getContext();
        initView(layout);
        initData();
        setListener();
        return layout;
    }

    private void setListener() {
        setDownListener();
        setDownUpListener();
    }

    private void setDownListener() {
        msrl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                msrl.setEnabled(true);
                msrl.setRefreshing(true);
                mtvRefreshHint.setVisibility(View.VISIBLE);
                mPageId=0;
                downloadNewGoodsList(ACITON_PULL_DOWN,mPageId);
            }
        });
    }

    private void setDownUpListener() {
        mrvNewGoods.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastPosition;
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastPosition = mLayoutManager.findLastVisibleItemPosition();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastPosition >=mAdapter.getItemCount() - 1 && mAdapter.isMore()) {
                    mPageId+=lastPosition;
                    downloadNewGoodsList(ACTION_PULL_UP,mPageId);
                }
            }
        });
    }

    private void initData() {
        downloadNewGoodsList(ACTION_DOWNLOAD, mPageId);
    }

    private void downloadNewGoodsList(final int action, int pageId) {
        final OkHttpUtils2<String> utils = new OkHttpUtils2<String>();
        utils.setRequestUrl(I.REQUEST_FIND_NEW_BOUTIQUE_GOODS)
                .addParam("cat_id",String.valueOf(CAT_ID))
                .addParam(I.PAGE_ID,pageId+"")
                .addParam(I.PAGE_SIZE,PAGE_SIZE+"")
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Log.e(TAG, "111111111111");
                        Log.e(TAG, "result=" +result);
                        Gson gson = new Gson();
                        NewGoodBean[] array = gson.fromJson(result, NewGoodBean[].class);
                        mAdapter.setMore(!(array == null || array.length == 0));
                        if (!mAdapter.isMore()) {
                            if (action == ACTION_PULL_UP) {
                                mAdapter.setFooterText("没有更多新货了！");
                            }
                            return;
                        }
                        ArrayList<NewGoodBean> mGoodsList = utils.array2List(array);
                        Log.e(TAG, "mGoodsList=" + mGoodsList);

                        switch (action) {
                            case ACTION_DOWNLOAD:
                                mAdapter.initGoodsList(mGoodsList);
                                mAdapter.setFooterText("疯狂加载中...");
                                break;
                            case ACITON_PULL_DOWN:
                                mAdapter.initGoodsList(mGoodsList);
                                mAdapter.setFooterText("疯狂加载中...");
                                msrl.setRefreshing(false);
                                mtvRefreshHint.setVisibility(View.GONE);
                                ImageLoader.release();
                                break;
                            case ACTION_PULL_UP:
                                mAdapter.addGoodsList(mGoodsList);
                                break;
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });


    }

    private void initView(View layout) {
        mrvNewGoods = (RecyclerView) layout.findViewById(R.id.rvNewGoods);
        mGoodsList = new ArrayList<>();
        mAdapter = new NewGoodsAdapter(mContext, mGoodsList);
        mrvNewGoods.setAdapter(mAdapter);
        mLayoutManager = new GridLayoutManager(mContext, 2);
        mrvNewGoods.setLayoutManager(mLayoutManager);
        msrl = (SwipeRefreshLayout) layout.findViewById(R.id.srl);
        mtvRefreshHint = (TextView) layout.findViewById(R.id.tvRefreshHint);
    }

}
