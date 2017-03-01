package com.example.jiawei.wyvideodemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.example.jiawei.wyvideodemo.preference.Preferences;
import com.example.jiawei.wyvideodemo.widget.AcceptDialog;
import com.example.wyvideo.AVEngine;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.avchat.AVChatCallback;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.netease.nimlib.sdk.avchat.constant.AVChatVideoScalingType;
import com.netease.nimlib.sdk.avchat.model.AVChatData;
import com.netease.nimlib.sdk.avchat.model.AVChatVideoRender;

public class LoginActivity extends BaseActivity implements AVEngine.AVEngineListener, AcceptDialog.AccetpDialogListener {

    private EditText loginAccountEdit;
    private EditText account2;
    private String account;
    private Button loginButton;
    private static final String TAG = "LoginActivity";
    private LoginInfo mLoginInfo;
    private AVEngine avEngine;
    private Context context;
    private AVChatVideoRender localVideoView;
    private FrameLayout largeFl;
    private FrameLayout smallFl;
    private View layoutVideo;
    private View layoutLogin;
    private AcceptDialog dialog;
    private AVChatVideoRender remoteVideoView;
    private AVChatData remoteChatData;
    private Button btnChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public);
        context = this;
        avEngine = AVEngine.getInstance();
        initListener();
        loginAccountEdit = findView(R.id.edit_login_account);
        findView(R.id.call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call();
            }
        });
        account2 = findView(R.id.account2);
        loginButton = findView(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        largeFl = findView(R.id.largeFL);
        smallFl = findView(R.id.smallFL);
        layoutVideo = findView(R.id.layout_video);
        layoutLogin = findView(R.id.layout_login);
        btnChat = (Button) findViewById(R.id.chat);
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,MessageChatActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initListener() {
        //收到视频邀请的回调
        avEngine.setOnAVEngineListener(this);
    }

    private void call() {
        avEngine.call(account2.getText().toString().trim(), AVChatType.VIDEO, null, null, new AVChatCallback<AVChatData>() {
            @Override
            public void onSuccess(final AVChatData avChatData) {
                Log.e(TAG, "onSuccess: " + "呼叫成功");
            }

            @Override
            public void onFailed(int i) {
                Log.e(TAG, "onSuccess: " + "呼叫失败");

            }

            @Override
            public void onException(Throwable throwable) {
                Log.e(TAG, "onSuccess: " + "呼叫异常");
            }
        });
    }

    private void addIntoSmallSizePreviewLayout(SurfaceView surfaceView) {
        if (surfaceView.getParent() != null) {
            ((ViewGroup) surfaceView.getParent()).removeView(surfaceView);
        }
        smallFl.addView(surfaceView);
        surfaceView.setZOrderMediaOverlay(true);
        layoutVideo.setVisibility(View.VISIBLE);
        layoutLogin.setVisibility(View.GONE);
    }

    private void login() {
        account = loginAccountEdit.getText().toString().trim();
        AbortableFuture<LoginInfo> loginRequest = NIMClient.getService(AuthService.class).login(new LoginInfo(account, "123456"));
        loginRequest.setCallback(new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo loginInfo) {
                Log.e("login", "onSuccess: 登陆成功");
                mLoginInfo = loginInfo;
                if (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    saveLoginInfo(loginInfo.getAccount(), loginInfo.getToken());
                } else {
                    requestPermission(Constants.WRITE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
            }

            @Override
            public void onFailed(int code) {
                Log.e("login", "onFailed: 登陆失败");
            }

            @Override
            public void onException(Throwable exception) {
                Log.e("login", "onSuccess: 登陆异常" + exception);
            }
        });
    }

    @Override
    public void saveLoginInfo() {
        if (mLoginInfo != null) {
            saveLoginInfo(mLoginInfo.getAccount(), mLoginInfo.getToken());
        }
    }

    private void saveLoginInfo(final String account, final String token) {
        Preferences.getInstance(this).saveUserAccount(account);
        Preferences.getInstance(this).saveUserToken(token);
    }

    protected <T extends View> T findView(int resId) {
        return (T) (findViewById(resId));
    }

    @Override
    public void receive(AVChatData chatData) {
        remoteChatData =chatData;
        dialog = new AcceptDialog(context);
        dialog.show();
        dialog.setOnAccetpDialogListener(this);
    }

    @Override
    public void showLocal() {
        Log.e(TAG, "showLocal: 回调显示本地视频"+account );
        AVChatManager.getInstance().setupVideoRender(account,  localVideoView = new AVChatVideoRender(context), false, AVChatVideoScalingType.SCALE_ASPECT_BALANCED);
        addIntoSmallSizePreviewLayout(localVideoView);
    }

    //onUserJoined回调
    @Override
    public void showRemoteView(String s) {
        Log.e(TAG, "showRemoteView: 回调显示远端视频" +s);

        AVChatManager.getInstance().setupVideoRender(s, remoteVideoView = new AVChatVideoRender(context), false, AVChatVideoScalingType.SCALE_ASPECT_BALANCED);
        addIntoBigSizePreviewLayout(remoteVideoView);
    }

    private void addIntoBigSizePreviewLayout(AVChatVideoRender surfaceView) {
        if (surfaceView.getParent() != null) {
            ((ViewGroup) surfaceView.getParent()).removeView(surfaceView);
        }
        largeFl.addView(surfaceView);
        surfaceView.setZOrderMediaOverlay(false);
        layoutVideo.setVisibility(View.VISIBLE);
        layoutLogin.setVisibility(View.GONE);
    }

    @Override
    public void accept() {
        avEngine.accept(null, new AVChatCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
//                AVChatManager.getInstance().setupVideoRender(account, localVideoView=new AVChatVideoRender(context), false, AVChatVideoScalingType.SCALE_ASPECT_BALANCED);
//                addIntoSmallSizePreviewLayout(localVideoView);
//                AVChatManager.getInstance().setupVideoRender(remoteChatData.getAccount(), remoteVideoView, false, AVChatVideoScalingType.SCALE_ASPECT_BALANCED);
//                addIntoBigSizePreviewLayout(remoteVideoView);

                dialog.dismiss();
            }

            @Override
            public void onFailed(int i) {

            }

            @Override
            public void onException(Throwable throwable) {

            }
        });
    }

    @Override
    public void reject() {

    }
}
