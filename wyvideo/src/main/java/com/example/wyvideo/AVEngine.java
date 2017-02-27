package com.example.wyvideo;

import android.util.Log;

import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.avchat.AVChatCallback;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.AVChatStateObserver;
import com.netease.nimlib.sdk.avchat.constant.AVChatEventType;
import com.netease.nimlib.sdk.avchat.constant.AVChatTimeOutEvent;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.netease.nimlib.sdk.avchat.model.AVChatAudioFrame;
import com.netease.nimlib.sdk.avchat.model.AVChatCalleeAckEvent;
import com.netease.nimlib.sdk.avchat.model.AVChatCommonEvent;
import com.netease.nimlib.sdk.avchat.model.AVChatControlEvent;
import com.netease.nimlib.sdk.avchat.model.AVChatData;
import com.netease.nimlib.sdk.avchat.model.AVChatNotifyOption;
import com.netease.nimlib.sdk.avchat.model.AVChatOptionalConfig;
import com.netease.nimlib.sdk.avchat.model.AVChatVideoFrame;

import java.util.Map;

/**
 * Created by jiawei on 2017/2/24.
 */

public class AVEngine implements AVChatStateObserver {

    private static AVEngine avEngine;
    private static final String TAG="AVEngine";

    private AVEngine(){

    }
    public void init(){
        registerNetCallObserver(true);
        registerAVChat(true);
    }

    public static synchronized AVEngine getInstance(){
        if(avEngine==null){
            avEngine = new AVEngine();
        }
        return avEngine;
    }

    //发起呼叫
    public void call(String account,
                     AVChatType callType,
                     AVChatOptionalConfig config,
                     AVChatNotifyOption notifyOption,
                     AVChatCallback<AVChatData> callback) {
        AVChatManager.getInstance().call(account, callType, config, notifyOption,callback);
    }

    //仅仅收到呼叫
    public void receive(){

    }

    //注册接收的监听
    private void registerAVChat(boolean register) {
        AVChatManager.getInstance().observeIncomingCall(new Observer<AVChatData>() {
            @Override
            public void onEvent(AVChatData chatData) {
//                AVChatActivity.launch(DemoCache.getContext(), chatData);
                Log.e(TAG, "onEvent: "+"收到呼叫" );
                if(mListener!=null){
                    mListener.receive(chatData);
                }
            }
        }, register);
    }

    //同意接听
    public void accept(AVChatOptionalConfig config,
                       AVChatCallback<java.lang.Void> callback){
        AVChatManager.getInstance().accept(config, callback);
    }

    //拒绝接听 挂断电话
    public void hangUp(AVChatCallback<java.lang.Void> callback){
        AVChatManager.getInstance().hangUp(callback);
    }

    // 请求音频切换到视频

    public void switchToVideo(AVChatCallback<java.lang.Void> callback){
        //请求音频切换到视频, 仅用于双人模式.
        AVChatManager.getInstance().requestSwitchToVideo(callback);
    }

    private void registerNetCallObserver(boolean register) {
        AVChatManager.getInstance().observeAVChatState(this, register);
        AVChatManager.getInstance().observeCalleeAckNotification(callAckObserver, register);
        AVChatManager.getInstance().observeControlNotification(callControlObserver, register);
        AVChatManager.getInstance().observeHangUpNotification(callHangupObserver, register);
//        AVChatManager.getInstance().observeOnlineAckNotification(onlineAckObserver, register);
        AVChatManager.getInstance().observeTimeoutNotification(timeoutObserver, register);
        AVChatManager.getInstance().observeAutoHangUpForLocalPhone(autoHangUpForLocalPhoneObserver, register);
    }

    /**
     *  ***************************监听**************************************
     */
    //监听被叫方回应（主叫方）主叫方在发起呼叫成功后需要监听被叫方的回应
    Observer<AVChatCalleeAckEvent> callAckObserver = new Observer<AVChatCalleeAckEvent>() {
        @Override
        public void onEvent(AVChatCalleeAckEvent ackInfo) {
            if (ackInfo.getEvent() == AVChatEventType.CALLEE_ACK_BUSY) {
                // 对方正在忙
            } else if (ackInfo.getEvent() == AVChatEventType.CALLEE_ACK_REJECT) {
                // 对方拒绝接听
            } else if (ackInfo.getEvent() == AVChatEventType.CALLEE_ACK_AGREE) {
                // 对方同意接听
                if (ackInfo.isDeviceReady()) {
                    // 设备初始化成功，开始通话
                } else {
                    // 设备初始化失败，无法进行通话
                }
            }
        }
    };

    //监听对方挂断（主叫方、被叫方）
    Observer<AVChatCommonEvent> callHangupObserver = new Observer<AVChatCommonEvent>() {
        @Override
        public void onEvent(AVChatCommonEvent hangUpInfo) {
            // 结束通话
        }
    };

