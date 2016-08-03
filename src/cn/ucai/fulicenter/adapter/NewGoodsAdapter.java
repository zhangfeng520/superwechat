package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.ImageLoader;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.NewGoodBean;

/**
 * Created by sks on 2016/8/1.
 */
public class NewGoodsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    ArrayList<NewGoodBean> mGoodsList;
    static final int ITEM_FOOTER=0;
    static final int ITEM_GOODS=1;
    String footerText;
    ViewGroup parent;
    boolean more;

    public boolean isMore() {
        return more;
    }

    public void setMore(boolean more) {
        this.more = more;
    }

    public void setFooterText(String footerText) {
        this.footerText = footerText;
        notifyDataSetChanged();
    }

    public NewGoodsAdapter(Context mContext, ArrayList<NewGoodBean> mGoodsList) {
        this.mContext = mContext;
        this.mGoodsList = mGoodsList;
        mGoodsList.addAll(mGoodsList);
        sortByAddTime();
    }

    @Override

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        RecyclerView.ViewHolder holder=null;
        View  layout;
        switch (viewType) {
            case ITEM_FOOTER:
                layout = LayoutInflater.from(mContext).inflate(R.layout.item_footer, null);
                holder = new FooterViewHolder(layout);
                break;
            case ITEM_GOODS:
                layout = LayoutInflater.from(mContext).inflate(R.layout.item_new_goods, null);
                holder = new NewGoodsViewHolder(layout);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == ITEM_FOOTER) {
            ((FooterViewHolder) holder).tvFooter.setText(footerText);
            return;
        }
        NewGoodsViewHolder viewHolder = (NewGoodsViewHolder) holder;
        NewGoodBean goods = mGoodsList.get(position);
        viewHolder.tvDescription.setText(goods.getGoodsBrief());
        viewHolder.tvMoney.setText(goods.getCurrencyPrice());
        ImageLoader.build()
                .url(I.SERVER_ROOT)
                .addParam(I.KEY_REQUEST,I.REQUEST_DOWNLOAD_ALBUM_IMG)
                .addParam("img_url",goods.getGoodsImg())
                .width(180)
                .height(200)
                .imageView(viewHolder.ivNewGoods)
                .defaultPicture(R.drawable.default_image)
                .listener(parent)
                .showImage(mContext);

    }

    @Override
    public int getItemCount() {
        return mGoodsList==null?0:mGoodsList.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return ITEM_FOOTER;
        } else {
            return ITEM_GOODS;
        }
    }

    public void initGoodsList(ArrayList<NewGoodBean> mGoodsList) {
        this.mGoodsList.clear();
        this.mGoodsList.addAll(mGoodsList);
//        sortByAddTime();
        notifyDataSetChanged();
    }
    public void addGoodsList(ArrayList<NewGoodBean> mGoodsList) {
        this.mGoodsList.addAll(mGoodsList);
//        sortByAddTime();
        notifyDataSetChanged();
    }

    class NewGoodsViewHolder extends RecyclerView.ViewHolder {
        ImageView ivNewGoods;
        TextView tvDescription, tvMoney;

        public NewGoodsViewHolder(View itemView) {
            super(itemView);
            ivNewGoods = (ImageView) itemView.findViewById(R.id.ivNewGoods);
            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
            tvMoney = (TextView) itemView.findViewById(R.id.tvMoney);
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {
        TextView tvFooter;

        public FooterViewHolder(View itemView) {
            super(itemView);
            tvFooter = (TextView) itemView.findViewById(R.id.tvFooter);
        }
    }

    private void sortByAddTime() {
        Collections.sort(mGoodsList, new Comparator<NewGoodBean>() {
            @Override
            public int compare(NewGoodBean newGoodBean, NewGoodBean t1) {
                return (int) (Long.valueOf(newGoodBean.getAddTime())-Long.valueOf(t1.getAddTime()));
            }
        });
    }
}
