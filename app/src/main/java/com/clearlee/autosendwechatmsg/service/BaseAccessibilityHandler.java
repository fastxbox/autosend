package com.clearlee.autosendwechatmsg.service;

import com.clearlee.autosendwechatmsg.AutoSendMsgService;

import java.util.Random;

import lombok.Data;

/**
 * author : linzhiji
 * date   : 2020/12/10下午8:19
 * desc   :
 * version: 1.0
 */
@Data
public class BaseAccessibilityHandler {
    protected AutoSendMsgService accessibilityService;

    public int getRandomAction5000Mill(){
        Random rand = new Random();
        return rand.nextInt(1000) + 4000;
    }

    public int getRandomAction3000Mill(){
        Random rand = new Random();
        return rand.nextInt(1000) + 2000;
    }

    public int getRandomAction2000Mill(){
        Random rand = new Random();
        return rand.nextInt(1000) + 1000;
    }

    public int getRandomAction1000Mill(){
        Random rand = new Random();
        return rand.nextInt(1000);
    }

    /**
     * sleep
     **/
    public void sleepMills(long mills) {
        try {
            Thread.sleep(mills);
        } catch (Exception e) {

        }
    }
}
