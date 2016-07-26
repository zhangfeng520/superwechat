package cn.ucai.superwechat.task;

import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.util.Log;

import java.util.List;
import java.util.Map;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.Utils;
import cn.ucai.superwechat.bean.GroupAvatar;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.bean.UserAvatar;
import cn.ucai.superwechat.data.OkHttpUtils2;

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
                        }

                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "error=" + error);
                    }
                });
    }
}
