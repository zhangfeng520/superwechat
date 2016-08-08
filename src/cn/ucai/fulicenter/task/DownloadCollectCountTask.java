package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;

import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;

/**
 * Created by sks on 2016/8/6.
 */
public class DownloadCollectCountTask {
    private final static String TAG = DownloadCollectCountTask.class.getSimpleName();
    private String userName;
    Context mContext;

    public DownloadCollectCountTask(Context mContext, String userName) {
        this.mContext = mContext;
        this.userName = userName;
    }

    public void execute() {
        final OkHttpUtils2<String> utils2 = new OkHttpUtils2<>();
        utils2.setRequestUrl(I.REQUEST_FIND_COLLECT_COUNT)
                .addParam(I.Collect.USER_NAME,userName)
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String ss) {
                        Log.e(TAG, "ss=" + ss);
                        Gson gson = new Gson();
                        MessageBean msg = gson.fromJson(ss, MessageBean.class);
                        Log.e(TAG, "msg=" + msg);
                        if (msg.isSuccess()) {
                            int count = Integer.parseInt(msg.getMsg());
                            FuliCenterApplication.getInstance().setCollectCount(count);
                            mContext.sendStickyBroadcast(new Intent("update_collect_count_list"));
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
    }
}
