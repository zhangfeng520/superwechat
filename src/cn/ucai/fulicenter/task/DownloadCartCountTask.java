package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;

import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.CollectBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;

/**
 * Created by sks on 2016/8/6.
 */
public class DownloadCartCountTask {
    private final static String TAG = DownloadCartCountTask.class.getSimpleName();
    private String userName;
    Context mContext;
    //下载所有数据
    private int page_id=0;
    private int page_size=100;


    public DownloadCartCountTask(Context mContext, String userName) {
        this.mContext = mContext;
        this.userName = userName;
    }

    public void execute() {
        final OkHttpUtils2<String> utils = new OkHttpUtils2<>();
        utils.setRequestUrl(I.REQUEST_FIND_CARTS)
                .addParam(I.Cart.USER_NAME,userName)
                .addParam(I.PAGE_ID,String.valueOf(page_id))
                .addParam(I.PAGE_SIZE,String.valueOf(page_size))
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.e(TAG,"s==="+s);
                        if (s != null) {
                            int allCount=0;
                            Gson gson = new Gson();
                            CartBean[] CartBean = gson.fromJson(s, CartBean[].class);
                            ArrayList<CartBean> cartBean = utils.array2List(CartBean);
                            Log.e(TAG, "cartBean=" + cartBean);
                            //保存用户购物车商品到全局变量
                            FuliCenterApplication.getInstance().setCartGoods(cartBean);
                            for (CartBean good : cartBean) {
                                int count = good.getCount();
                                allCount+=count;
                            }
                            //保存用户购物车商品数量到全局变量
                            FuliCenterApplication.getInstance().setCartCount(allCount);
                            mContext.sendStickyBroadcast(new Intent("update_cart_list"));
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });

    }
}
