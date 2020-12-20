package com.clearlee.autosendwechatmsg.util;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * Create by WangTengFei on 2020/11/17 11:30
 * 通过 AccessibilityService查找APP中指定的View
 */
public class FindViewHelper {

    private static final String TAG = "FindViewHelper";

    /**
     * 在找到所有匹配控件列表中 查找第一个匹配的控件即可
     *
     * @param tfs 匹配条件，多个AbstractTF是&&的关系， 注意： id和text查找同时只能有一个
     *            如：
     *            AbstractTF.newContentDescription("表情", true),AbstractTF.newClassName(AbstractTF.ST_IMAGEVIEW)
     *            表示描述内容是'表情'并且是imageview的控件
     */
    @Nullable
    public AccessibilityNodeInfo findFirst(AccessibilityService service, FindViewNode... tfs) {
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
     * 将所有和匹配条件匹配的控件全部返回
     *
     * @param tfs 匹配条件，多个AbstractTF是&&的关系，如：
     *            AbstractTF.newContentDescription("表情", true),AbstractTF.newClassName(AbstractTF.ST_IMAGEVIEW)
     *            表示描述内容是'表情'并且是imageview的控件
     */
    @NonNull
    public List<AccessibilityNodeInfo> findAll(AccessibilityService service, @NonNull FindViewNode... tfs) {
        if (service == null) {
            Log.e(TAG, TAG + "----service is null");
            return null;
        }
        if (tfs.length == 0) {
            Log.e(TAG, TAG + "----FindViewNode is null");
            return null;
        }

        ArrayList<AccessibilityNodeInfo> list = new ArrayList<>();
        AccessibilityNodeInfo rootInfo = service.getRootInActiveWindow();
        if (rootInfo == null) return list;

        int idTextTFCount = 0, idTextIndex = 0;
        for (int i = 0; i < tfs.length; i++) {
            if (tfs[i] instanceof FindViewNode.IdTextTF) {
                idTextTFCount++;
                idTextIndex = i;
            }
        }
        switch (idTextTFCount) {
            case 0://id或text数量为0，直接循环查找
                findAllRecursive(list, rootInfo, tfs);
                break;
            case 1://id或text数量为1，先查出对应的id或text，然后再循环
                List<AccessibilityNodeInfo> listIdText = ((FindViewNode.IdTextTF) tfs[idTextIndex]).findAll(rootInfo);
                if (Tool.isEmpty(listIdText)) {
                    break;
                }
                if (tfs.length == 1) {
                    list.addAll(listIdText);
                } else {
                    for (AccessibilityNodeInfo info : listIdText) {
                        boolean isOk = true;
                        for (FindViewNode tf : tfs) {
                            if (!tf.checkOk(info)) {
                                isOk = false;
                                break;
                            }
                        }
                        if (isOk) {
                            list.add(info);
                        } else {
                            info.recycle();
                        }
                    }
                }
                break;
            default:
                throw new RuntimeException("由于时间有限，并且多了也没什么用，所以IdTF和TextTF只能有一个");
        }
        rootInfo.recycle();
        return list;
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

    /**
     * @param tfs 由于是递归循环，会忽略IdTF和TextTF
     */
    private static void findAllRecursive(List<AccessibilityNodeInfo> list, AccessibilityNodeInfo parent, @NonNull FindViewNode... tfs) {
        if (parent == null || list == null) return;
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
                list.add(child);
            } else {
                findAllRecursive(list, child, tfs);
                child.recycle();
            }
        }
    }
}
