package com.clearlee.autosendwechatmsg.wework;

import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.blankj.utilcode.util.CollectionUtils;
import com.clearlee.autosendwechatmsg.AutoSendMsgService;
import com.clearlee.autosendwechatmsg.constant.SendStatus;
import com.clearlee.autosendwechatmsg.service.BaseAccessibilityHandler;
import com.clearlee.autosendwechatmsg.service.HandleAccessibility;
import com.clearlee.autosendwechatmsg.util.ActionHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lombok.Getter;
import lombok.Setter;

/**
 * author : linzhiji
 * date   : 2020/12/10下午8:11
 * desc   :
 * version: 1.0
 */
@Getter
@Setter
public class WeworkHandler extends BaseAccessibilityHandler implements HandleAccessibility {
    private static final String TAG = "WeworkHandler";

    private List<String> allNameList = new ArrayList<>();
    private int mRepeatCount;

    @Override
    public void handleEvent(AutoSendMsgService accessibilityService, final AccessibilityEvent event){
        this.accessibilityService = accessibilityService;
        String currentActivity = event.getClassName().toString();
        if (WeChatTextWrapper.sendStatus == SendStatus.send_done ||
                WeChatTextWrapper.sendStatus == SendStatus.send_fail) {
            return;
        }
        if (currentActivity.equals(WeChatTextWrapper.WechatClass.WECHAT_CLASS_LAUNCHUI)) {
            handleFlow_LaunchUI();
        } else if (currentActivity.equals(WeChatTextWrapper.WechatClass.WECHAT_CLASS_CONTACTINFOUI)) {
            handleFlow_ContactInfoUI();
        } else if (currentActivity.equals(WeChatTextWrapper.WechatClass.WECHAT_CLASS_CHATUI)) {
            handleFlow_ChatUI();
        } else if (currentActivity.equals(WeChatTextWrapper.WechatClass.WEWORK_CLASS_WWMAIN)) {
            if (WeChatTextWrapper.autoActionWeWork == AutoActionWeWork.sendOnceText ||
                    WeChatTextWrapper.autoActionWeWork == AutoActionWeWork.sendOnceImage ||
                    WeChatTextWrapper.autoActionWeWork == AutoActionWeWork.sendMultiText) {
                handleFlow_WeWorkLaunchUI();
            } else if (WeChatTextWrapper.autoActionWeWork == AutoActionWeWork.checkMsg) {
                handleFlow_WeWorkLaunchUI();
            }
        } else if (currentActivity.equals(WeChatTextWrapper.WechatClass.WEWORK_CLASS_GROUPCHATUI)) {
            handleFlow_WeWorkGroupChatUI();
        }
    }


    private void handleFlow_ChatUI() {

        //如果微信已经处于聊天界面，需要判断当前联系人是不是需要发送的联系人
        String curUserName = ActionHelper.findTextById(this.accessibilityService, WeChatTextWrapper.WechatId.WECHATID_CHATUI_USERNAME_ID, null);
        if (!TextUtils.isEmpty(curUserName) && curUserName.equals(WechatUtils.NAME)) {
            if (ActionHelper.findViewByIdAndPasteContent(this.accessibilityService, WeChatTextWrapper.WechatId.WECHATID_CHATUI_EDITTEXT_ID, WechatUtils.CONTENT)) {
                sendContent();
            } else {
                //当前页面可能处于发送语音状态，需要切换成发送文本状态
                ActionHelper.findViewIdAndClick(this.accessibilityService, WeChatTextWrapper.WechatId.WECHATID_CHATUI_SWITCH_ID, null);

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (ActionHelper.findViewByIdAndPasteContent(this.accessibilityService, WeChatTextWrapper.WechatId.WECHATID_CHATUI_EDITTEXT_ID, WechatUtils.CONTENT)) {
                    sendContent();
                }
            }
        } else {
            //回到主界面
            ActionHelper.findViewIdAndClick(this.accessibilityService, WeChatTextWrapper.WechatId.WECHATID_CHATUI_BACK_ID, null);
        }
    }

