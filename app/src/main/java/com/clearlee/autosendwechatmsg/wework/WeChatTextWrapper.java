package com.clearlee.autosendwechatmsg.wework;

import com.clearlee.autosendwechatmsg.constant.SendStatus;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Clearlee on 2017/12/22 0023.
 * 微信版本6.6.0
 */



public class WeChatTextWrapper {

    public static final String PACKAGENAME = "com.tencent.wework";

    public static class WechatClass{
        //微信首页
        public static final String WECHAT_CLASS_LAUNCHUI = "com.tencent.wework.launch.LaunchSplashActivity";

        //微信联系人页面
        public static final String WECHAT_CLASS_CONTACTINFOUI = "com.tencent.wework.plugin.profile.ui.ContactInfoUI";
        //微信聊天页面
        public static final String WECHAT_CLASS_CHATUI = "com.tencent.wework.ui.chatting.ChattingUI";



        public static final String WEWORK_CLASS_WWMAIN = "com.tencent.wework.launch.WwMainActivity";

        public static final String WEWORK_CLASS_GROUPCHATUI = "com.tencent.wework.msg.controller.ExternalGroupMessageListActivity";

    }


    public static class WechatId{
        /**
         * 通讯录界面
         */
        public static final String WECHATID_CONTACTUI_LISTVIEW_ID = "com.tencent.wework:id/n3";
        public static final String WECHATID_CONTACTUI_ITEM_ID = "com.tencent.wework:id/nx";
        public static final String WECHATID_CONTACTUI_NAME_ID = "com.tencent.wework:id/o1";


        /**
         * 企业微信消息列表
         */
        public static final String WEWORKID_MESSAGETUI_LISTVIEW_ID = "com.tencent.wework:id/bcb";
        public static final String WEWORKID_MESSAGETUI_NAME_ID = "com.tencent.wework:id/ec6";

        /**
         * 企业微信群聊页面
         */

        public static final String WEWORKCHATID_GROUPCHATUI_USERNAME_ID = "com.tencent.wework:id/hxc";
        public static final String WEWORKCHATID_GROUPCHATUI_EDITTEXT_ID = "com.tencent.wework:id/ejs";
        public static final String WEWORKCHATID_GROUPCHATUI_SWITCH_ID = "com.tencent.wework:id/dmj";
        public static final String WEWORKCHATID_GROUPCHATUI_BACK_ID = "com.tencent.wework:id/hxb";
        public static final String WEWORKCHATID_GROUPCHATUI_PLUS_ID = "com.tencent.wework:id/ejg";
        public static final String WEWORKCHATID_PICKIMAGEUI_SELECT_ID = "com.tencent.wework:id/gll";
        public static final String WEWORKCHATID_PICKIMAGEUI_SEND_ID = "com.tencent.wework:id/hxt";
        public static final String WEWORKCHATID_CHATLIST_MSG_ID = "com.tencent.wework:id/g2q";
        public static final String WEWORKCHATID_GROUPCHATUI_MSGTEXT_ID = "com.tencent.wework:id/ejd";


        /**
         * 聊天界面
         */
        public static final String WECHATID_CHATUI_EDITTEXT_ID = "com.tencent.wework:id/ami";
        public static final String WECHATID_CHATUI_USERNAME_ID = "com.tencent.wework:id/ko";
        public static final String WECHATID_CHATUI_BACK_ID = "com.tencent.wework:id/km";
        public static final String WECHATID_CHATUI_SWITCH_ID = "com.tencent.wework:id/amg";
    }


    public static List<String> AUTOMSGLIST = Arrays.asList("早上好~\n" + "周xxx啦~吃点好的吧！[鼓掌]\n" + "好邻居煎饼/咖啡/奶茶/包子/豆浆/关东煮/玉米 怎么搭配呢",
            "早上~\n" + "周xxx还不来份好邻居的早餐吗！[愉快][愉快]\n" + "煎饼/咖啡/奶茶/包子/豆浆/关东煮/玉米 早上吃的好",
            "早上~\n" + "大家吃早餐了吗？煎饼来一份，记得要吃早餐哦。",
            "早上好啊~\n" + "不要睡太晚，要记得吃早餐不要饿坏胃",
            "早上好啊~\n" + "在健康这条路上，好习惯显得尤为重要，每天再忙也要记得吃早饭",
            "早上好啊~\n" + "上班不但要记得签到，还要记得吃早餐哦。",
            "早上好~\n" + "只有填饱肚子才有力气干活呢！冲鸭！");




    public static AutoActionWeWork autoActionWeWork = AutoActionWeWork.none;

    public static SendStatus sendStatus = SendStatus.none;

//    public static final String WECAHT_PACKAGENAME = "com.tencent.mm";
//
//
//    public static class WechatClass{
//        //微信首页
//        public static final String WECHAT_CLASS_LAUNCHUI = "com.tencent.mm.ui.LauncherUI";
//        //微信联系人页面
//        public static final String WECHAT_CLASS_CONTACTINFOUI = "com.tencent.mm.plugin.profile.ui.ContactInfoUI";
//        //微信聊天页面
//        public static final String WECHAT_CLASS_CHATUI = "com.tencent.mm.ui.chatting.ChattingUI";
//    }
//
//
//    public static class WechatId{
//        /**
//         * 通讯录界面
//         */
//        public static final String WECHATID_CONTACTUI_LISTVIEW_ID = "com.tencent.mm:id/n3";
//        public static final String WECHATID_CONTACTUI_ITEM_ID = "com.tencent.mm:id/nx";
//        public static final String WECHATID_CONTACTUI_NAME_ID = "com.tencent.mm:id/o1";
//
//        /**
//         * 聊天界面
//         */
//        public static final String WECHATID_CHATUI_EDITTEXT_ID = "com.tencent.mm:id/ami";
//        public static final String WECHATID_CHATUI_USERNAME_ID = "com.tencent.mm:id/ko";
//        public static final String WECHATID_CHATUI_BACK_ID = "com.tencent.mm:id/km";
//        public static final String WECHATID_CHATUI_SWITCH_ID = "com.tencent.mm:id/amg";
//    }
}
