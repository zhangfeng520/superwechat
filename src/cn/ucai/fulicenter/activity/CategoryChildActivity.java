package cn.ucai.fulicenter.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.Fragments.NewGoodsFragment;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.ImageLoader;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.NewGoodsAdapter;
import cn.ucai.fulicenter.bean.CategoryChildBean;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.view.CatChildFilterButton;

public class CategoryChildActivity extends Activity implements View.OnClickListener{
    final static String TAG = NewGoodsFragment.class.getSimpleName();
    Context mContext;
    static final int ACTION_DOWNLOAD=0;
    static final int ACITON_PULL_DOWN=1;
    static final int ACTION_PULL_UP=2;
    static final int PAGE_SIZE=2;
    int mPageId=0;
    NewGoodsAdapter mAdapter;
    RecyclerView mrvNewGoods;
    SwipeRefreshLayout msrl;
    ArrayList<NewGoodBean> mGoodsList;
    TextView mtvRefreshHint,tvTitle;
    GridLayoutManager mLayoutManager;
    private Button btnPrice;
    private Button btnTime;
    private boolean mSortPrice;
    private boolean mSortTime;
    private int sortBy;
    private CatChildFilterButton mCatChildFilterButton;
    private String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_child);
        mContext=this;
        initView();
        initData();
        setListener();
    }
    private void setListener() {
        setDownListener();
        btnPrice.setOnClickListener(this);
        btnTime.setOnClickListener(this);
        ArrayList<CategoryChildBean> childList = (ArrayList<CategoryChildBean>) getIntent().getSerializableExtra("childList");
        mCatChildFilterButton.setOnCatFilterClickListener(name,childList);
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

    private void initData() {
        downloadNewGoodsList(ACTION_DOWNLOAD, mPageId);
    }

    private void downloadNewGoodsList(final int action, int pageId) {
        final int CAT_ID = getIntent().getIntExtra(I.CategoryChild.CAT_ID, 0);
        final OkHttpUtils2<String> utils = new OkHttpUtils2<String>();
        utils.setRequestUrl(I.REQUEST_FIND_GOODS_DETAILS)
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
//                            if (action == ACTION_PULL_UP) {
////                                mAdapter.setFooterText("没有更多新货了！");
//                            }
                            return;
                        }
                        ArrayList<NewGoodBean> mGoodsList = utils.array2List(array);
                        Log.e(TAG, "mGoodsList=" + mGoodsList);

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
//                                mAdapter.addGoodsList(mGoodsList);
//                                break;
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });


    }

    private void initView() {
        mrvNewGoods = (RecyclerView)findViewById(R.id.rvNewGoods);
        mGoodsList = new ArrayList<>();
        mAdapter = new NewGoodsAdapter(mContext, mGoodsList);
        mrvNewGoods.setAdapter(mAdapter);
        mLayoutManager = new GridLayoutManager(mContext, 2);
        mrvNewGoods.setLayoutManager(mLayoutManager);
        msrl = (SwipeRefreshLayout)findViewById(R.id.srl);
        mtvRefreshHint = (TextView) findViewById(R.id.tvRefreshHint);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        btnPrice = (Button) findViewById(R.id.btnPrice);
        btnTime = (Button) findViewById(R.id.btnTime);
        mCatChildFilterButton = (CatChildFilterButton) findViewById(R.id.btnCatChildFilter);
        name=getIntent().getStringExtra(I.CategoryGroup.NAME);
        mCatChildFilterButton.setText(name);
    }

    public void onBack(View view) {
        finish();
    }

    private void changeImage(int drawableId,Button btn) {
        Drawable rewardIcon = getResources().getDrawable(drawableId);
        if(rewardIcon!=null)
        {
            rewardIcon.setBounds(0, 0, rewardIcon.getMinimumWidth(), rewardIcon.getMinimumHeight());
            btn.setCompoundDrawables(null, null,rewardIcon, null);
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPrice:
                if (mSortPrice) {
                    sortBy = I.SORT_BY_PRICE_ASC;
                   changeImage(R.drawable.upup,btnPrice);
                } else {
                    sortBy = I.SORT_BY_PRICE_DESC;
                    changeImage(R.drawable.downdown,btnPrice);
                }
                mSortPrice = !mSortPrice;
                break;
            case R.id.btnTime:
                if (mSortTime) {
                    sortBy = I.SORT_BY_ADDTIME_ASC;
                    changeImage(R.drawable.upup,btnTime);
                    Log.e(TAG, "btnTimeup");
                } else {
                    sortBy = I.SORT_BY_ADDTIME_DESC;
                    changeImage(R.drawable.downdown,btnTime);
                    Log.e(TAG, "btnTimedown");
                }
                mSortTime = !mSortTime;
                break;
        }
        mAdapter.setSortBy(sortBy);
    }
}
