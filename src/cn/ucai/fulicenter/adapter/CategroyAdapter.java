package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.ImageLoader;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.CategoryChildBean;
import cn.ucai.fulicenter.bean.CategoryGroupBean;

/**
 * Created by sks on 2016/8/2.
 */
public class CategroyAdapter extends BaseExpandableListAdapter {
    Context mContext;
    ArrayList<CategoryGroupBean> mGroupList;
    ArrayList<ArrayList<CategoryChildBean>> mChildList;

    public CategroyAdapter(Context mContext, ArrayList<CategoryGroupBean> mGroupList, ArrayList<ArrayList<CategoryChildBean>> mChildList) {
        this.mContext = mContext;
        this.mGroupList = mGroupList;
        this.mChildList = mChildList;
    }

    @Override

    public int getGroupCount() {
        return mGroupList==null?0:mGroupList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return mChildList==null||mChildList.get(i)==null?0:mChildList.get(i).size();
    }

    @Override
    public CategoryGroupBean getGroup(int i) {
        return mGroupList.get(i);
    }

    @Override
    public CategoryChildBean getChild(int i, int i1) {
        return mChildList.get(i).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i1) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View layout, ViewGroup viewGroup) {
        GroupViewHolder holder=null;
        if (layout == null) {
            layout = LayoutInflater.from(mContext).inflate(R.layout.item_category_group, null);
            holder = new GroupViewHolder();
            holder.ivGroup = (ImageView) layout.findViewById(R.id.ivGroup);
            holder.ivSelect = (ImageView) layout.findViewById(R.id.ivSelect);
            holder.tvGroup = (TextView) layout.findViewById(R.id.tvGroup);
            layout.setTag(holder);
        } else {
            holder = (GroupViewHolder) layout.getTag();
        }
        CategoryGroupBean group = mGroupList.get(i);
        holder.tvGroup.setText(group.getName());
        ImageLoader.build()
                .url(I.SERVER_ROOT)
                .addParam(I.KEY_REQUEST,I.REQUEST_DOWNLOAD_CATEGORY_GROUP_IMAGE)
                .addParam("imageurl",group.getImageUrl())
                .width(40)
                .height(40)
                .imageView(holder.ivGroup)
                .defaultPicture(R.drawable.default_image)
                .listener(viewGroup)
                .showImage(mContext);
        if (b) {
            holder.ivSelect.setImageResource(R.drawable.expand_off);
        } else {
            holder.ivSelect.setImageResource(R.drawable.expand_on);
        }
        return layout;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View layout, ViewGroup viewGroup) {
        ChildViewHolder holder = null;
        if (layout == null) {
            layout = LayoutInflater.from(mContext).inflate(R.layout.item_category_child, null);
            holder = new ChildViewHolder();
            holder.ivChild = (ImageView) layout.findViewById(R.id.ivChild);
            holder.tvChild = (TextView) layout.findViewById(R.id.tvChild);
            layout.setTag(holder);
        } else {
            holder = (ChildViewHolder) layout.getTag();
        }
        final CategoryChildBean child = getChild(i, i1);
        holder.tvChild.setText(child.getName());
        ImageLoader.build()
                .url(I.SERVER_ROOT)
                .addParam(I.KEY_REQUEST,I.REQUEST_DOWNLOAD_CATEGORY_CHILD_IMAGE)
                .addParam("imageurl",child.getImageUrl())
                .width(25)
                .height(25)
                .imageView(holder.ivChild)
                .defaultPicture(R.drawable.default_image)
                .listener(viewGroup)
                .showImage(mContext);

        return layout;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
    class GroupViewHolder{
        ImageView ivGroup,ivSelect;
        TextView tvGroup;
    }
    class ChildViewHolder{
        ImageView ivChild;
        TextView tvChild;
    }
}
