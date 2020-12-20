package com.clearlee.autosendwechatmsg.service;

import android.view.accessibility.AccessibilityEvent;

import com.clearlee.autosendwechatmsg.AutoSendMsgService;

/**
 * author : linzhiji
 * date   : 2020/12/10下午8:31
 * desc   :
 * version: 1.0
 */
public interface HandleAccessibility {

    public void handleEvent(AutoSendMsgService accessibilityService, final AccessibilityEvent event);

}
