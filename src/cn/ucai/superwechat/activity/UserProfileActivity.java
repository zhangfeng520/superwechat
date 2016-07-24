package cn.ucai.superwechat.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMValueCallBack;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.Utils;
import cn.ucai.superwechat.applib.controller.HXSDKHelper;
import com.easemob.chat.EMChatManager;
import cn.ucai.superwechat.DemoHXSDKHelper;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.bean.UserAvatar;
import cn.ucai.superwechat.data.OkHttpUtils2;
import cn.ucai.superwechat.db.UserDao;
import cn.ucai.superwechat.domain.User;
import cn.ucai.superwechat.listener.OnSetAvatarListener;
import cn.ucai.superwechat.utils.UserUtils;
import com.squareup.picasso.Picasso;

public class UserProfileActivity extends BaseActivity implements OnClickListener{
	private static final String TAG = UserProfileActivity.class.getSimpleName();
	private static final int REQUESTCODE_PICK = 1;
	private static final int REQUESTCODE_CUTTING = 2;
	private ImageView headAvatar;
	private ImageView headPhotoUpdate;
	private ImageView iconRightArrow;
	private TextView tvNickName;
	private TextView tvUsername;
	private ProgressDialog dialog;
	private RelativeLayout rlNickName;
	private String btnname;
	OnSetAvatarListener mOnSetAvatarListener;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_user_profile);
		initView();
		initListener();
	}

	private void initView() {
		headAvatar = (ImageView) findViewById(R.id.user_head_avatar);
		headPhotoUpdate = (ImageView) findViewById(R.id.user_head_headphoto_update);
		tvUsername = (TextView) findViewById(R.id.user_username);
		tvNickName = (TextView) findViewById(R.id.user_nickname);
		rlNickName = (RelativeLayout) findViewById(R.id.rl_nickname);
		iconRightArrow = (ImageView) findViewById(R.id.ic_right_arrow);
	}
	
	private void initListener() {
		Intent intent = getIntent();
		String username = intent.getStringExtra("username");
		btnname = username;
		boolean enableUpdate = intent.getBooleanExtra("setting", false);

		if (enableUpdate) {
//			UserAvatar user = SuperWeChatApplication.getInstance().getUser();
//			UserUtils.setAppUserAvatar(this,user.getMUserName(),headAvatar);
//			Log.e(TAG, "ksdfsdfdsfsd=" + user.toString());
//			UserUtils.setAppUserNick(user.getMUserName(),tvNickName);
			headPhotoUpdate.setVisibility(View.VISIBLE);
			iconRightArrow.setVisibility(View.VISIBLE);
			rlNickName.setOnClickListener(this);
			headAvatar.setOnClickListener(this);


		} else {
			headPhotoUpdate.setVisibility(View.GONE);
			iconRightArrow.setVisibility(View.INVISIBLE);


		}
		if (username == null) {
//			tvUsername.setText(EMChatManager.getInstance().getCurrentUser());
//			UserUtils.setCurrentUserNick(tvNickName);
//			UserUtils.setCurrentUserAvatar(this, headAvatar);
			tvUsername.setText(SuperWeChatApplication.getInstance().getUserName());
			findViewById(R.id.btnSendMessage).setVisibility(View.GONE);
			if (tvNickName != null) {
				UserUtils.setCurrentAppUserNick(tvNickName);
			}
			UserUtils.setAppUserAvatar(this,SuperWeChatApplication.getInstance().getUserName(),headAvatar);

		} else if (username.equals(SuperWeChatApplication.getInstance().getUserName())){
//			tvUsername.setText(EMChatManager.getInstance().getCurrentUser());
//			UserUtils.setCurrentUserNick(tvNickName);
//			UserUtils.setCurrentUserAvatar(this, headAvatar);
			findViewById(R.id.btnSendMessage).setVisibility(View.GONE);
			tvUsername.setText(SuperWeChatApplication.getInstance().getUserName());
			UserUtils.setCurrentAppUserNick(tvNickName);
			UserUtils.setAppUserAvatar(this,SuperWeChatApplication.getInstance().getUserName(),headAvatar);

		} else {
			tvUsername.setText(username);
//			UserUtils.setUserNick(username, tvNickName);
//			UserUtils.setUserAvatar(this, username, headAvatar);
			//修改联系人个人资料的头像和昵称
			UserUtils.setAppUserNick(username,tvNickName);
			UserUtils.setAppUserAvatar(this,username,headAvatar);
//			asyncFetchUserInfo(username);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			//更新头像
		case R.id.user_head_avatar:
			mOnSetAvatarListener = new OnSetAvatarListener(UserProfileActivity.this, R.id.Layout_user_profile, getAvatarName(), I.AVATAR_TYPE_USER_PATH);
//			uploadHeadPhoto();
			break;
		case R.id.rl_nickname:
			final EditText editText = new EditText(this);
			new AlertDialog.Builder(this).setTitle(R.string.setting_nickname).setIcon(android.R.drawable.ic_dialog_info).setView(editText)
					.setPositiveButton(R.string.dl_ok, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							String nickString = editText.getText().toString();
							if (TextUtils.isEmpty(nickString)) {
								Toast.makeText(UserProfileActivity.this, getString(R.string.toast_nick_not_isnull), Toast.LENGTH_SHORT).show();
								return;
							}
//							updateRemoteNick(nickString);
							//个人资料页面更新用户昵称
							updateUserNick(nickString);
						}
					}).setNegativeButton(R.string.dl_cancel, null).show();
			break;
		default:
			break;
		}

	}

	
	public void asyncFetchUserInfo(String username){
		((DemoHXSDKHelper)HXSDKHelper.getInstance()).getUserProfileManager().asyncGetUserInfo(username, new EMValueCallBack<User>() {
			
			@Override
			public void onSuccess(User user) {
				if (user != null) {
					tvNickName.setText(user.getNick());
					if(!TextUtils.isEmpty(user.getAvatar())){
						 Picasso.with(UserProfileActivity.this).load(user.getAvatar()).placeholder(R.drawable.default_avatar).into(headAvatar);
					}else{
						Picasso.with(UserProfileActivity.this).load(R.drawable.default_avatar).into(headAvatar);
					}
					UserUtils.saveUserInfo(user);
				}
			}
			
			@Override
			public void onError(int error, String errorMsg) {
			}
		});
	}

	private void uploadHeadPhoto() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle(R.string.dl_title_upload_photo);
		builder.setItems(new String[] { getString(R.string.dl_msg_take_photo), getString(R.string.dl_msg_local_upload) },
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						switch (which) {
						case 0:
							Toast.makeText(UserProfileActivity.this, getString(R.string.toast_no_support),
									Toast.LENGTH_SHORT).show();
							break;
						case 1:
							Intent pickIntent = new Intent(Intent.ACTION_PICK,null);
							pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
							startActivityForResult(pickIntent, REQUESTCODE_PICK);
							break;
						default:
							break;
						}
					}
				});
		builder.create().show();

	}
	//更新用户头像(待完善）
	private void updateUserAvatar() {
		final OkHttpUtils2<Result> utils = new OkHttpUtils2<Result>();
		File file = new File(OnSetAvatarListener.getAvatarPath(UserProfileActivity.this,I.AVATAR_TYPE_USER_PATH),avatarName+I.AVATAR_SUFFIX_JPG);
		Log.e(TAG, "1111111111111" + file.toString());
		utils.setRequestUrl(I.REQUEST_UPLOAD_AVATAR)
				.addParam(I.AVATAR_TYPE,"user_avatar")
				.addParam(I.NAME_OR_HXID,SuperWeChatApplication.getInstance().getUser().getMUserName())
				.addFile(file)
				.targetClass(Result.class)
				.execute(new OkHttpUtils2.OnCompleteListener<Result>() {
					@Override
					public void onSuccess(Result result) {
						Toast.makeText(UserProfileActivity.this, "更新头像成功！", Toast.LENGTH_SHORT).show();
						Log.e(TAG, "222222222222");
//						SuperWeChatApplication.getInstance().getUser().setMAvatarLastUpdateTime(System.currentTimeMillis() + "");
					}
					@Override
					public void onError(String error) {

					}
				});

	}

	//更新用户昵称
	private void updateUserNick(final String nickname) {
		final OkHttpUtils2<String> utils2 = new OkHttpUtils2<String>();
		utils2.setRequestUrl(I.REQUEST_UPDATE_USER_NICK)
				.addParam(I.User.USER_NAME,SuperWeChatApplication.getInstance().getUserName())
				.addParam(I.User.NICK,nickname)
				.targetClass(String.class)
				.execute(new OkHttpUtils2.OnCompleteListener<String>() {
					@Override
					public void onSuccess(String s) {
						Result result = Utils.getResultFromJson(s, UserAvatar.class);
						UserAvatar  user = (UserAvatar) result.getRetData();
						Toast.makeText(UserProfileActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
						//同步到远端数据
						tvNickName.setText(nickname);
						SuperWeChatApplication.currentUserNick = user.getMUserNick();
						SuperWeChatApplication.getInstance().setUser(user);
					}

					@Override
					public void onError(String error) {

					}
				});

	}
	private void updateRemoteNick(final String nickName) {
		dialog = ProgressDialog.show(this, getString(R.string.dl_update_nick), getString(R.string.dl_waiting));
		new Thread(new Runnable() {

			@Override
			public void run() {
				boolean updatenick = ((DemoHXSDKHelper)HXSDKHelper.getInstance()).getUserProfileManager().updateParseNickName(nickName);
				if (UserProfileActivity.this.isFinishing()) {
					return;
				}
				if (!updatenick) {
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatenick_fail), Toast.LENGTH_SHORT)
									.show();
							dialog.dismiss();
						}
					});
				} else {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatenick_success), Toast.LENGTH_SHORT)
									.show();
							tvNickName.setText(nickName);
						}
					});
				}
			}
		}).start();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		switch (requestCode) {
//		case REQUESTCODE_PICK:
//			if (data == null || data.getData() == null) {
//				return;
//			}
//			startPhotoZoom(data.getData());
//			break;
//		case REQUESTCODE_CUTTING:
//			if (data != null) {
//				setPicToView(data);
//			}
//
//			break;
//		default:
//
//			break;
//		}
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode!=RESULT_OK){
			return;
		}
		mOnSetAvatarListener.setAvatar(requestCode,data,headAvatar);
		if (requestCode == OnSetAvatarListener.REQUEST_CROP_PHOTO) {
			updateUserAvatar();
		}
	}

	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", true);
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 300);
		intent.putExtra("outputY", 300);
		intent.putExtra("return-data", true);
		intent.putExtra("noFaceDetection", true);
		startActivityForResult(intent, REQUESTCODE_CUTTING);
	}
	
	/**
	 * save the picture data
	 * 
	 * @param picdata
	 */
	private void setPicToView(Intent picdata) {
		Bundle extras = picdata.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			Drawable drawable = new BitmapDrawable(getResources(), photo);
			headAvatar.setImageDrawable(drawable);
			uploadUserAvatar(Bitmap2Bytes(photo));
		}

	}
	
	private void uploadUserAvatar(final byte[] data) {
		dialog = ProgressDialog.show(this, getString(R.string.dl_update_photo), getString(R.string.dl_waiting));
		new Thread(new Runnable() {

			@Override
			public void run() {
				final String avatarUrl = ((DemoHXSDKHelper)HXSDKHelper.getInstance()).getUserProfileManager().uploadUserAvatar(data);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						dialog.dismiss();
						if (avatarUrl != null) {
							Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatephoto_success),
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatephoto_fail),
									Toast.LENGTH_SHORT).show();
						}

					}
				});

			}
		}).start();

		dialog.show();
	}
	
	
	public byte[] Bitmap2Bytes(Bitmap bm){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}


	public void onSentMessage(View view) {
		Toast.makeText(UserProfileActivity.this, "终于点到我了", Toast.LENGTH_SHORT).show();
		startActivity(new Intent(UserProfileActivity.this,ChatActivity.class).putExtra("userId",btnname));

//		Intent intent = new Intent();
//		intent.putExtra("chatType", 1);
//		startActivity(intent);
	}

	String avatarName;
	public String getAvatarName() {
		avatarName = System.currentTimeMillis() + "";
		return avatarName;
	}
}
