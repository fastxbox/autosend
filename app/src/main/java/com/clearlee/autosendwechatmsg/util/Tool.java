package com.clearlee.autosendwechatmsg.util;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;
import java.util.Random;

/**
 * Create by WangTengFei on 2020/11/9 18:19
 */
public class Tool  {

    /**
     * 判断字符串是否为null或是长度为0 或是字符串null
     **/
    public static boolean IsEmptyOrNullString(String s) {
        return (s == null) || (s.trim().length() == 0) || s.equalsIgnoreCase("null");
    }

    public static <T> boolean isEmpty(List<T> list) {
        if (list == null || list.size() == 0) {
            return true;
        }
        return false;
    }

    /**
     * 获取当前进程名
     */
    public static String getCurrentProcessName(Context context) {
        int pid = android.os.Process.myPid();
        String processName = "";
        ActivityManager manager = (ActivityManager) context.getSystemService
                (Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo process : manager.getRunningAppProcesses()) {
            if (process.pid == pid) {
                processName = process.processName;
            }
        }
        return processName;
    }

    /**
     * 返回指定小时对应的毫秒数
     * **/
    public static long getHourMills(int hours){
        return hours*getMinMills(60);
    }

    /**
     * 返回指定分钟数对应的毫秒数
     **/
    public static long getMinMills(int mins) {
        return mins * 60 * 1000;
    }

    /**
     * 返回指定的秒数的毫秒
     **/
    public static long getSecMills(int seconds) {
        return seconds * 1000;
    }

    /**
     * 返回秒
     **/
    public static int getSecFromMills(long mills) {
        int secs = (int) (mills / 1000);
        return secs <= 0 ? 0 : secs;
    }

    /**
     * 返回 1000-3000毫秒随机时间
     * @return
     */
    public static int getRandomActionMill(){
        Random rand = new Random();
        return rand.nextInt(2*1000) + 1000;
    }
}
