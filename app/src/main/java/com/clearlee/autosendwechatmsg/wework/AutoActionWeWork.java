package com.clearlee.autosendwechatmsg.wework;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public enum AutoActionWeWork {
    none(0, "无"),
    sendOnceText(1, "发送内容"),
    sendOnceImage(2, "发送图片"),
    sendMultiText(3, "自动营销"),
    checkMsg(4, "检查消息");

    Integer type;
    String name;

    AutoActionWeWork(int type, String name){
        this.type = type;
        this.name = name;
    }

    public static List<String> listName(){
        List<String> names = new ArrayList<>();
        for(AutoActionWeWork autoActionWeWork : AutoActionWeWork.values()){
            names.add(autoActionWeWork.getName());
        }
        return names;
    }
}