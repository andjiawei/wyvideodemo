package com.example.jiawei.wyvideodemo;

import android.app.Application;
import android.text.TextUtils;

import com.example.jiawei.wyvideodemo.preference.Preferences;
import com.example.wyvideo.AVEngine;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.LoginInfo;


/**
 * Created by jiawei on 2017/2/23.
 */

public class NimApplication extends Application {

    private static final String TAG="NimApplication";

    @Override
    public void onCreate() {
        // ... your codes

        // SDK初始化（启动后台服务，若已经存在用户登录信息， SDK 将完成自动登录）
        NIMClient.init(this, loginInfo(), null);
        AVEngine.getInstance().init();
    }

    // 如果已经存在用户登录信息，返回LoginInfo，否则返回null即可
    private LoginInfo loginInfo() {
        String account = Preferences.getInstance(this).getUserAccount();
        String token = Preferences.getInstance(this).getUserToken();

        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(token)) {
//            DemoCache.setAccount(account.toLowerCase());
            return new LoginInfo(account, token);
        } else {
            return null;
        }
    }

}