    Observer<AVChatControlEvent> callControlObserver = new Observer<AVChatControlEvent>() {
        @Override
        public void onEvent(AVChatControlEvent event) {
            handleCallControl(event);
        }
    };

    private void handleCallControl(AVChatControlEvent event) {
        switch (event.getControlCommand()) {
            case SWITCH_AUDIO_TO_VIDEO:
                // 对方请求切换音频到视频
                break;
            case SWITCH_AUDIO_TO_VIDEO_AGREE:
                // 对方同意切换音频到视频
                break;
            case SWITCH_AUDIO_TO_VIDEO_REJECT:
                // 对方拒绝切换音频到视频
                break;
            case SWITCH_VIDEO_TO_AUDIO:
                // 对方请求视频切换到音频
                break;
            case NOTIFY_VIDEO_OFF:
                // 对方关闭视频的通知
                break;
            case NOTIFY_VIDEO_ON:
                // 对方开启视频的通知
                break;
            default:
                break;
        }
    }

    //主叫方在拨打网络通话时，超过 45 秒被叫方还未接听来电，则自动挂断。被叫方超过 45 秒未接听来听，也会自动挂断，
    // 在通话过程中网络超时 30 秒自动挂断。
    Observer<AVChatTimeOutEvent> timeoutObserver = new Observer<AVChatTimeOutEvent>() {
        @Override
        public void onEvent(AVChatTimeOutEvent event) {
            // 超时类型
        }
    };

    /** 参数为自动挂断的原因：
     * 1 作为被叫方：网络通话有来电还未接通，此时有本地来电，那么拒绝网络来电
     * 2 作为主叫方：正在发起网络通话时有本地来电，那么挂断网络呼叫
     * 3 双方正在进行网络通话，当有本地来电，用户接听时，挂断网络通话
     * 4 如果发起网络通话，无论是否建立连接，用户又拨打本地电话，那么网络通话挂断
     */
    Observer<Integer> autoHangUpForLocalPhoneObserver = new Observer<Integer>() {
        @Override
        public void onEvent(Integer integer) {
            // 结束通话
        }
    };

    public void onDestory(){
        registerNetCallObserver(false);
        registerAVChat(false);
    }

    /**
     * 回调
     */

    @Override
    public void onTakeSnapshotResult(String s, boolean b, String s1) {
        
    }

    @Override
    public void onConnectionTypeChanged(int i) {

    }

    @Override
    public void onLocalRecordEnd(String[] strings, int i) {

    }

    @Override
    public void onFirstVideoFrameAvailable(String s) {

    }

    @Override
    public void onVideoFpsReported(String s, int i) {

    }

    @Override
    public void onJoinedChannel(int i, String s, String s1) {

    }

    @Override
    public void onLeaveChannel() {

    }

    @Override
    public void onUserJoined(String s) {
        // TODO: 2017/2/24
        Log.e(TAG, "onUserJoined: "+"对方已接受加入，返回的"+s );
        if(mListener!=null){
            mListener.showRemoteView(s);
        }
//        avChatUI.setVideoAccount(account);
//        avChatUI.initRemoteSurfaceView(avChatUI.getVideoAccount());
    }

    @Override
    public void onUserLeave(String s, int i) {
        Log.e(TAG, "onUserJoined:用户离开 "+s+"i:"+i );
    }

    @Override
    public void onProtocolIncompatible(int i) {

    }

    @Override
    public void onDisconnectServer() {

    }

    @Override
    public void onNetworkQuality(String s, int i) {

    }

    @Override
    public void onCallEstablished() {
        Log.e(TAG, "onCallEstablished: "+"通道已建立好" );
        if(mListener!=null){
            mListener.showLocal();
        }
//        avChatUI.initLocalSurfaceView();
    }

    @Override
    public void onDeviceEvent(int i, String s) {

    }

    @Override
    public void onFirstVideoFrameRendered(String s) {
        Log.e(TAG, "onFirstVideoFrameRendered: "+"第一帧视频显示"+s);
    }

    @Override
    public void onVideoFrameResolutionChanged(String s, int i, int i1, int i2) {

    }

    @Override
    public int onVideoFrameFilter(AVChatVideoFrame avChatVideoFrame) {
        return 0;
    }

    @Override
    public int onAudioFrameFilter(AVChatAudioFrame avChatAudioFrame) {
        return 0;
    }

    @Override
    public void onAudioDeviceChanged(int i) {

    }

    @Override
    public void onReportSpeaker(Map<String, Integer> map, int i) {

    }

    @Override
    public void onStartLiveResult(int i) {

    }

    @Override
    public void onStopLiveResult(int i) {

    }

    @Override
    public void onAudioMixingEvent(int i) {

    }

    /**
     *
     * 本类自定义监听
     */
    private AVEngineListener mListener;
    public void setOnAVEngineListener(AVEngineListener listener){
        mListener=listener;
    }
    public interface AVEngineListener{
        void receive(AVChatData chatData);
        void showLocal();
        void showRemoteView(String s);
    }
}
