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
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.CartAdapter;
import cn.ucai.fulicenter.adapter.CollectGoodsAdapter;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.CollectBean;

/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment {
    private static final String TAG = CartFragment.class.getSimpleName();
    RecyclerView mrvCollectGoods;
    ImageView mivcollectgoods;
    Context mContext;
    ArrayList<CartBean> mCartList;
    LinearLayoutManager mLayoutManager;
    CartAdapter mAdapter;
    //增加下拉刷新
    SwipeRefreshLayout srl;
    TextView tvRefreshHint;

    View view ;

    public CartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_cart, container, false);
        view=layout;
        mContext = layout.getContext();
        initView(layout);
        setListener();
        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        setListener();
    }

    private void setListener() {
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srl.setRefreshing(true);
                srl.setEnabled(true);
                tvRefreshHint.setVisibility(View.VISIBLE);
                setRecycleView();
//                ArrayList<CollectBean> goods = FuliCenterApplication.getInstance().getCollectGoods();
                int count = FuliCenterApplication.getInstance().getCartCount();
                Log.e(TAG, "count=" + count);
            }
        });
    }
    private void initView(View layout) {
        tvRefreshHint = (TextView) layout.findViewById(R.id.tvRefreshHint);
        mivcollectgoods = (ImageView) layout.findViewById(R.id.collectgoods);
        mrvCollectGoods = (RecyclerView)layout.findViewById(R.id.rvNewGoods);
        mCartList = FuliCenterApplication.getInstance().getCartGoods();
        mAdapter = new CartAdapter(mContext, mCartList);
        mLayoutManager = new LinearLayoutManager(mContext);
        srl = (SwipeRefreshLayout)layout.findViewById(R.id.srl);

        mrvCollectGoods.setAdapter(mAdapter);
        mrvCollectGoods.setLayoutManager(mLayoutManager);

    }

    private void setRecycleView() {
        initView(view);
        srl.setRefreshing(false);
        tvRefreshHint.setVisibility(View.GONE);
    }

}
