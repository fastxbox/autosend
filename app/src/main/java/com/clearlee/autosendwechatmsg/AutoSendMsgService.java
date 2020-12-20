package com.clearlee.autosendwechatmsg;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.clearlee.autosendwechatmsg.dianping.DianPingHandler;
import com.clearlee.autosendwechatmsg.wework.WeworkHandler;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Clearlee
 * 2017/12/22.
 */
@Getter
@Setter
public class AutoSendMsgService extends AccessibilityService {

    private static final String TAG = "AutoSendMsgService";
    public String currentPackageName, currentActivityClassName;//记录所监测的app当前页面的元素 所对应的包名和类名（只记录 当前app页面对应的）
    WeworkHandler weworkHandler;
    DianPingHandler dianPingHandler;
//    public static boolean hasSend;
//    public static final int SEND_FAIL = 0;
//    public static final int SEND_SUCCESS = 1;
//    public static int SEND_STATUS;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        this.weworkHandler = new WeworkHandler();
        this.weworkHandler.setAccessibilityService(this);
        this.dianPingHandler = new DianPingHandler();
        this.dianPingHandler.setAccessibilityService(this);
        this.dianPingHandler.startThread();
    }

    /**
     * 必须重写的方法，响应各种事件。
     *
     * @param event
     */
    @Override
    public void onAccessibilityEvent(final AccessibilityEvent event) {
//        Log.e(TAG, "-------------------------------------------------------------");
        int eventType = event.getEventType();

        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED: {
                Log.e(TAG, "packageName:" + event.getPackageName() + "");//响应事件的包名，也就是哪个应用才响应了这个事件
                Log.e(TAG, "source:" + event.getSource() + "");//事件源信息
                Log.e(TAG, "source class:" + event.getClassName() + "");//事件源的类名，比如android.widget.TextView
                Log.e(TAG, "event type(int):" + eventType + "");


                Log.e(TAG, "event type:TYPE_NOTIFICATION_STATE_CHANGED");
                this.currentActivityClassName = event.getClassName().toString();
                this.currentPackageName = event.getPackageName().toString();
//                if(currentPackageName.equals(WeChatTextWrapper.PACKAGENAME)){
//                    this.weworkHandler.handleEvent(this, event);
//                }else if(currentPackageName.equals(DianPingTextWrapper.PACKAGENAME)){
//                    this.dianPingHandler.handleEvent(this, event);
//                }
                break;
            }
//            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED://窗体状态改变
//                Log.e(TAG, "event type:TYPE_NOTIFICATION_STATE_CHANGED");
//                break;
//            case AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED://View获取到焦点
//                Log.e(TAG, "event type:TYPE_VIEW_ACCESSIBILITY_FOCUSED");
//                break;
//            case AccessibilityEvent.TYPE_GESTURE_DETECTION_START:
//                Log.e(TAG, "event type:TYPE_GESTURE_DETECTION_START_FOCUSED");
//                break;
//            case AccessibilityEvent.TYPE_GESTURE_DETECTION_END:
//                Log.e(TAG, "event type:TYPE_GESTURE_DETECTION_END");
//                break;
//            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
//                Log.e(TAG, "event type:TYPE_WINDOW_CONTENT_CHANGED");
////                String currentActivity = event.getClassName().toString();
////                if (currentActivity.equals(WeChatTextWrapper.WechatClass.WEWORK_CLASS_WWMAIN)) {
////                    if (WeChatTextWrapper.autoActionWeWork == AutoActionWeWork.checkMsg) {
////                        handleFlow_WeWorkLaunchUI();
////                    }
////                }
//                break;
//            case AccessibilityEvent.TYPE_VIEW_CLICKED:
//                Log.e(TAG, "event type:TYPE_VIEW_CLICKED");
//                break;
//            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
//                Log.e(TAG, "event type:TYPE_VIEW_TEXT_CHANGED");
//                break;
//            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
//                Log.e(TAG, "event type:TYPE_VIEW_SCROLLED");
//                break;
//            case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
//                Log.e(TAG, "event type:TYPE_VIEW_TEXT_SELECTION_CHANGED");
//                break;

        }
        for (CharSequence txt : event.getText()) {
            Log.e(TAG, "text:" + txt);//输出当前事件包含的文本信息
        }
        Log.e(TAG, "-------------------------------------------------------------");
    }

    @Override
    public void onInterrupt() {

    }



}
