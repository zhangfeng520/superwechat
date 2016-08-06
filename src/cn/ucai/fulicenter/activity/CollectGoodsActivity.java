package cn.ucai.fulicenter.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.ImageLoader;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.CollectGoodsAdapter;
import cn.ucai.fulicenter.bean.CollectBean;

public class CollectGoodsActivity extends Activity {
    private static final String TAG = CollectGoodsActivity.class.getSimpleName();
    TextView tvCollectCount;
    RecyclerView mrvCollectGoods;
    ImageView mivcollectgoods;
    Context mContext;
    ArrayList<CollectBean> mCollectList;
    LinearLayoutManager mLayoutManager;
    CollectGoodsAdapter mAdapter;
    //增加下拉刷新
    SwipeRefreshLayout srl;
    TextView tvRefreshHint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_goods);
        mContext = this;
        initView();
        setListener();
    }

    @Override
    protected void onResume() {
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
                ArrayList<CollectBean> goods = FuliCenterApplication.getInstance().getCollectGoods();
                int count = FuliCenterApplication.getInstance().getCollectCount();
                Log.e(TAG, "正在执行下拉刷新goods=" + goods + ",count=" + count);
            }
        });
    }

    //给下拉刷新增加一个开关




    private void initView() {
        tvRefreshHint = (TextView) findViewById(R.id.tvRefreshHint);
        tvCollectCount = (TextView) findViewById(R.id.tvCollectCount);
        mivcollectgoods = (ImageView) findViewById(R.id.collectgoods);
        mrvCollectGoods = (RecyclerView) findViewById(R.id.rvCollectGoods);
        mCollectList = FuliCenterApplication.getInstance().getCollectGoods();
        mAdapter = new CollectGoodsAdapter(mContext, mCollectList);
        mLayoutManager = new LinearLayoutManager(mContext);
        srl = (SwipeRefreshLayout) findViewById(R.id.srl);

        mrvCollectGoods.setAdapter(mAdapter);
        mrvCollectGoods.setLayoutManager(mLayoutManager);
        int  count= FuliCenterApplication.getInstance().getCollectCount();
        if (count != 0) {
            String text = "全部宝贝(" + count + ")";
            tvCollectCount.setText(text);
            mivcollectgoods.setVisibility(View.GONE);
        } else {
            tvCollectCount.setText("全部宝贝(??)");
            mivcollectgoods.setVisibility(View.VISIBLE);
        }

    }

    private void setRecycleView() {
        initView();
        srl.setRefreshing(false);
        tvRefreshHint.setVisibility(View.GONE);
    }

    public void onBack(View view) {
        finish();
    }
}
