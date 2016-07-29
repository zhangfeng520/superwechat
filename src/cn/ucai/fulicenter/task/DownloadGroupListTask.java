package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.SuperWeChatApplication;
import cn.ucai.fulicenter.Utils;
import cn.ucai.fulicenter.bean.GroupAvatar;
import cn.ucai.fulicenter.bean.Result;
import cn.ucai.fulicenter.data.OkHttpUtils2;

/**
 * Created by sks on 2016/7/20.
 */
public class DownloadGroupListTask {
    private static final String TAG = DownloadGroupListTask.class.getSimpleName();
    String userName;
    Context mContext;

    public DownloadGroupListTask(Context context, String userName) {
        mContext = context;
        this.userName = userName;
    }
    public void execute(){
        final OkHttpUtils2<String> utils = new OkHttpUtils2<String>();
        utils.setRequestUrl(I.REQUEST_FIND_GROUP_BY_USER_NAME)
                .addParam(I.User.USER_NAME,userName)
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.e(TAG, "s="+s);
                        Log.e(TAG,"username===============" + userName);
                        Result result = Utils.getListResultFromJson(s, GroupAvatar.class);
                        Log.e(TAG, "result=" + result);
                            List<GroupAvatar> list = (List<GroupAvatar>) result.getRetData();
                            if (list!=null&&list.size()>0) {
                                Log.e(TAG, "list.size=" + list.size());
                                SuperWeChatApplication.getInstance().setGroupList(list);
                                mContext.sendStickyBroadcast(new Intent("update_group_list"));
                                for (GroupAvatar g : list) {
                                    SuperWeChatApplication.getInstance().getGroupMap().put(g.getMGroupHxid(), g);
                                }
                        }

                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "error=" + error);
                    }
                });
    }
}
