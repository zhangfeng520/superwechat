package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.ImageLoader;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.GoodDetailsActivity;
import cn.ucai.fulicenter.bean.CollectBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;

/**
 * Created by sks on 2016/8/6.
 */
public class CollectGoodsAdapter extends RecyclerView.Adapter {
    private static final String TAG = CollectGoodsAdapter.class.getSimpleName();
    Context mContext;
    ArrayList<CollectBean> mCollectList;
    ViewGroup parent;
    public CollectGoodsAdapter(Context mContext, ArrayList<CollectBean> mCollectList) {
        this.mContext = mContext;
        this.mCollectList = mCollectList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        View layout = LayoutInflater.from(mContext).inflate(R.layout.item_collect_goods, null);
        MyViewHolder holder = new MyViewHolder(layout);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder viewHolder = (MyViewHolder) holder;
        final CollectBean good = mCollectList.get(position);
        viewHolder.tvGoodsName.setText(good.getGoodsName());
        viewHolder.tvGoodsEnglishName.setText(good.getGoodsEnglishName());
        ImageLoader.build()
                .url(I.SERVER_ROOT)
                .addParam(I.KEY_REQUEST,I.REQUEST_DOWNLOAD_ALBUM_IMG)
                .addParam("img_url",good.getGoodsImg())
                .width(120)
                .height(120)
                .imageView(viewHolder.ivAvatar)
                .defaultPicture(R.drawable.default_image)
                .listener(parent)
                .showImage(mContext);
        //增加收藏商品的商品点击事件，跳转到商品详情
        viewHolder.layout_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(mContext, GoodDetailsActivity.class).putExtra((D.NewGood.KEY_GOODS_ID),good.getGoodsId()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCollectList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvGoodsName,tvGoodsEnglishName;
        RelativeLayout layout_collect;
        public MyViewHolder(View itemView) {
            super(itemView);
            ivAvatar = (ImageView) itemView.findViewById(R.id.ivAvatar);
            tvGoodsName = (TextView) itemView.findViewById(R.id.tvGoodsTitle);
            tvGoodsEnglishName = (TextView) itemView.findViewById(R.id.tvPrice);
            layout_collect = (RelativeLayout) itemView.findViewById(R.id.layout_collect);
        }
    }
}
