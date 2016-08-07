package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.IntegerRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.ImageLoader;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.GoodDetailsActivity;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.CollectBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;

/**
 * Created by sks on 2016/8/7.
 */
public class CartAdapter extends RecyclerView.Adapter{
    private static final String TAG = CollectGoodsAdapter.class.getSimpleName();
    Context mContext;
    ArrayList<CartBean> mCartList;
    ViewGroup parent;
    public CartAdapter(Context mContext,ArrayList<CartBean> mCartList) {
        this.mContext = mContext;
        this.mCartList = mCartList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        View layout = LayoutInflater.from(mContext).inflate(R.layout.item_cart, null);
        MyViewHolder holder = new MyViewHolder(layout);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final MyViewHolder viewHolder = (MyViewHolder) holder;
        final CartBean good = mCartList.get(position);
        int goodId=good.getGoodsId();
//        GoodDetailsBean goods = good.getGoods();
//        Log.e(TAG, "goods========" + goods);

        final OkHttpUtils2<String> utils = new OkHttpUtils2();
        utils.setRequestUrl(I.REQUEST_FIND_GOOD_DETAILS)
                .addParam(I.Cart.GOODS_ID, String.valueOf(goodId))
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Log.e(TAG, "result===" + result);
                        Gson gson = new Gson();
                        GoodDetailsBean goodDetailsBean = gson.fromJson(result, GoodDetailsBean.class);
                        if (goodDetailsBean != null) {
                            //得到点击数据
                            viewHolder.tvGoodsName.setText(goodDetailsBean.getGoodsName());
                            viewHolder.tvPrice.setText(goodDetailsBean.getCurrencyPrice());
                            viewHolder.tvGoodsTitle.setText(goodDetailsBean.getGoodsEnglishName());
                            ImageLoader.build()
                                    .url(I.SERVER_ROOT)
                                    .addParam(I.KEY_REQUEST,I.REQUEST_DOWNLOAD_ALBUM_IMG)
                                    .addParam("img_url",goodDetailsBean.getGoodsImg())
                                    .width(120)
                                    .height(120)
                                    .imageView(viewHolder.ivAvatar)
                                    .defaultPicture(R.drawable.default_image)
                                    .listener(parent)
                                    .showImage(mContext);
                            //增加收藏商品的商品点击事件，跳转到商品详情
                            viewHolder.layout_cart.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mContext.startActivity(new Intent(mContext, GoodDetailsActivity.class).putExtra((D.NewGood.KEY_GOODS_ID),good.getGoodsId()));
                                }
                            });
                            //修改购物车中单个商品显示的商品数量
                            viewHolder.tvGoodsCount.setText(String.valueOf(good.getCount()));

                        } else {
                            Toast.makeText(mContext, "获取信息失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "error=" + error);
                    }
                });

    }

    @Override
    public int getItemCount() {
        return mCartList==null?0:mCartList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar,ivIncreate,ivReduce;
        TextView tvGoodsName,tvPrice,tvGoodsTitle,tvGoodsCount;
        RelativeLayout layout_cart;

        public MyViewHolder(View itemView) {
            super(itemView);
            ivAvatar = (ImageView) itemView.findViewById(R.id.ivAvatar);
            tvGoodsName = (TextView) itemView.findViewById(R.id.tvGoodsName);
            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);

            layout_cart = (RelativeLayout) itemView.findViewById(R.id.layout_cart);
            tvGoodsTitle = (TextView) itemView.findViewById(R.id.tvGoodsTitle);
            tvGoodsCount = (TextView) itemView.findViewById(R.id.tvGoodsCount);
            ivIncreate = (ImageView) itemView.findViewById(R.id.ivIncreate);
            ivReduce = (ImageView) itemView.findViewById(R.id.ivReduce);

        }
    }
}
