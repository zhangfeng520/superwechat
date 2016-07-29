package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;
import java.util.Map;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.SuperWeChatApplication;
import cn.ucai.fulicenter.Utils;
import cn.ucai.fulicenter.bean.Result;
import cn.ucai.fulicenter.bean.UserAvatar;
import cn.ucai.fulicenter.data.OkHttpUtils2;

/**
 * Created by sks on 2016/7/20.
 */
public class DownloadContactListTask {
    private static final String TAG = DownloadContactListTask.class.getSimpleName();
    String userName;
    Context mContext;

    public DownloadContactListTask(Context context,String userName) {
        mContext = context;
        this.userName = userName;
    }
    public void execute(){
        final OkHttpUtils2<String> utils = new OkHttpUtils2<String>();
        utils.setRequestUrl(I.REQUEST_DOWNLOAD_CONTACT_ALL_LIST)
                .addParam(I.Contact.USER_NAME,userName)
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.e(TAG, "s="+s);
                        Result result = Utils.getListResultFromJson(s, UserAvatar.class);
                        Log.e(TAG, "result=" + result);
                        if (result != null && result.isRetMsg()) {
                            List<UserAvatar> list = (List<UserAvatar>) result.getRetData();
                            if (list!=null&&list.size()>0) {
                                Map<String, UserAvatar> userMap = SuperWeChatApplication.getInstance().getUserMap();
                                for(UserAvatar u:list){
                                    userMap.put(u.getMUserName(), u);
                                }
                                Log.e(TAG, "list.size=" + list.size());
                                SuperWeChatApplication.getInstance().setUserList(list);
                                mContext.sendStickyBroadcast(new Intent("update_contact_list"));
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
