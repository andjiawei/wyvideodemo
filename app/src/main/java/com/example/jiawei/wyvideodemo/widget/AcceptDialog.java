package com.example.jiawei.wyvideodemo.widget;


import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.example.jiawei.wyvideodemo.R;

/**
 * Created by jiawei on 2017/2/24.
 */

public class AcceptDialog extends Dialog implements View.OnClickListener {

    public AcceptDialog(Context context) {
        super(context);
        init(context);
    }

    public AcceptDialog(Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    protected AcceptDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_accept, null);
         view.findViewById(R.id.accept).setOnClickListener(this);
        view.findViewById(R.id.reject).setOnClickListener(this);

        this.setContentView(view);
        this.setCancelable(true);
        this.setCanceledOnTouchOutside(false);

//        DisplayMetrics metrics = new DisplayMetrics();
//        getWindow().getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
//        layoutParams.width = (int) (metrics.widthPixels * 0.72f);
//        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        layoutParams.gravity = Gravity.CENTER;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reject:
                reject();
                break;
            case R.id.accept:
                accept();
                break;
        }
    }

    private void reject() {
        if(mListener!=null){
            mListener.reject();
        }
    }

    private void accept() {
        if(mListener!=null){
            mListener.accept();
        }
    }

    private AccetpDialogListener mListener;
    public void setOnAccetpDialogListener(AccetpDialogListener listener){
        mListener=listener;
    }
    public interface AccetpDialogListener{
        void accept();
        void reject();
    }
}
