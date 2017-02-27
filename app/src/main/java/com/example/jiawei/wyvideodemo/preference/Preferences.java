package com.example.jiawei.wyvideodemo.preference;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
    private static final String KEY_USER_ACCOUNT = "account";
    private static final String KEY_USER_TOKEN = "token";
    private static final String SHARE_DATA_FILE_NAME = "Demo";
    private static Preferences instance;
    public static SharedPreferences mSharedPreferences;

    private Preferences(Context context) {
        mSharedPreferences = context.getSharedPreferences(SHARE_DATA_FILE_NAME, Context.MODE_PRIVATE);
    }

    public synchronized static Preferences getInstance(Context context) {
        if (instance == null) {
            instance = new Preferences(context);
        }
        return instance;
    }

    public void saveUserAccount(String account) {
        saveString(KEY_USER_ACCOUNT, account);
    }

    public  String getUserAccount() {
        return getString(KEY_USER_ACCOUNT);
    }

    public  void saveUserToken(String token) {
        saveString(KEY_USER_TOKEN, token);
    }

    public  String getUserToken() {
        return getString(KEY_USER_TOKEN);
    }

    private  void saveString(String key, String value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private  String getString(String key) {
        return mSharedPreferences.getString(key, "");
    }
}
