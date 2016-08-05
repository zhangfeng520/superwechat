package cn.ucai.fulicenter.Fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.FuliCenterMainActivity;
import cn.ucai.fulicenter.activity.LoginActivity;
import cn.ucai.fulicenter.activity.SettingActivity;
import cn.ucai.fulicenter.activity.SettingsFragment;
import cn.ucai.fulicenter.activity.UserProfileActivity;
import cn.ucai.fulicenter.utils.UserUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalCenterFragment extends Fragment {
    private static final String TAG = PersonalCenterFragment.class.getSimpleName();
    RecyclerView mrvContact;
    MyAdapter myAdapter;
    Context mContext;
    LinearLayoutManager mlayout;
    int action;
    private Handler handler;
    public PersonalCenterFragment() {
        // Required empty public constructor
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        FuliCenterMainActivity activity = (FuliCenterMainActivity) getActivity();
//        handler=activity.handler;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View layout = inflater.inflate(R.layout.fragment_personal_center, container, false);
        mContext = layout.getContext();
        initView(layout);
        action = getActivity().getIntent().getIntExtra("action", 0);
        Log.e(TAG, "action=" + action);
        return layout;
    }

    private void initView(View layout) {
        mrvContact = (RecyclerView) layout.findViewById(R.id.rv_personal_center);
        myAdapter = new MyAdapter(mContext);
        mlayout = new LinearLayoutManager(mContext);
        mrvContact.setAdapter(myAdapter);
        mrvContact.setLayoutManager(mlayout);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvSettings;
        ImageView ivAvatar;
        TextView tvUserName;
        public MyViewHolder(View itemView) {
            super(itemView);
            tvSettings = (TextView) itemView.findViewById(R.id.tvSettings);
            ivAvatar = (ImageView) itemView.findViewById(R.id.ivAvatar);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
        }
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        Context mContext;

        public MyAdapter(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            String username =getActivity().getIntent().getStringExtra("username");
            View layout = LayoutInflater.from(mContext).inflate(R.layout.item_personal_center, null);
            MyViewHolder holder = new MyViewHolder(layout);
            if (FuliCenterApplication.getInstance().getUser() != null) {
                UserUtils.setAppUserAvatar(mContext,FuliCenterApplication.getInstance().getUserName(),holder.ivAvatar);
                UserUtils.setCurrentAppUserNick(holder.tvUserName);
            } else if (username != null) {
                Log.e(TAG, "我也来了！！！");
                UserUtils.setAppUserAvatar(mContext,username,holder.ivAvatar);
                UserUtils.setAppUserNick(username,holder.tvUserName);
            }
            holder.ivAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(mContext,UserProfileActivity.class));
                }
            });
            holder.tvUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(mContext,UserProfileActivity.class));
                }
            });
            holder.tvSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        startActivity(new Intent(mContext, SettingActivity.class).putExtra("action",action));
//                    handler.sendEmptyMessage(100);
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 1;
        }
    }


}
