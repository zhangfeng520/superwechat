package cn.ucai.fulicenter.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
    TextView tvCollectCount;
    RecyclerView mrvCollectGoods;
    ImageView mivcollectgoods;
    Context mContext;
    ArrayList<CollectBean> mCollectList;
    LinearLayoutManager mLayoutManager;
    CollectGoodsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_goods);
        mContext=this;
        initView();
        initData();
    }

    private void initData() {
       int  count= FuliCenterApplication.getInstance().getCollectCount();
        if (count!= 0) {
            String text="全部宝贝("+count+")";
            tvCollectCount.setText(text);
            mivcollectgoods.setVisibility(View.GONE);
        }
    }

    private void initView() {
        tvCollectCount = (TextView) findViewById(R.id.tvCollectCount);
        mivcollectgoods = (ImageView) findViewById(R.id.collectgoods);
        mrvCollectGoods = (RecyclerView) findViewById(R.id.rvCollectGoods);
        mCollectList = FuliCenterApplication.getInstance().getCollectGoods();
        mAdapter = new CollectGoodsAdapter(mContext, mCollectList);
        mrvCollectGoods.setAdapter(mAdapter);
        mLayoutManager = new LinearLayoutManager(mContext);
        mrvCollectGoods.setLayoutManager(mLayoutManager);
    }

    public void onBack(View view) {
        finish();
    }
}