    private void sendGroupTextContent(String content, boolean isNeedReturnToOtherAPP) {
        if (ActionHelper.findViewByIdAndPasteContent(this.accessibilityService, WeChatTextWrapper.WechatId.WEWORKCHATID_GROUPCHATUI_EDITTEXT_ID, content)) {
            ActionHelper.findTextAndClick(this.accessibilityService, "发送", null);
            WeChatTextWrapper.sendStatus = SendStatus.send_done;
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ActionHelper.findViewIdAndClick(this.accessibilityService, WeChatTextWrapper.WechatId.WEWORKCHATID_GROUPCHATUI_BACK_ID, null);
            if (isNeedReturnToOtherAPP) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ActionHelper.resetAndReturnApp(this.accessibilityService);
            }
        }
    }

    private void sendTextContent() {
        if (ActionHelper.findViewByIdAndPasteContent(this.accessibilityService, WeChatTextWrapper.WechatId.WEWORKCHATID_GROUPCHATUI_EDITTEXT_ID, WechatUtils.CONTENT)) {
            sendContent();
        } else {
            //当前页面可能处于发送语音状态，需要切换成发送文本状态
            ActionHelper.findViewIdAndClick(this.accessibilityService, WeChatTextWrapper.WechatId.WEWORKCHATID_GROUPCHATUI_SWITCH_ID, null);

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (ActionHelper.findViewByIdAndPasteContent(this.accessibilityService, WeChatTextWrapper.WechatId.WEWORKCHATID_GROUPCHATUI_EDITTEXT_ID, WechatUtils.CONTENT)) {
                sendContent();
            }
        }
    }

    private void handleFlow_WeWorkGroupChatUI() {
        if (WeChatTextWrapper.autoActionWeWork == AutoActionWeWork.checkMsg) {
            AccessibilityNodeInfo rootNode = this.accessibilityService.getRootInActiveWindow();
            if (null != rootNode) {
                List<AccessibilityNodeInfo> listview = rootNode.findAccessibilityNodeInfosByViewId(WeChatTextWrapper.WechatId.WEWORKCHATID_GROUPCHATUI_MSGTEXT_ID);
                if (!CollectionUtils.isEmpty(listview)) {
                    AccessibilityNodeInfo lastNode = listview.get(listview.size() - 1);
                    String lastNodeStr = lastNode.getText().toString();
                    if (lastNodeStr.contains("电话")) {
                        sendGroupTextContent("400-8833-850", true);
                    } else if (lastNodeStr.contains("还有咖啡吗")) {
                        sendGroupTextContent("亲，还有的，欢迎到店购买", true);
                    } else if (lastNodeStr.contains("营业时间")) {
                        sendGroupTextContent("我们营业时间是周一到周五，上午7点到晚上8点", true);
                    } else {
                        ActionHelper.findViewIdAndClick(this.accessibilityService, WeChatTextWrapper.WechatId.WEWORKCHATID_GROUPCHATUI_BACK_ID, null);
                    }
                } else {
                    ActionHelper.findViewIdAndClick(this.accessibilityService, WeChatTextWrapper.WechatId.WEWORKCHATID_GROUPCHATUI_BACK_ID, null);
                }
            } else {
                ActionHelper.findViewIdAndClick(this.accessibilityService, WeChatTextWrapper.WechatId.WEWORKCHATID_GROUPCHATUI_BACK_ID, null);
            }

        } else {
            //如果微信已经处于聊天界面，需要判断当前联系人是不是需要发送的联系人
            String curUserName = ActionHelper.findTextById(this.accessibilityService, WeChatTextWrapper.WechatId.WEWORKCHATID_GROUPCHATUI_USERNAME_ID, null);
            if (!TextUtils.isEmpty(curUserName) && curUserName.contains(WechatUtils.NAME)) {
                if (WeChatTextWrapper.autoActionWeWork == AutoActionWeWork.sendOnceText) {
                    sendGroupTextContent(getRandomString(), true);
                } else if (WeChatTextWrapper.autoActionWeWork == AutoActionWeWork.sendOnceImage) {
                    sendImageContent();
                } else if (WeChatTextWrapper.autoActionWeWork == AutoActionWeWork.sendMultiText) {
                    sendGroupTextContent(getRandomString(), true);
                }
            } else {
                //回到主界面
                ActionHelper.findViewIdAndClick(this.accessibilityService, WeChatTextWrapper.WechatId.WEWORKCHATID_GROUPCHATUI_BACK_ID, null);
            }
        }
    }

    private void handleFlow_ContactInfoUI() {
        ActionHelper.findTextAndClick(this.accessibilityService, "发消息", null);
    }

    private void handleFlow_LaunchUI() {
        try {
            //点击通讯录，跳转到通讯录页面
            ActionHelper.findTextAndClick(this.accessibilityService, "通讯录", null);

            Thread.sleep(50);

            //再次点击通讯录，确保通讯录列表移动到了顶部
            ActionHelper.findTextAndClick(this.accessibilityService, "通讯录", null);

            Thread.sleep(200);

            //遍历通讯录联系人列表，查找联系人
            AccessibilityNodeInfo itemInfo = TraversalAndFindContacts();
            if (itemInfo != null) {
                ActionHelper.performClick(itemInfo);
            } else {
                WeChatTextWrapper.sendStatus = SendStatus.send_fail;
                ActionHelper.resetAndReturnApp(this.accessibilityService);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkMsg() {
//        while (true) {
//            if (ActionHelper.stopLoop == true) {
//                break;
//            }
        boolean hasNewMsg = false;
        AccessibilityNodeInfo rootNode = this.accessibilityService.getRootInActiveWindow();
        List<AccessibilityNodeInfo> listview = rootNode.findAccessibilityNodeInfosByViewId(WeChatTextWrapper.WechatId.WEWORKCHATID_CHATLIST_MSG_ID);
        if (!CollectionUtils.isEmpty(listview)) {
            for (AccessibilityNodeInfo nodeInfo : listview) {
                AccessibilityNodeInfo clickNode = ActionHelper.findClickableNode(nodeInfo);
                if (null != clickNode) {
                    ActionHelper.performClick(clickNode);
                    hasNewMsg = true;
                    break;
                }
            }
        }
        if(!hasNewMsg){
            try {
                Thread.sleep(2000);
            }catch (Exception e){
            }
            ActionHelper.resetAndReturnApp(this.accessibilityService);
        }
//            try {
//                Thread.sleep(6000);
//            } catch (Exception e) {
//
//            }
//        }
    }

    private void handleFlow_WeWorkLaunchUI() {
        try {
            //点击通讯录，跳转到通讯录页面
            ActionHelper.findTextAndClick(this.accessibilityService, "消息", null);

            Thread.sleep(50);

            //再次点击通讯录，确保通讯录列表移动到了顶部
            ActionHelper.findTextAndClick(this.accessibilityService, "消息", null);

            Thread.sleep(1000);

            if (WeChatTextWrapper.autoActionWeWork == AutoActionWeWork.checkMsg) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        checkMsg();
//                    }
//                });
                checkMsg();

            } else {
                //遍历通讯录联系人列表，查找联系人
                AccessibilityNodeInfo itemInfo = TraversalAndFindWeWorkChatList();
                if (itemInfo != null) {
                    ActionHelper.performClick(itemInfo);
                } else {
                    WeChatTextWrapper.sendStatus = SendStatus.send_fail;
                    ActionHelper.resetAndReturnApp(this.accessibilityService);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 从头到尾遍历聊天记录
     */
    private AccessibilityNodeInfo TraversalAndFindWeWorkChatList() {

        if (allNameList != null) allNameList.clear();

        AccessibilityNodeInfo rootNode = this.accessibilityService.getRootInActiveWindow();
        List<AccessibilityNodeInfo> listview = rootNode.findAccessibilityNodeInfosByViewId(WeChatTextWrapper.WechatId.WEWORKID_MESSAGETUI_LISTVIEW_ID);

        //是否滚动到了底部
        boolean scrollToBottom = false;
        if (listview != null && !listview.isEmpty()) {
            while (true) {
                //获取当前屏幕上的联系人信息
                List<AccessibilityNodeInfo> nameList = rootNode.findAccessibilityNodeInfosByViewId(WeChatTextWrapper.WechatId.WEWORKID_MESSAGETUI_NAME_ID);

                if (nameList != null && !nameList.isEmpty()) {
                    for (int i = 0; i < nameList.size(); i++) {
                        if (i == 0) {
                            //必须在一个循环内，防止翻页的时候名字发生重复
                            mRepeatCount = 0;
                        }
                        AccessibilityNodeInfo nodeInfo = nameList.get(i);
                        String nickname = nodeInfo.getText().toString();
                        Log.e(TAG, "nickname = " + nickname);
                        if (nickname.equals(WechatUtils.NAME)) {
                            return ActionHelper.findClickableNode(nodeInfo);
                        }
                        if (!allNameList.contains(nickname)) {
                            allNameList.add(nickname);
                        } else if (allNameList.contains(nickname)) {
                            Log.e(TAG, "mRepeatCount = " + mRepeatCount);
                            if (mRepeatCount == 10) {
                                //表示已经滑动到顶部了
                                if (scrollToBottom) {
                                    Log.e(TAG, "没有找到联系人");
                                    //此次发消息操作已经完成
                                    WeChatTextWrapper.sendStatus = SendStatus.send_done;
                                    return null;
                                }
                                scrollToBottom = true;
                            }
                            mRepeatCount++;
                        }
                    }
                }

                if (!scrollToBottom) {
                    //向下滚动
                    listview.get(0).performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                } else {
                    return null;
                }

                //必须等待，因为需要等待滚动操作完成
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 从头至尾遍历寻找联系人
     *
     * @return
     */
    private AccessibilityNodeInfo TraversalAndFindContacts() {

        if (allNameList != null) allNameList.clear();

        AccessibilityNodeInfo rootNode = this.accessibilityService.getRootInActiveWindow();
        List<AccessibilityNodeInfo> listview = rootNode.findAccessibilityNodeInfosByViewId(WeChatTextWrapper.WechatId.WECHATID_CONTACTUI_LISTVIEW_ID);

        //是否滚动到了底部
        boolean scrollToBottom = false;
        if (listview != null && !listview.isEmpty()) {
            while (true) {
                //获取当前屏幕上的联系人信息
                List<AccessibilityNodeInfo> nameList = rootNode.findAccessibilityNodeInfosByViewId(WeChatTextWrapper.WechatId.WECHATID_CONTACTUI_NAME_ID);
                List<AccessibilityNodeInfo> itemList = rootNode.findAccessibilityNodeInfosByViewId(WeChatTextWrapper.WechatId.WECHATID_CONTACTUI_ITEM_ID);

                if (nameList != null && !nameList.isEmpty()) {
                    for (int i = 0; i < nameList.size(); i++) {
                        if (i == 0) {
                            //必须在一个循环内，防止翻页的时候名字发生重复
                            mRepeatCount = 0;
                        }
                        AccessibilityNodeInfo itemInfo = itemList.get(i);
                        AccessibilityNodeInfo nodeInfo = nameList.get(i);
                        String nickname = nodeInfo.getText().toString();
                        Log.e(TAG, "nickname = " + nickname);
                        if (nickname.equals(WechatUtils.NAME)) {
                            return itemInfo;
                        }
                        if (!allNameList.contains(nickname)) {
                            allNameList.add(nickname);
                        } else if (allNameList.contains(nickname)) {
                            Log.e(TAG, "mRepeatCount = " + mRepeatCount);
                            if (mRepeatCount == 100) {
                                //表示已经滑动到顶部了
                                if (scrollToBottom) {
                                    Log.e(TAG, "没有找到联系人");
                                    //此次发消息操作已经完成
                                    WeChatTextWrapper.sendStatus = SendStatus.send_done;
                                    return null;
                                }
                                scrollToBottom = true;
                            }
                            mRepeatCount++;
                        }
                    }
                }

                if (!scrollToBottom) {
                    //向下滚动
                    listview.get(0).performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                } else {
                    return null;
                }

                //必须等待，因为需要等待滚动操作完成
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private void sendContent() {
        ActionHelper.findTextAndClick(this.accessibilityService, "发送", null);
        WeChatTextWrapper.sendStatus = SendStatus.send_done;
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ActionHelper.resetAndReturnApp(this.accessibilityService);
    }

    private void sendImageContent() {

        // 弹出底部工具选择栏
        ActionHelper.findViewIdAndClick(this.accessibilityService, WeChatTextWrapper.WechatId.WEWORKCHATID_GROUPCHATUI_PLUS_ID, null);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 弹出选择图片
        ActionHelper.findTextAndClick(this.accessibilityService, "图片", null);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 选择最新一张图片
        ActionHelper.findViewIdAndClick(this.accessibilityService, WeChatTextWrapper.WechatId.WEWORKCHATID_PICKIMAGEUI_SELECT_ID, null);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //发送
        ActionHelper.findViewIdAndClick(this.accessibilityService, WeChatTextWrapper.WechatId.WEWORKCHATID_PICKIMAGEUI_SEND_ID, null);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        WeChatTextWrapper.sendStatus = SendStatus.send_done;
        ActionHelper.resetAndReturnApp(this.accessibilityService);
    }

    private String getRandomString() {
        Random rand = new Random();
        Integer size = WeChatTextWrapper.AUTOMSGLIST.size();
        Integer randIdx = rand.nextInt(size);
        String msg = WeChatTextWrapper.AUTOMSGLIST.get(randIdx);
        return msg;
    }

}
