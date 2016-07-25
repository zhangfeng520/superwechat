/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import com.easemob.chat.EMGroupManager;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.Utils;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.data.OkHttpUtils2;
import cn.ucai.superwechat.listener.OnSetAvatarListener;

import com.easemob.exceptions.EaseMobException;

import java.io.File;

public class NewGroupActivity extends BaseActivity {
	public static final int CREATE_GROUP_ICON=100;
	private static final String TAG = NewGroupActivity.class.getSimpleName();
	private EditText groupNameEditText;
	private ProgressDialog progressDialog;
	private EditText introductionEditText;
	private CheckBox checkBox;
	private CheckBox memberCheckbox;
	private LinearLayout openInviteContainer;
	Context mContext;
	ImageView  ivAvatar;
	OnSetAvatarListener mOnSetAvatarListener;




	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_group);
		mContext=this;
		initView();
		setListener();


	}

	private void setListener() {
		setSaveGroupClickListener();
		setOnCheckchangeListener();
		setGroupIconClickListener();
	}

	private void setGroupIconClickListener() {
		findViewById(R.id.Layout_group_icon).setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			mOnSetAvatarListener = new OnSetAvatarListener(NewGroupActivity.this, R.id.Layout_new_group, getAvatarName(), I.AVATAR_TYPE_GROUP_PATH);
		}
	});
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
	 * @param v
	 */
	int count1;
	int count2;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) {
			return;
		}
		if (requestCode == CREATE_GROUP_ICON) {
			setProgressDialog();
			//新建群组
			//1.创建环信的群组
			//2.创建远端群组，上传头像
			//3.添加群组成员到远端
			createNewGroup(data);
		} else {
			mOnSetAvatarListener.setAvatar(requestCode,data,ivAvatar);
		}

	}
	String avatarName;
	public String getAvatarName() {
		avatarName = System.currentTimeMillis() + "";
		return avatarName;
	}

	private void updateGroupAvatar() {
		final OkHttpUtils2<Result> utils = new OkHttpUtils2<Result>();
		File file = new File(OnSetAvatarListener.getAvatarPath(NewGroupActivity.this, I.AVATAR_TYPE_GROUP_PATH),avatarName+I.AVATAR_SUFFIX_JPG);
		Log.e(TAG, "1111111111111" + file.toString());
		utils.setRequestUrl(I.REQUEST_DOWNLOAD_AVATAR)
				.addParam(I.AVATAR_TYPE,"group_icon")
				.addParam(I.NAME_OR_HXID, SuperWeChatApplication.getInstance().getUser().getMUserName())
				.addFile(file)
				.targetClass(Result.class)
				.execute(new OkHttpUtils2.OnCompleteListener<Result>() {
					@Override
					public void onSuccess(Result result) {
						Toast.makeText(NewGroupActivity.this, "更新头像成功！", Toast.LENGTH_SHORT).show();
						Log.e(TAG, "222222222222");
					}
					@Override
					public void onError(String error) {

					}
				});

	}

	public void back(View view) {
		finish();
	}
	public void setProgressDialog() {
		String st1 = getResources().getString(R.string.Is_to_create_a_group_chat);
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(st1);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();
	}

	public void createNewGroup( final Intent data) {
		final String st2 = getResources().getString(R.string.Failed_to_create_groups);
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 调用sdk创建群组方法
				String groupName = groupNameEditText.getText().toString().trim();
				String desc = introductionEditText.getText().toString();
				String[] members = data.getStringArrayExtra("newmembers");
				try {
					if(checkBox.isChecked()){
						count1=1;
						//创建公开群，此种方式创建的群，可以自由加入
						//创建公开群，此种方式创建的群，用户需要申请，等群主同意后才能加入此群
						EMGroupManager.getInstance().createPublicGroup(groupName, desc, members, true,200);
					}else{
						count1=0;
						//创建不公开群
						EMGroupManager.getInstance().createPrivateGroup(groupName, desc, members, memberCheckbox.isChecked(),200);
					}
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							setResult(RESULT_OK);
							finish();
						}
					});
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
				if(isChecked){
					openInviteContainer.setVisibility(View.INVISIBLE);
					count2=1;
				}else{
					openInviteContainer.setVisibility(View.VISIBLE);
					count2=0;
				}
			}
		});
	}
}
