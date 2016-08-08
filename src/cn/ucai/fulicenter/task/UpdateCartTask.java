package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.activity.BaseActivity;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;

/**
 * Created by sks on 2016/8/7.
 */
public class UpdateCartTask extends BaseActivity {
    private static final String TAG = UpdateCartTask.class.getSimpleName();
    Context mContext;
    CartBean mCart;
    public UpdateCartTask(Context mContext, CartBean mCart) {
        this.mContext = mContext;
        this.mCart = mCart;
    }
    public void execute() {
        ArrayList<CartBean> cartGoods = FuliCenterApplication.getInstance().getCartGoods();
        if (cartGoods.contains(mCart)) {
            //如果添加过该商品则修改商品数量
            if (mCart.getCount() <= 0) {
                final OkHttpUtils2<String> utils = new OkHttpUtils2<>();
                utils.setRequestUrl(I.REQUEST_DELETE_CART)
                        .addParam(I.Cart.ID, String.valueOf(mCart.getId()))
                        .targetClass(String.class)
                        .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                ArrayList<CartBean> cartGoods = FuliCenterApplication.getInstance().getCartGoods();
                                cartGoods.remove(mCart);
                                Log.e(TAG, "delete=" + s);
                                Toast.makeText(mContext, "删除物品成功", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(String error) {
                                Log.e(TAG, "deleteerror=" + error);
                            }
                        });
            } else {
                Log.e(TAG, "我错了");
                final OkHttpUtils2<String> utils = new OkHttpUtils2<>();
                utils.setRequestUrl(I.REQUEST_UPDATE_CART)
                        .addParam(I.Cart.ID,String.valueOf(mCart.getId()))
                        .addParam(I.Cart.COUNT,String.valueOf(mCart.getCount()))
                        .addParam(I.Cart.IS_CHECKED,String.valueOf(mCart.isChecked()))
                        .targetClass(String.class)
                        .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                            @Override
                            public void onSuccess(String result) {
                                ArrayList<CartBean> cartGoods = FuliCenterApplication.getInstance().getCartGoods();
                                FuliCenterApplication.getInstance().getCartGoods().remove(cartGoods.size()-1);
                                FuliCenterApplication.getInstance().getCartGoods().add(mCart);
//                                cartGoods.set(cartGoods.indexOf(mCart), mCart);
                                Log.e(TAG, "修改商品成功"+cartGoods.size());
                                Toast.makeText(mContext, "修改商品数量成功", Toast.LENGTH_SHORT).show();
//                                FuliCenterApplication.getInstance().setCartGoods(mCart);
                            }

                            @Override
                            public void onError(String error) {
                                Log.e(TAG, "updatecount=" + error);
                            }
                        });
            }
        } else {
            //如果没有添加过商品则添加
            final OkHttpUtils2<String> utils = new OkHttpUtils2<>();
            utils.setRequestUrl(I.REQUEST_ADD_CART)
                    .addParam(I.Cart.GOODS_ID,String.valueOf(mCart.getGoodsId()))
                    .addParam(I.Cart.USER_NAME,mCart.getUserName())
                    .addParam(I.Cart.COUNT,String.valueOf(mCart.getCount()))
                    .addParam(I.Cart.IS_CHECKED,String.valueOf(mCart.isChecked()))
                    .targetClass(String.class)
                    .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                        @Override
                        public void onSuccess(String result) {
                            Log.e(TAG, "result=" + result);
                            Gson gson = new Gson();
                            MessageBean msg = gson.fromJson(result, MessageBean.class);
                            mCart.setId(Integer.parseInt(msg.getMsg()));
                            ArrayList<CartBean> cartGoods = FuliCenterApplication.getInstance().getCartGoods();
                            cartGoods.add(mCart);
                            Toast.makeText(mContext, "增加商品成功", Toast.LENGTH_SHORT).show();
//                            sendStickyBroadcast(new Intent("update_cart_list"));
                        }

                        @Override
                        public void onError(String error) {
                            Log.e(TAG, "error=" + error);
                        }
                    });
        }
    }

}
