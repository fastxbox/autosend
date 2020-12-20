package com.clearlee.autosendwechatmsg.dianping;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public enum AutoActionDianPing {
    none(0, "无"),
    fetchFoodShopInfo(1, "抓取美食点评数据");

    Integer type;
    String name;

    AutoActionDianPing(int type, String name){
        this.type = type;
        this.name = name;
    }

    public static List<String> listName(){
        List<String> names = new ArrayList<>();
        for(AutoActionDianPing autoActionWeWork : AutoActionDianPing.values()){
            names.add(autoActionWeWork.getName());
        }
        return names;
    }
}