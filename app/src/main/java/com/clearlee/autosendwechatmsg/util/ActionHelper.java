package com.clearlee.autosendwechatmsg.util;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.StringUtils;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * author : linzhiji
 * date   : 2020/12/10下午8:41
 * desc   :
 * version: 1.0
 */
public class ActionHelper {
    private static final String TAG = "ActionHelper";

    static public AccessibilityNodeInfo findClickableNode(AccessibilityNodeInfo node) {
        if (null != node && node.isClickable()) {
            return node;
        } else {
            return findClickableNode(node.getParent());
        }
    }

    static public void resetAndReturnApp(AccessibilityService accessibilityService) {
//        hasSend = true;
        if (null == accessibilityService) {
            return;
        }
        ActivityManager activityManager = (ActivityManager) accessibilityService.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(3);
        for (ActivityManager.RunningTaskInfo runningTaskInfo : runningTaskInfos) {
            if (accessibilityService.getPackageName().equals(runningTaskInfo.topActivity.getPackageName())) {
                activityManager.moveTaskToFront(runningTaskInfo.id, ActivityManager.MOVE_TASK_WITH_HOME);
                return;
            }
        }
    }

    /**
     * 在当前页面查找文字内容并点击
     *
     * @param text
     */
    public static boolean findTextAndClick(AccessibilityService accessibilityService, String text, AccessibilityNodeInfo accessibilityNodeInfo) {
        boolean ret = false;
        if (null == accessibilityNodeInfo) {
            accessibilityNodeInfo = accessibilityService.getRootInActiveWindow();
        }
        if (accessibilityNodeInfo == null) {
            return false;
        }
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null && (text.equals(nodeInfo.getText()) || text.equals(nodeInfo.getContentDescription()))) {
                    ret = performClick(nodeInfo);
                    break;
                }
            }
        }
        return ret;
    }

    public static AccessibilityNodeInfo findViewById(AccessibilityService accessibilityService, String id, AccessibilityNodeInfo rootNodeInfo) {
        if (null == rootNodeInfo) {
            rootNodeInfo = accessibilityService.getRootInActiveWindow();
        }
        if (rootNodeInfo == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfoList = rootNodeInfo.findAccessibilityNodeInfosByViewId(id);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null) {
                    return nodeInfo;
                }
            }
        }
        return null;
    }

    /**
     * 检查viewId进行点击
     *
     * @param accessibilityService
     * @param id
     */
    public static void findViewIdAndClick(AccessibilityService accessibilityService, String id, AccessibilityNodeInfo rootNodeInfo) {
        if (null == rootNodeInfo) {
            rootNodeInfo = accessibilityService.getRootInActiveWindow();
        }
        if (rootNodeInfo == null) {
            return;
        }
        List<AccessibilityNodeInfo> nodeInfoList = rootNodeInfo.findAccessibilityNodeInfosByViewId(id);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null) {
                    performClick(nodeInfo);
                    break;
                }
            }
        }
    }

    public static List<AccessibilityNodeInfo> listByViewId(AccessibilityService accessibilityService, String id, AccessibilityNodeInfo accessibilityNodeInfo) {
        if (null == accessibilityNodeInfo) {
            accessibilityNodeInfo = accessibilityService.getRootInActiveWindow();
        }

        if (accessibilityNodeInfo == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(id);
        return nodeInfoList;
    }

    public static List<String> listTextByViewId(AccessibilityService accessibilityService, String id, AccessibilityNodeInfo accessibilityNodeInfo) {
        List<AccessibilityNodeInfo> nodeInfoList = listByViewId(accessibilityService, id, accessibilityNodeInfo);
        if (CollectionUtils.isEmpty(nodeInfoList)) {
            return null;
        }

        List<String> strList = new ArrayList<>();
        for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
            CharSequence charSequence = nodeInfo.getText();
            if (null != charSequence) {
                String text = charSequence.toString();
                if (!StringUtils.isEmpty(text)) {
                    strList.add(text);
                }
            }
        }
        return strList;
    }

    public static boolean findViewByIdAndPasteContent(AccessibilityService accessibilityService, String id, String content) {
        AccessibilityNodeInfo rootNode = accessibilityService.getRootInActiveWindow();
        if (rootNode != null) {
            List<AccessibilityNodeInfo> editInfo = rootNode.findAccessibilityNodeInfosByViewId(id);
            if (editInfo != null && !editInfo.isEmpty()) {
                Bundle arguments = new Bundle();
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, content);
                editInfo.get(0).performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                return true;
            }
            return false;
        }
        return false;
    }

    public static String findTextById(AccessibilityService accessibilityService, String id, AccessibilityNodeInfo rootInfo) {
        if (null == rootInfo) {
            rootInfo = accessibilityService.getRootInActiveWindow();
        }
        if (rootInfo != null) {
            List<AccessibilityNodeInfo> userNames = rootInfo.findAccessibilityNodeInfosByViewId(id);
            if (userNames != null && userNames.size() > 0) {
                String name = userNames.get(0).getText().toString();
                return name;
            }
        }
        return null;
    }


    public static void iterateText(AccessibilityService accessibilityService, AccessibilityNodeInfo rootInfo, List<String> strList) {
        if (null == rootInfo) {
            rootInfo = accessibilityService.getRootInActiveWindow();
        }
        travesalChild(rootInfo, strList);
    }

    public static void travesalChild(AccessibilityNodeInfo parent, List<String> strList) {

        if (parent == null) {
            return;
        }

        for (int i = 0; i < parent.getChildCount(); i++) {
            AccessibilityNodeInfo child = parent.getChild(i);
            if (child == null) {
                continue;
            }
            CharSequence charSequence = child.getText();
            if (null != charSequence) {
                String text = child.getText().toString();
                if (!StringUtils.isEmpty(text)) {
                    strList.add(text);
                }
            }
            travesalChild(child, strList);
        }
    }

    /**
     * 在当前页面查找对话框文字内容并点击
     *
     * @param text1 默认点击text1
     * @param text2
     */
    public static void findDialogAndClick(AccessibilityService accessibilityService, String text1, String text2) {

        AccessibilityNodeInfo accessibilityNodeInfo = accessibilityService.getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return;
        }

        List<AccessibilityNodeInfo> dialogWait = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text1);
        List<AccessibilityNodeInfo> dialogConfirm = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text2);
        if (!dialogWait.isEmpty() && !dialogConfirm.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : dialogWait) {
                if (nodeInfo != null && text1.equals(nodeInfo.getText())) {
                    performClick(nodeInfo);
                    break;
                }
            }
        }

    }

    //模拟点击事件
    public static boolean performClick(AccessibilityNodeInfo nodeInfo) {
        boolean ret = false;
        if (nodeInfo == null) {
            return ret;
        }
        if (nodeInfo.isClickable()) {
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            ret = true;
        } else {
            ret = performClick(nodeInfo.getParent());
        }
        return ret;
    }

    //模拟返回事件
    public static void performBack(AccessibilityService service) {
        if (service == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
        }
    }

    /**
     * 判断当前app是否为
     **/
    public static boolean isHitApp(AccessibilityService accessibilityService, String packageName) {
        if (accessibilityService != null) {
            String currentPackageName = accessibilityService.getPackageName();
            if (!StringUtils.isEmpty(currentPackageName) && currentPackageName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 启动app
     **/
    public static void startApp(AccessibilityService accessibilityService, String packageName, String className) {
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            ComponentName cmp = new ComponentName(packageName, className);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cmp);
            if (accessibilityService != null) {
                accessibilityService.startActivity(intent);
            }
        } catch (ActivityNotFoundException e) {
            // TODO: handle exception
        }
    }

    /**
     * @param tfs 递归查找控件 checkOk 方法进行匹配 会忽略IdTF和TextTF（checkOk方法为空）
     */
    private static AccessibilityNodeInfo findFirstRecursive(AccessibilityNodeInfo parent, @NonNull FindViewNode... tfs) {
        if (parent == null) return null;
        if (tfs.length == 0) throw new InvalidParameterException("AbstractTF不允许传空");

        for (int i = 0; i < parent.getChildCount(); i++) {
            AccessibilityNodeInfo child = parent.getChild(i);
            if (child == null) continue;
            boolean isOk = true;
            for (FindViewNode tf : tfs) {
                if (!tf.checkOk(child)) {
                    isOk = false;
                    break;
                }
            }
            if (isOk) {
                return child;
            } else {
                AccessibilityNodeInfo childChild = findFirstRecursive(child, tfs);
                child.recycle();
                if (childChild != null) {
                    return childChild;
                }
            }
        }
        return null;
    }


    public static AccessibilityNodeInfo findFirst(AccessibilityService service, FindViewNode... tfs) {
        if (service == null) {
            Log.e(TAG, TAG + "----service is null");
            return null;
        }
        if (tfs.length == 0) {
            Log.e(TAG, TAG + "----FindViewNode is null");
            return null;
        }
        AccessibilityNodeInfo rootInfo = service.getRootInActiveWindow();
        if (rootInfo == null) {
            Log.e(TAG, TAG + "----AccessibilityService find rootNoteInfo is null");
            return null;
        }
        int idTextTFCount = 0, idTextIndex = 0;
        for (int i = 0; i < tfs.length; i++) {
            //统计采用 id和Text方式 匹配的数量
            if (tfs[i] instanceof FindViewNode.IdTextTF) {
                idTextTFCount++;
                idTextIndex = i;
            }
        }
        switch (idTextTFCount) {
            case 0://id或text数量为0，直接递归查找
                AccessibilityNodeInfo returnInfo = findFirstRecursive(rootInfo, tfs);
                rootInfo.recycle();
                return returnInfo;
            case 1://id或text数量为1，先查出对应的id或text，然后匹配其他条件
                if (tfs.length == 1) {
                    AccessibilityNodeInfo returnInfo2 = ((FindViewNode.IdTextTF) tfs[idTextIndex]).findFirst(rootInfo);
                    rootInfo.recycle();
                    return returnInfo2;
                } else {
                    List<AccessibilityNodeInfo> listIdText = ((FindViewNode.IdTextTF) tfs[idTextIndex]).findAll(rootInfo);
                    if (Tool.isEmpty(listIdText)) {
                        break;
                    }
                    AccessibilityNodeInfo returnInfo3 = null;
                    for (AccessibilityNodeInfo info : listIdText) {//遍历找到匹配的
                        if (returnInfo3 == null) {
                            boolean isOk = true;
                            for (FindViewNode tf : tfs) {
                                if (!tf.checkOk(info)) {
                                    isOk = false;
                                    break;
                                }
                            }
                            if (isOk) {
                                returnInfo3 = info;
                            } else {
                                info.recycle();
                            }
                        } else {
                            info.recycle();
                        }
                    }
                    rootInfo.recycle();
                    return returnInfo3;
                }
            default:
                throw new RuntimeException("id和Text只能有一个");//要么通过id查找要么通过text查找 除了id和Text外 可以同时匹配其他属性 比如 className属性 FindViewNode.newClassName
        }
        rootInfo.recycle();
        return null;
    }

    /**
     * 根据元素 id查找指定的元素
     **/
    public static AccessibilityNodeInfo selectNodeInfoById(AccessibilityService accessibilityService, String noteInfoId) {
        if (accessibilityService != null) {
            return findFirst(accessibilityService, FindViewNode.newId(noteInfoId));
        }
        return null;
    }

    /**
     * sleep
     **/
    public static void sleepMills(long mills) {
        try {
            Thread.sleep(mills);
        } catch (Exception e) {

        }
    }

    /**
     * 模拟点击事件
     *
     * @param nodeInfo nodeInfo
     */
    public static boolean performViewClick(AccessibilityService accessibilityService, AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) {
            return false;
        }
        while (nodeInfo != null) {
            if (nodeInfo.isClickable()) {
                try {
                    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    sleepMills(600);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
            nodeInfo = nodeInfo.getParent();
        }
        return false;
    }

    public static boolean performViewClick(AccessibilityService accessibilityService, String info) {
        AccessibilityNodeInfo nodeInfo = selectNodeInfoById(accessibilityService, info);
        if (null == nodeInfo) {
            return false;
        }
        return performViewClick(accessibilityService, nodeInfo);
    }

    /**
     * 上滑
     **/
    public static void scrollForward(AccessibilityNodeInfo node) {
        if (node == null || !node.isScrollable())
            return;
        node.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
        sleepMills(Tool.getSecMills(1));
    }

    /**
     * 下滑
     **/
    public static void scrollBackward(AccessibilityNodeInfo node) {
        if (node == null || !node.isScrollable()) return;
        node.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
        sleepMills(Tool.getSecMills(1));
    }

    // https://blog.csdn.net/littlefishvc/article/details/80057841
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void adbClick(AccessibilityService accessibilityService,Rect rect) {
        Log.d(TAG, "printTree: bound:" + rect);
        Point position = new Point(rect.left + 10, rect.top + 10);
        GestureDescription.Builder builder = new GestureDescription.Builder();
        Path p = new Path();
        p.moveTo(position.x, position.y);
        builder.addStroke(new GestureDescription.StrokeDescription(p, 0L, 100L));
        GestureDescription gesture = builder.build();
        boolean isDispatched = accessibilityService.dispatchGesture(gesture, new AccessibilityService.GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                Log.d(TAG, "onCompleted: 完成..........");
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
                Log.d(TAG, "onCompleted: 取消..........");
            }
        }, null);

    }


}
