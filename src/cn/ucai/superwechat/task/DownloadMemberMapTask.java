package cn.ucai.superwechat.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.Utils;
import cn.ucai.superwechat.bean.MemberUserAvatar;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.bean.UserAvatar;
import cn.ucai.superwechat.data.OkHttpUtils2;

/**
 * Created by sks on 2016/7/20.
 */
public class DownloadMemberMapTask {
    private static final String TAG = DownloadMemberMapTask.class.getSimpleName();
    String hxid;
    Context mContext;

    public DownloadMemberMapTask(Context context, String hxid) {
        mContext = context;
        this.hxid = hxid;
    }
    public void execute(){
        final OkHttpUtils2<String> utils = new OkHttpUtils2<String>();
        utils.setRequestUrl(I.REQUEST_DOWNLOAD_GROUP_MEMBERS_BY_HXID)
                .addParam(I.Contact.USER_NAME,hxid)
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.e(TAG, "s="+s);
                        Result result = Utils.getListResultFromJson(s, UserAvatar.class);
                        Log.e(TAG, "result=" + result);
                        if (result != null && result.isRetMsg()) {
                            List<MemberUserAvatar> list = (List<MemberUserAvatar>) result.getRetData();
                            if (list!=null&&list.size()>0) {
                                Map<String, HashMap<String, MemberUserAvatar>> memberMap = SuperWeChatApplication.getInstance().getMemberMap();
                                if (!memberMap.containsKey(hxid)) {
                                    memberMap.put(hxid, new HashMap<String, MemberUserAvatar>());
                                }
                                HashMap<String, MemberUserAvatar> hxidMembers = memberMap.get(hxid);

                                for(MemberUserAvatar u:list){
                                    hxidMembers.put(u.getMUserName(), u);
                                }
                                mContext.sendStickyBroadcast(new Intent("update_member_list"));
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
