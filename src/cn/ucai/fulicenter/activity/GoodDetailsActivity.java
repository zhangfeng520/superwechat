package cn.ucai.fulicenter.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.view.FlowIndicator;
import cn.ucai.fulicenter.view.SlideAutoLoopView;

/**
 * Created by sks on 2016/8/3.
 */
public class GoodDetailsActivity extends BaseActivity {
    private static final String TAG = GoodDetailsActivity.class.getSimpleName();
    private ImageView ivShare;
    private ImageView ivCollect;
    private ImageView ivCart;
    private TextView tvCartCount;

    private TextView tvEnglishName;
    private TextView tvName;
    private TextView tvPriceShop;
    private TextView tvPriceCurrent;

    private SlideAutoLoopView mSlideAutoLoopView;
    private FlowIndicator mFlowIndicator;
    private WebView mwbGoodBrief;
    private int mGoodsId;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_good_details);
        initView();
        initData();
        setListener();
    }

    private void setListener() {
        final OkHttpUtils2<String> utils = new OkHttpUtils2();
        utils.setRequestUrl(I.REQUEST_FIND_GOOD_DETAILS)
                .addParam(I.Cart.GOODS_ID,String.valueOf(mGoodsId))
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Log.e(TAG, "result===" + result);
                        Gson gson = new Gson();
                        GoodDetailsBean goodDetailsBean = gson.fromJson(result, GoodDetailsBean.class);
                        if (goodDetailsBean != null) {
                            tvEnglishName.setText(goodDetailsBean.getGoodsEnglishName());
                            tvName.setText(goodDetailsBean.getGoodsName());
                            tvPriceShop.setText("商店价格："+goodDetailsBean.getShopPrice());
                            tvPriceCurrent.setText("当前价格："+goodDetailsBean.getCurrencyPrice());

                        }else{
                            Toast.makeText(GoodDetailsActivity.this, "获取信息失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "error=" + error);
                    }
                });
    }

    private void initData() {
        mGoodsId = getIntent().getIntExtra((D.NewGood.KEY_GOODS_ID), 0);
    }

    private void initView() {
        ivShare = (ImageView) findViewById(R.id.iv_good_share);
        ivCollect = (ImageView) findViewById(R.id.iv_good_collect);
        ivCart = (ImageView) findViewById(R.id.iv_good_cart);
        tvCartCount = (TextView) findViewById(R.id.tv_good_count);
        tvEnglishName = (TextView) findViewById(R.id.tv_good_EnglishName);
        tvName = (TextView) findViewById(R.id.tv_good_name);
        tvPriceShop = (TextView) findViewById(R.id.tv_price_shop);
        tvPriceCurrent = (TextView) findViewById(R.id.tv_price_current);
        mSlideAutoLoopView = (SlideAutoLoopView) findViewById(R.id.salv);
        mFlowIndicator = (FlowIndicator) findViewById(R.id.indicator);
        mwbGoodBrief = (WebView) findViewById(R.id.wvBrief);
        WebSettings settings = mwbGoodBrief.getSettings();
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setBuiltInZoomControls(true);
    }

    public void onBack(View view) {
        finish();
    }
}
