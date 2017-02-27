package com.example.jiawei.wyvideodemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_base);

        if(!hasPermission(Manifest.permission.CAMERA)){
            requestPermission(Constants.CAMERA,Manifest.permission.CAMERA);
        }

        if(!hasPermission(Manifest.permission.RECORD_AUDIO)){
            requestPermission(Constants.RECORD_AUDIO,Manifest.permission.RECORD_AUDIO);
        }
    }

    //检查是否有权限
    public boolean hasPermission(String... permissions){
        for (String permission:permissions){
            if(ContextCompat.checkSelfPermission(this,permission)!= PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }
    //请求权限
    public void requestPermission(int code,String... permissions){
        ActivityCompat.requestPermissions(this,permissions,code);
    }

    //请求结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case Constants.WRITE:
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    saveLoginInfo();
                }else{//用户拒绝

                }
                break;
            case Constants.CAll:
                doCallphone();
                break;
            case Constants.CAMERA:
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
//                    saveLoginInfo();
                }else{//用户拒绝

                }
                break;
            case Constants.RECORD_AUDIO:
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
//                    saveLoginInfo();
            }else{//用户拒绝

            }
            break;
        }
    }
    //子类具体实现
    public void  saveLoginInfo(){

    }
    //子类具体实现
    public void doCallphone(){

    }
}
