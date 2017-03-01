package com.example.jiawei.wyvideodemo.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by jiawei on 2017/3/1.
 */

public class Utils {
    /**
     * 隐藏系统键盘
     *
     * @param a
     * @return
     */
    public static boolean hideSoftInput(Activity a) {
        try {
            View view = a.getCurrentFocus();
            return view == null || ((InputMethodManager) a.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 显示系统键盘
     *
     * @param editText
     * @param context
     */
    public static void showSoftInput(final EditText editText, final Context context) {
        editText.requestFocus();
        editText.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, 0);
            }
        }, 100);
    }
}
