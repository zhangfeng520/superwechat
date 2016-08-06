package cn.ucai.fulicenter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.AlbumsBean;
import cn.ucai.fulicenter.bean.CollectBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.bean.UserAvatar;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.task.DownloadCollectTask;
import cn.ucai.fulicenter.view.FlowIndicator;
import cn.ucai.fulicenter.view.SlideAutoLoopView;

/**
 * Created by sks on 2016/8/3.
 */
public class GoodDetailsActivity extends BaseActivity {
    private static final String TAG = GoodDetailsActivity.class.getSimpleName();
    private ImageView ivShare;
    //商品收藏
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
                            showGoodDetails(goodDetailsBean);
                            //得到点击数据
                            FuliCenterApplication.getInstance().setGoodDetailsBean(goodDetailsBean);
                        }else{
                            Toast.makeText(GoodDetailsActivity.this, "获取信息失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "error=" + error);
                    }
                });

        final UserAvatar user = FuliCenterApplication.getInstance().getUser();
        //收藏的点击事件
        ivCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user!=null) {
                    final OkHttpUtils2<String> utils2 = new OkHttpUtils2<String>();
                    utils2.setRequestUrl(I.REQUEST_IS_COLLECT)
                            .addParam(I.Collect.GOODS_ID, String.valueOf(mGoodsId))
                            .addParam(I.Collect.USER_NAME, user.getMUserName())
                            .targetClass(String.class)
                            .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                                @Override
                                public void onSuccess(String s) {
                                    int count = FuliCenterApplication.getInstance().getCollectCount();
                                    Log.e(TAG, "s=" + s);
                                    Gson gson = new Gson();
                                    MessageBean msg = gson.fromJson(s, MessageBean.class);
                                    if (msg.isSuccess()) {
                                        FuliCenterApplication.getInstance().setCollectCount(count-1);
                                        ivCollect.setImageResource(R.drawable.bg_collect_in);
                                        FuliCenterApplication.getInstance().getCollectGoods().remove(count - 1);
                                        //删除收藏
                                        deleteCollect(user.getMUserName(), mGoodsId);
                                    }else{
                                        FuliCenterApplication.getInstance().setCollectCount(count + 1);
                                        ivCollect.setImageResource(R.drawable.bg_collect_out);
                                        //添加收藏
                                        addCollect(user.getMUserName());
                                    }
                                }

                                @Override
                                public void onError(String error) {
                                    Toast.makeText(GoodDetailsActivity.this, "error"+error, Toast.LENGTH_SHORT).show();
                                }
                            });

                }else {
                    Toast.makeText(GoodDetailsActivity.this, "请先进行登录", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(GoodDetailsActivity.this,LoginActivity.class));
                    //增加全局变量判断如果是从商品详情进入的登录界面，如果选择返回，则返回当前页面
                    FuliCenterApplication.getInstance().setB(0);
                }
            }
        });
    }
    /**
     * 删除收藏
     * create by MrZhang
     */
    private void deleteCollect(String userName, int goodId) {
        final OkHttpUtils2<String> utils = new OkHttpUtils2<>();
        utils.setRequestUrl(I.REQUEST_DELETE_COLLECT)
                .addParam(I.Collect.USER_NAME,userName)
                .addParam(I.Collect.GOODS_ID,String.valueOf(goodId))
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Log.e(TAG, "result05=" + result);
                        Gson gson = new Gson();
                        MessageBean msg = gson.fromJson(result, MessageBean.class);
                        if (msg.isSuccess()) {
                            Log.e(TAG, "delete成功啦");
                            Toast.makeText(GoodDetailsActivity.this, "取消收藏成功啦！", Toast.LENGTH_SHORT).show();
                            UserAvatar user = FuliCenterApplication.getInstance().getUser();
                            new DownloadCollectTask(GoodDetailsActivity.this,user.getMUserName()).execute();

                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
    }

    /**
     * 添加收藏
     * create by MrZhang
     */
    private void addCollect(final String userName) {
        final GoodDetailsBean good = FuliCenterApplication.getInstance().getGoodDetailsBean();
        final OkHttpUtils2<String> utlis = new OkHttpUtils2<String>();
        utlis.setRequestUrl(I.REQUEST_ADD_COLLECT)
                .addParam(I.Collect.USER_NAME,userName)
                .addParam(I.Collect.GOODS_ID,String.valueOf(good.getGoodsId()))
                .addParam(I.Collect.GOODS_NAME,good.getGoodsName())
                .addParam(I.Collect.GOODS_ENGLISH_NAME,good.getGoodsEnglishName())
                .addParam(I.Collect.GOODS_THUMB,good.getGoodsThumb())
                .addParam(I.Collect.GOODS_IMG,good.getGoodsImg())
                .addParam("addTime",String.valueOf(good.getAddTime()))
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.e(TAG, "addCollect.s=" + s);
                        Gson gson = new Gson();
                        MessageBean msg = gson.fromJson(s, MessageBean.class);
                        if (msg.isSuccess()) {
                            CollectBean collectBean = new CollectBean();
                            collectBean.setId(0);
                            collectBean.setUserName(userName);
                            collectBean.setGoodsId(good.getGoodsId());
                            collectBean.setGoodsName(good.getGoodsName());
                            collectBean.setGoodsEnglishName(good.getGoodsEnglishName());
                            collectBean.setGoodsThumb(good.getGoodsThumb());
                            collectBean.setGoodsImg(good.getGoodsImg());
                            collectBean.setAddTime(good.getAddTime());
                            FuliCenterApplication.getInstance().getCollectGoods().add(collectBean);
                            Log.e(TAG, "累死宝宝了");
                            Log.e(TAG, "collectBean=" + collectBean);
                            Toast.makeText(GoodDetailsActivity.this, "添加收藏成功啦！", Toast.LENGTH_SHORT).show();
                            UserAvatar user = FuliCenterApplication.getInstance().getUser();
                            new DownloadCollectTask(GoodDetailsActivity.this,user.getMUserName()).execute();
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
    }

    private void showGoodDetails(GoodDetailsBean goodDetailsBean) {
        tvEnglishName.setText(goodDetailsBean.getGoodsEnglishName());
        tvName.setText(goodDetailsBean.getGoodsName());
        tvPriceShop.setText("商店价格："+goodDetailsBean.getShopPrice());
        tvPriceCurrent.setText("当前价格："+goodDetailsBean.getCurrencyPrice());
        mSlideAutoLoopView.startPlayLoop(mFlowIndicator, getAlbumImagUrl(goodDetailsBean), goodDetailsBean.getProperties()[0].getAlbums().length);
        mwbGoodBrief.loadDataWithBaseURL(null,goodDetailsBean.getGoodsBrief(),D.TEXT_HTML,D.UTF_8,null);
    }

    private String[] getAlbumImagUrl(GoodDetailsBean goodDetailsBean) {
        String[] albumImagUrl = new String[]{};
        if (goodDetailsBean.getProperties() != null && goodDetailsBean.getProperties().length > 0) {
            AlbumsBean[] albums = goodDetailsBean.getProperties()[0].getAlbums();
            albumImagUrl = new String[albums.length];
            for(int i=0;i<albumImagUrl.length;i++) {
                albumImagUrl[i] = albums[i].getImgUrl();
            }
        }
            return albumImagUrl;
    }

    private void initData() {
        mGoodsId = getIntent().getIntExtra((D.NewGood.KEY_GOODS_ID), 0);
        //增加判断条件显示图片
        if (FuliCenterApplication.getInstance().getUser() != null) {
            ArrayList<CollectBean> collectGoods = FuliCenterApplication.getInstance().getCollectGoods();
            for (CollectBean collect : collectGoods) {
                if (mGoodsId == collect.getGoodsId()) {
                    ivCollect.setImageResource(R.drawable.bg_collect_out);
                }
            }
        }
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
        //返回时清空得到的gooddetailsbean
        FuliCenterApplication.getInstance().setGoodDetailsBean(null);
        finish();
    }
}
