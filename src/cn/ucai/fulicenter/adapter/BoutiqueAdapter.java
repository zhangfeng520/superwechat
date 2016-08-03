package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.ImageLoader;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.BoutiqueBean;

/**
 * Created by sks on 2016/8/2.
 */
public class BoutiqueAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    ArrayList<BoutiqueBean> mGoodsList;
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

    public BoutiqueAdapter(Context mContext, ArrayList<BoutiqueBean> mGoodsList) {
        this.mContext = mContext;
        this.mGoodsList = mGoodsList;
    }

    @Override

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        RecyclerView.ViewHolder holder=null;
        View layout;
        switch (viewType) {
            case ITEM_FOOTER:
                layout = LayoutInflater.from(mContext).inflate(R.layout.item_footer, null);
                holder = new FooterViewHolder(layout);
                break;
            case ITEM_GOODS:
                layout = LayoutInflater.from(mContext).inflate(R.layout.item_boutique, null);
                holder = new BoutiqueViewHolder(layout);
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
        BoutiqueViewHolder viewHolder = (BoutiqueViewHolder) holder;
        BoutiqueBean goods = mGoodsList.get(position);
        viewHolder.tvGoodsName.setText(goods.getName());
        viewHolder.tvDesc.setText(goods.getDescription());
        viewHolder.tvTitle.setText(goods.getTitle());
        ImageLoader.build()
                .url(I.SERVER_ROOT)
                .addParam(I.KEY_REQUEST,I.REQUEST_DOWNLOAD_ALBUM_IMG)
                .addParam("img_url",goods.getImageurl())
                .width(380)
                .height(200)
                .imageView(viewHolder.ivAvatar)
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

    public void initGoodsList(ArrayList<BoutiqueBean> mGoodsList) {
        this.mGoodsList.clear();
        this.mGoodsList.addAll(mGoodsList);
        notifyDataSetChanged();
    }
    public void addGoodsList(ArrayList<BoutiqueBean> mGoodsList) {
        this.mGoodsList.addAll(mGoodsList);
        notifyDataSetChanged();
    }

    class BoutiqueViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvDesc, tvTitle,tvGoodsName;

        public BoutiqueViewHolder(View itemView) {
            super(itemView);
            ivAvatar = (ImageView) itemView.findViewById(R.id.ivAvatar);
            tvDesc = (TextView) itemView.findViewById(R.id.tvGoodsDesc);
            tvTitle = (TextView) itemView.findViewById(R.id.tvGoodsTitle);
            tvGoodsName = (TextView) itemView.findViewById(R.id.tvGoodsName);
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {
        TextView tvFooter;

        public FooterViewHolder(View itemView) {
            super(itemView);
            tvFooter = (TextView) itemView.findViewById(R.id.tvFooter);
        }
    }
}

