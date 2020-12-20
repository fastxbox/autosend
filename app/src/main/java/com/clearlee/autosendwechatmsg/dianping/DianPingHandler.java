package com.clearlee.autosendwechatmsg.dianping;

import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.StringUtils;
import com.clearlee.autosendwechatmsg.AutoSendMsgService;
import com.clearlee.autosendwechatmsg.service.BaseAccessibilityHandler;
import com.clearlee.autosendwechatmsg.service.HandleAccessibility;
import com.clearlee.autosendwechatmsg.util.ActionHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

/**
 * author : linzhiji
 * date   : 2020/12/10下午8:28
 * desc   :
 * version: 1.0
 */
@Setter
@Getter
public class DianPingHandler extends BaseAccessibilityHandler implements HandleAccessibility {
    static final String TAG = "DianPingHandler";
    List<String> shopNameList = new ArrayList<>();
    List<String> regionList = new ArrayList<>();

    @Override
    public void handleEvent(AutoSendMsgService accessibilityService, final AccessibilityEvent event) {
        this.accessibilityService = accessibilityService;
        String currentActivity = event.getClassName().toString();
    }


    public void startThread() {
        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                while (true) {
                    if (DianPingTextWrapper.autoActionDianping == AutoActionDianPing.fetchFoodShopInfo) {
                        DianPingTextWrapper.autoActionDianping = AutoActionDianPing.none;

                        startApp();
                        handleShopListActivity();
                        //                        startGetFoodShopInfo();
                    }
                    Thread.sleep(1000);
                }
            }
        }).start();
    }

    public void startApp(){
        if (!ActionHelper.isHitApp(this.accessibilityService, DianPingTextWrapper.PACKAGENAME)) {
            ActionHelper.startApp(this.accessibilityService, DianPingTextWrapper.PACKAGENAME, DianPingTextWrapper.PAGE_CLASS_SPLASH_PAGE);
        }
        sleepMills(getRandomAction5000Mill());
    }
    /***
     * 开始遍历店铺门店信息，需要获取 门店名，电话，商圈，分类，评分，评论数
     * .先到首页
     * .for(遍历所有需要搜索的城市){
     *      到城市列表页面，搜索 所需城市;
     *      点击搜索结果第一个项，到城市详情页，点击美食;
     *      到美食店铺列表页面，点击区域筛选,获取所有区域信息
     *      for(逐个点击区域取店铺列表）{
     *          for(逐步滑动列表，获取信息）{
     *              收集门店名，商圈，分类，评分，评论数据;
     *              到详情页面，收集电话信息
     *          }
     *      }
     * }
     */
    public void startGetFoodShopInfo() {
        startApp();

        if(!waitNeededView(DianPingTextWrapper.PAGE_CLASS_SPLASH_PAGE)){
           return;
        }
        // 到城市页面
        ActionHelper.performViewClick(this.accessibilityService, DianPingTextWrapper.Home_City_Label);
        if(!waitNeededView(DianPingTextWrapper.City_Activity)){
            return;
        }
        searchCity();

        ActionHelper.findTextAndClick(this.accessibilityService, "美食", null);
        if(!waitNeededView(DianPingTextWrapper.Food_Activity)){
            return;
        }
        sleepMills(getRandomAction2000Mill());
        handleShopListActivity();
    }

    /**
     * 城市列表页，搜索城市，搜索到后到详情页面
     */
    public void searchCity(){
        if(!waitNeededView(DianPingTextWrapper.City_Activity)){
            return;
        }

        String cityName = "北京";
        //点击搜索框
        ActionHelper.performViewClick(this.accessibilityService, DianPingTextWrapper.City_Search_Bar_View);
        sleepMills(getRandomAction2000Mill());
        // 粘贴搜索
        String findCity = cityName + "\r\n";
        ActionHelper.findViewByIdAndPasteContent(this.accessibilityService, DianPingTextWrapper.City_Search_Text_View, findCity);
        sleepMills(getRandomAction2000Mill());
        // 查找第一个
        ActionHelper.performViewClick(this.accessibilityService, DianPingTextWrapper.City_Search_Result_Row);
        if(!waitNeededView(DianPingTextWrapper.PAGE_CLASS_SPLASH_PAGE)){
            return;
        }
    }

    /**
     * 店铺列表页面，先找到所有区域，然后点击后，右侧筛选全部
     */

    public void handleShopListActivity(){
        traversalRegionInfo();
        for(String region:this.regionList){
            clickRegion(region);
            findAllShopInfo();
        }
    }

    /**
     * 店铺列表页，获取区域筛选里所有区域信息
     */
    public void traversalRegionInfo(){
        if(!waitNeededView(DianPingTextWrapper.Food_Activity)){
            return;
        }

        // 点击区域筛选
        ActionHelper.findViewIdAndClick(this.accessibilityService, DianPingTextWrapper.Food_Shop_List_Filter, null);
        sleepMills(getRandomAction2000Mill());
        AccessibilityNodeInfo leftFilterNode = ActionHelper.findViewById(this.accessibilityService, DianPingTextWrapper.Food_Shop_Filter_Region_Left, null);
        // 滑动到顶部
        scrollToTop(leftFilterNode, 20);
        
        int step = 1;
        while(step < 100){
            List<String> regionNameList = ActionHelper.listTextByViewId(this.accessibilityService, DianPingTextWrapper.Food_Shop_Filter_Region_Text, leftFilterNode);
            int num = concatRegionName(regionNameList);
            if(num <= 0){
                break;
            }
            ActionHelper.scrollForward(leftFilterNode);
//            sleepMills(getRandomAction2000Mill());
            step++;
        }

        // 筛选出无用的区域
        filterInvalidRegion();

        // 再次点击筛选区域，返回
        ActionHelper.findViewIdAndClick(this.accessibilityService, DianPingTextWrapper.Food_Shop_List_Filter, null);
        sleepMills(getRandomAction2000Mill());
    }


    public void scrollToTop(AccessibilityNodeInfo node, int count){
        int step = 1;
        while(step < count){
            ActionHelper.scrollBackward(node);
            step++;
        }
    }

    public void clickRegion(String region){
        if(!waitNeededView(DianPingTextWrapper.Food_Activity)){
            return;
        }

        // 点击区域筛选
        ActionHelper.findViewIdAndClick(this.accessibilityService, DianPingTextWrapper.Food_Shop_List_Filter, null);
        sleepMills(getRandomAction2000Mill());

        AccessibilityNodeInfo leftFilterNode = ActionHelper.findViewById(this.accessibilityService, DianPingTextWrapper.Food_Shop_Filter_Region_Left, null);
        int step = 1;
        while(step < 100){
            boolean ret = ActionHelper.findTextAndClick(this.accessibilityService, region, leftFilterNode);
            sleepMills(getRandomAction2000Mill());
            if(ret){
                String rightText = "全部" + region;
                AccessibilityNodeInfo rightFilterNode = ActionHelper.findViewById(this.accessibilityService, DianPingTextWrapper.Food_Shop_Filter_Region_Right, null);
                ret = ActionHelper.findTextAndClick(this.accessibilityService, rightText, rightFilterNode);
                sleepMills(getRandomAction2000Mill());
                if(ret == true){
                    break;
                }
            }
            step++;
        }

        // 没找到的话，返回
        if(step == 100){
            // 再次点击筛选区域，返回
            ActionHelper.findViewIdAndClick(this.accessibilityService, DianPingTextWrapper.Food_Shop_List_Filter, null);
            sleepMills(getRandomAction2000Mill());
        }
    }

    /***
     * 店铺列表页面，取完店铺信息，向上继续滑动
     */
    public void findAllShopInfo(){
        if(!waitNeededView(DianPingTextWrapper.Food_Activity)){
            return;
        }
        AccessibilityNodeInfo nodeInfo = ActionHelper.findViewById(this.accessibilityService ,DianPingTextWrapper.Food_Shop_List_RecyclerView, null);
        int step = 1;
        while (step < 10000){
            // 如果本次向上滑动后，取得的店铺信息，之前都已经存在，则认为已经取完本区域所有门店
            int shopNameNum = iterateFoodShopInfo();
            if(shopNameNum == 0){
                break;
            }

            ActionHelper.scrollForward(nodeInfo);
            // 向上滚动
            sleepMills(getRandomAction2000Mill());
            step++;
        }

    }

    /***
     * 美食-店铺列表页，获取店铺信息
     * @return
     */
    public int iterateFoodShopInfo() {
        if(!waitNeededView(DianPingTextWrapper.Food_Activity)){
            return 0;
        }
        int addNameNum = 0;
        List<AccessibilityNodeInfo> nodeList = ActionHelper.listByViewId(this.accessibilityService, DianPingTextWrapper.Food_Shop_ShopItem, null);
        for (AccessibilityNodeInfo nodeInfo : nodeList) {
            String shopName = ActionHelper.findTextById(this.accessibilityService, DianPingTextWrapper.Food_Shop_ShopItem_Title, nodeInfo);
            if(!addShopName(shopName)){
                continue;
            }
            addNameNum++;
            String shopCategory = ActionHelper.findTextById(this.accessibilityService, DianPingTextWrapper.Food_Shop_ShopItem_Category, nodeInfo);
            String shopRegionName = ActionHelper.findTextById(this.accessibilityService, DianPingTextWrapper.Food_Shop_ShopItem_RegionName, nodeInfo);
            String shopCommentNum = ActionHelper.findTextById(this.accessibilityService, DianPingTextWrapper.Food_Shop_ShopItem_CommentNum, nodeInfo);
            String shopPrice = ActionHelper.findTextById(this.accessibilityService, DianPingTextWrapper.Food_Shop_ShopItem_ShopPrice, nodeInfo);

            ActionHelper.performViewClick(this.accessibilityService, nodeInfo);
            if(!waitNeededView(DianPingTextWrapper.Food_Shop_Activity)){
                continue;
            }
            findInfoFromShopHomePage();
        }
        return addNameNum;
    }

    /***
     * 美食-店铺详情页，获取电话
     */
    public void findInfoFromShopHomePage() {

        if(!DianPingTextWrapper.Food_Shop_Activity.equals(this.accessibilityService.currentActivityClassName)){
            return;
        }

        ActionHelper.findViewIdAndClick(this.accessibilityService, DianPingTextWrapper.Food_Shop_Home_Contact, null);
        sleepMills(getRandomAction3000Mill());
        String curPackageName = this.accessibilityService.getCurrentPackageName();
        if (!StringUtils.isEmpty(curPackageName) && curPackageName.equals("com.android.contacts")) {
            String contact = ActionHelper.findTextById(this.accessibilityService, DianPingTextWrapper.System_Contact_input, null);
            Log.e(TAG, "contact =" + contact);
        } else {
            List<String> contactList = new ArrayList<>();
            ActionHelper.iterateText(this.accessibilityService, null, contactList);
            handleContactList(contactList);
            Log.e(TAG, "contact =" + contactList);
        }

        ActionHelper.performBack(this.accessibilityService);

        sleepMills(getRandomAction3000Mill());

        if(DianPingTextWrapper.Food_Shop_Activity.equals(this.accessibilityService.currentActivityClassName)){
            ActionHelper.performBack(this.accessibilityService);
        }
        sleepMills(getRandomAction3000Mill());
    }

    public void handleContactList(List<String> contactList) {
        if (CollectionUtils.isEmpty(contactList)) {
            return;
        }
        List<String> handledList = new ArrayList<>();
        for (String contact : contactList) {
            if (StringUtils.isEmpty(contact)) {
                continue;
            }
            if (contact.equals("立即在线订座")) {
                continue;
            }
            if (contact.equals("取消")) {
                continue;
            }
            handledList.add(contact);
        }
        contactList.clear();
        contactList.addAll(handledList);
    }

    public void logCurView() {
        Log.i(TAG, "cur package=" + this.accessibilityService.currentPackageName);
        Log.i(TAG, "cur class=" + this.accessibilityService.currentActivityClassName);
    }

    public boolean waitNeededView(String view) {
        if (StringUtils.isEmpty(view)) {
            return false;
        }
        Log.i(TAG, "cur class=" + this.accessibilityService.currentActivityClassName);
        boolean ret = false;
        int step = 1;
        while (step < 10) {
            if (view.equals(this.accessibilityService.currentActivityClassName)) {
                sleepMills(getRandomAction2000Mill());
                return true;
            }
            sleepMills(getRandomAction2000Mill());
            step++;
        }
        return false;
    }

    public boolean addShopName(String shopName){
        for(String sn:this.shopNameList){
            if(sn.equals(shopName)){
                return false;
            }
        }
        this.shopNameList.add(shopName);
        return true;
    }

    /***
     * 2个list合并去重，并计算增加量
     * @param curPageList
     * @return
     */
    public int concatRegionName(List<String> curPageList){
        int len = this.regionList.size();

        this.regionList.addAll(curPageList);
        HashSet h = new HashSet(this.regionList);
        this.regionList.clear();
        this.regionList.addAll(h);
        int afterLen = this.regionList.size();
        int num = afterLen - len;
        return num;
    }

    /**
     * 去掉不用的区域
     */
    public void filterInvalidRegion(){
        List<String> result = new ArrayList<>();
        for(String region:this.regionList){
            if (StringUtils.isEmpty(region)) {
                continue;
            }
            if (region.equals("附近")) {
                continue;
            }
            if (region.equals("热门商圈")) {
                continue;
            }
            result.add(region);
        }
        this.regionList.clear();
        this.regionList.addAll(result);
    }
}
