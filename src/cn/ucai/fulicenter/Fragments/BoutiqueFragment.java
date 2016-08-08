package cn.ucai.fulicenter.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
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
import cn.ucai.fulicenter.adapter.BoutiqueAdapter;
import cn.ucai.fulicenter.bean.BoutiqueBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;

/**
 * A simple {@link Fragment} subclass.
 */
public class BoutiqueFragment extends Fragment {

    final static String TAG = NewGoodsFragment.class.getSimpleName();
    Context mContext;
    static final int ACTION_DOWNLOAD=0;
    static final int ACITON_PULL_DOWN=1;
    static final int ACTION_PULL_UP=2;
    BoutiqueAdapter mAdapter;
    RecyclerView mrvNewGoods;
    SwipeRefreshLayout msrl;
    ArrayList<BoutiqueBean> mGoodsList;
    TextView mtvRefreshHint;
    LinearLayoutManager mLayoutManager;

    public BoutiqueFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_boutique, container, false);
        mContext = layout.getContext();
        initView(layout);
        initData();
        setListener();
        int action = getActivity().getIntent().getIntExtra("action", 0);
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
                downloadNewGoodsList(ACITON_PULL_DOWN);
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
                downloadNewGoodsList(ACTION_PULL_UP);
            }
        });
    }

    private void initData() {
        downloadNewGoodsList(ACTION_DOWNLOAD);
    }

    private void downloadNewGoodsList(final int action) {
        final OkHttpUtils2<String> utils = new OkHttpUtils2<String>();
        utils.setRequestUrl(I.REQUEST_FIND_BOUTIQUES)
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Log.e(TAG, "22222222222");
                        Log.e(TAG, "boutique=" +result);
                        Gson gson = new Gson();
                        BoutiqueBean[] array = gson.fromJson(result, BoutiqueBean[].class);
                        mAdapter.setMore(!(array == null || array.length == 0));
                        if (!mAdapter.isMore()) {

                            return;
                        }
                        ArrayList<BoutiqueBean> mGoodsList = utils.array2List(array);
                        switch (action) {
                            case ACTION_DOWNLOAD:
                                mAdapter.initGoodsList(mGoodsList);
//                                mAdapter.setFooterText("疯狂加载中...");
                                break;
                            case ACITON_PULL_DOWN:
                                mAdapter.initGoodsList(mGoodsList);
//                                mAdapter.setFooterText("疯狂加载中...");
                                msrl.setRefreshing(false);
                                mtvRefreshHint.setVisibility(View.GONE);
                                ImageLoader.release();
                                break;
//                            case ACTION_PULL_UP:
//                                mAdapter.setFooterText("没有更多新货了！");
//                                break;
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
        mAdapter = new BoutiqueAdapter(mContext, mGoodsList);
        mrvNewGoods.setAdapter(mAdapter);
        mLayoutManager = new LinearLayoutManager(mContext);
        mrvNewGoods.setLayoutManager(mLayoutManager);
        msrl = (SwipeRefreshLayout) layout.findViewById(R.id.srl);
        mtvRefreshHint = (TextView) layout.findViewById(R.id.tvRefreshHint);
    }


}
