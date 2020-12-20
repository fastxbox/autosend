package com.clearlee.autosendwechatmsg.dianping;

import com.clearlee.autosendwechatmsg.constant.SendStatus;

/**
 * author : linzhiji
 * date   : 2020/12/10下午8:26
 * desc   :
 * version: 1.0
 */
public class DianPingTextWrapper {

    public static final String PACKAGENAME = "com.dianping.v1";

    public static String PAGE_CLASS_SPLASH_PAGE = "com.dianping.v1.NovaMainActivity";

    public static String Home_City_Label = "com.dianping.v1:id/city";
    public static String Home_City_Food = "com.dianping.v1:id/nearby";


    public static String City_Activity = "com.dianping.main.city.CityListSwitchActivity";
    public static String City_Row_Label = "android:id/text1";
    public static String City_Search_Text_View = "com.dianping.v1:id/search_edit";
    public static String City_Search_Bar_View = "com.dianping.v1:id/button_search_bar";
    public static String City_Search_Result_Row = "com.dianping.v1:id/area";

    public static String Food_Activity = "com.dianping.channel.food.ShopListFoodActivity";


    public static String Food_Shop_Activity = "com.dianping.shopshell.ShopInfoActivity";
    public static String Food_Shop_ShopItem = "com.dianping.v1:id/shop_item";
    public static String Food_Shop_ShopItem_Title = "com.dianping.v1:id/tv_shop_title";
    public static String Food_Shop_ShopItem_Category = "com.dianping.v1:id/category_name";
    public static String Food_Shop_ShopItem_RegionName = "com.dianping.v1:id/region_name";
    public static String Food_Shop_ShopItem_CommentNum = "com.dianping.v1:id/tv_comment_num";
    public static String Food_Shop_ShopItem_ShopPrice = "com.dianping.v1:id/tv_shop_price";
    public static String Food_Shop_ShopItem_Pic = "com.dianping.v1:id/shop_pic_thumb";
    public static String Food_Shop_List_RecyclerView = "com.dianping.v1:id/pagecontainer_recyclerview";
    public static String Food_Shop_List_Filter = "com.dianping.v1:id/txt_filter_title";

    public static String Food_Shop_Filter_Region_Left = "com.dianping.v1:id/left_lv";
    public static String Food_Shop_Filter_Region_Right = "com.dianping.v1:id/right_lv";

    public static String Food_Shop_Filter_Region_Text = "android:id/text1";
    public static String Food_Shop_Filter_Region_Number = "android:id/text2";


    public static String Food_Shop_Home_Contact = "com.dianping.v1:id/phone_content";


    public static String System_Contact_input = "com.android.contacts:id/normal_input";




    public static AutoActionDianPing autoActionDianping = AutoActionDianPing.none;

    public static SendStatus sendStatus = SendStatus.none;

}
