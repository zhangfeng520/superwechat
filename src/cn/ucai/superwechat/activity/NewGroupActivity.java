/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.ucai.superwechat.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.Utils;
import cn.ucai.superwechat.bean.GroupAvatar;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.data.OkHttpUtils2;
import cn.ucai.superwechat.listener.OnSetAvatarListener;

import com.easemob.exceptions.EaseMobException;

import java.io.File;

public class NewGroupActivity extends BaseActivity {
    public static final int CREATE_GROUP_ICON = 100;
    private static final String TAG = NewGroupActivity.class.getSimpleName();
    private EditText groupNameEditText;
    private ProgressDialog progressDialog;
    private EditText introductionEditText;
    private CheckBox checkBox;
    private CheckBox memberCheckbox;
    private LinearLayout openInviteContainer;
    Context mContext;
    ImageView ivAvatar;
    OnSetAvatarListener mOnSetAvatarListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);
        mContext = this;
        initView();
        setListener();


    }

    private void setListener() {
        setSaveGroupClickListener();
        setOnCheckchangeListener();
        setGroupIconClickListener();
    }


    private void initView() {
        groupNameEditText = (EditText) findViewById(R.id.edit_group_name);
        introductionEditText = (EditText) findViewById(R.id.edit_group_introduction);
        checkBox = (CheckBox) findViewById(R.id.cb_public);
        memberCheckbox = (CheckBox) findViewById(R.id.cb_member_inviter);
        openInviteContainer = (LinearLayout) findViewById(R.id.ll_open_invite);
        ivAvatar = (ImageView) findViewById(R.id.iv_group_avatar);
    }

    /**
     * @param
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == CREATE_GROUP_ICON) {
            setProgressDialog();
            //新建群组
            createEMNewGroup(data);
        } else {
            mOnSetAvatarListener.setAvatar(requestCode, data, ivAvatar);
        }
    }

    public void setProgressDialog() {
        String st1 = getResources().getString(R.string.Is_to_create_a_group_chat);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(st1);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    public void createEMNewGroup( final Intent data) {
        final String st2 = getResources().getString(R.string.Failed_to_create_groups);
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 调用sdk创建群组方法
                String groupName = groupNameEditText.getText().toString().trim();
                String desc = introductionEditText.getText().toString();
                String[] members = data.getStringArrayExtra("newmembers");
                EMGroup group;
                try {
                    if (checkBox.isChecked()) {
                        //创建公开群，此种方式创建的群，可以自由加入
                        //创建公开群，此种方式创建的群，用户需要申请，等群主同意后才能加入此群
                       group= EMGroupManager.getInstance().createPublicGroup(groupName, desc, members, true, 200);
                    } else {
                        //创建不公开群
                        group=EMGroupManager.getInstance().createPrivateGroup(groupName, desc, members, memberCheckbox.isChecked(), 200);
                    }
                    Log.e(TAG, group.getGroupId());
                    createAppGroup(group.getGroupId(), groupName, desc, members);

                } catch (final EaseMobException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(NewGroupActivity.this, st2 + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }
        }).start();


    }

    private void createAppGroup(final String groupId, String groupName, String desc, final String[] members) {
        File file = new File(OnSetAvatarListener.getAvatarPath(NewGroupActivity.this,I.AVATAR_TYPE_GROUP_PATH),avatarName+I.AVATAR_SUFFIX_JPG);
        String owner = SuperWeChatApplication.getInstance().getUserName();
        boolean isPublic=checkBox.isChecked();
        boolean invite = !isPublic;
        final OkHttpUtils2<String> utils = new OkHttpUtils2<String>();
        utils.setRequestUrl(I.REQUEST_CREATE_GROUP)
                .addParam(I.Group.HX_ID,groupId)
                .addParam(I.Group.DESCRIPTION,desc)
                .addParam(I.Group.NAME,groupName)
                .addParam(I.Group.OWNER,owner)
                .addParam(I.Group.IS_PUBLIC,String.valueOf(isPublic))
                .addParam(I.Group.ALLOW_INVITES,String.valueOf(invite))
                .addFile(file)
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.e(TAG, "s=" + s);
                        Result result = Utils.getResultFromJson(s, GroupAvatar.class);
                        Log.e(TAG, "result=" + result);
                        if (result != null && result.isRetMsg()) {
                            final GroupAvatar groupAvatar = (GroupAvatar) result.getRetData();
                            if (members != null && members.length > 0) {
                                createGroupMembers(groupId, members);
                            }else {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        progressDialog.dismiss();
                                        setResult(RESULT_OK);
                                        finish();
                                        Toast.makeText(NewGroupActivity.this, "创建群组成功", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            Log.e(TAG, "fail to create..." + result);
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "error..." + error);
                        Toast.makeText(NewGroupActivity.this, "创建失败", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });

    }

    private void createGroupMembers(String groupId, String[] members) {
        String member = "";
        for (String m : members) {
            member += m + ",";
        }
        member=member.substring(0, member.length() - 1);
        Log.e(TAG, member);
        final OkHttpUtils2<String> utils2 = new OkHttpUtils2<String>();
        utils2.setRequestUrl(I.REQUEST_ADD_GROUP_MEMBERS)
                .addParam(I.Member.USER_NAME,member)
                .addParam(I.Member.GROUP_HX_ID,groupId)
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.e(TAG, "s=" + s);
                        Result result = Utils.getResultFromJson(s, GroupAvatar.class);
                        Log.e(TAG, "result=" + result);
                        progressDialog.dismiss();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                progressDialog.dismiss();
                                setResult(RESULT_OK);
                                finish();
                                Toast.makeText(NewGroupActivity.this, "创建群组成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "error=" + error);
                        Toast.makeText(NewGroupActivity.this, "添加联系人失败", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
    }

    String avatarName;

    public String getAvatarName() {
        avatarName = System.currentTimeMillis() + "";
        return avatarName;
    }

    public void setSaveGroupClickListener() {
        findViewById(R.id.btnSaveGroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str6 = getResources().getString(R.string.Group_name_cannot_be_empty);
                String name = groupNameEditText.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    Intent intent = new Intent(NewGroupActivity.this, AlertDialog.class);
                    intent.putExtra("msg", str6);
                    startActivity(intent);
                } else {
                    // 进通讯录选人
                    startActivityForResult(new Intent(NewGroupActivity.this, GroupPickContactsActivity.class).putExtra("groupName", name), CREATE_GROUP_ICON);
                }
            }
        });
    }

    private void setOnCheckchangeListener() {
        checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    openInviteContainer.setVisibility(View.INVISIBLE);
                } else {
                    openInviteContainer.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setGroupIconClickListener() {
        findViewById(R.id.Layout_group_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnSetAvatarListener = new OnSetAvatarListener(NewGroupActivity.this, R.id.Layout_new_group, getAvatarName(), I.AVATAR_TYPE_GROUP_PATH);
            }
        });
    }

    public void back(View view) {
        finish();
    }
}
