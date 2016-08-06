package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;

import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.bean.CollectBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;

/**
 * Created by sks on 2016/8/6.
 */
public class DownloadCollectTask {
    private static final String TAG = DownloadCollectTask.class.getSimpleName();
    private String userName;
    private Context mContext;
    public DownloadCollectTask(Context mContext,String userName) {
        this.userName = userName;
        this.mContext = mContext;
    }
    //下载所有数据
    private int page_id=0;
    private int page_size=100;

    public void execute() {
        final OkHttpUtils2<String> utils = new OkHttpUtils2<>();
        utils.setRequestUrl(I.REQUEST_FIND_COLLECTS)
                .addParam(I.Collect.USER_NAME,userName)
                .addParam(I.PAGE_ID,String.valueOf(page_id))
                .addParam(I.PAGE_SIZE,String.valueOf(page_size))
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.e(TAG,"s==="+s);
                        Gson gson = new Gson();
                        CollectBean[] collectBeen = gson.fromJson(s, CollectBean[].class);
                        ArrayList<CollectBean> collectGoods = utils.array2List(collectBeen);
                        //保存用户收藏商品到全局变量
                        FuliCenterApplication.getInstance().setCollectGoods(collectGoods);
                        mContext.sendStickyBroadcast(new Intent("update_collect_list"));
                    }

                    @Override
                    public void onError(String error) {

                    }
                });

    }
}
