package com.clearlee.autosendwechatmsg.util;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
/**
 * Create by WangTengFei on 2020/11/17 11:30
 * 通过AccessibilityService查找控件 条件匹配
 * **/
public abstract class FindViewNode<T> {

    protected final T mCheckData;//查找时所提供的条件 比如：提供id、提供text根据这些条件进行查找

    protected final boolean mIsEquals;//指定查找精确度 ：对于找到的控件是 必须和提供的条件相等 还是 包含关系

    private static Rect mRecycleRect = new Rect();

    public static final String
            ST_VIEW = "android.view.View",
            ST_TEXTVIEW = "android.widget.TextView",
            ST_IMAGEVIEW = "android.widget.ImageView",
            ST_BUTTON = "android.widget.Button",
            ST_IMAGEBUTTON = "android.widget.ImageButton",
            ST_EDITTEXT = "android.widget.EditText",
            ST_LISTVIEW = "android.widget.ListView",
            ST_LINEARLAYOUT = "android.widget.LinearLayout",
            ST_VIEWGROUP = "android.view.ViewGroup",
            ST_SYSTEMUI = "com.android.systemui";

    private FindViewNode(@NonNull T checkData, boolean isEquals) {
        mCheckData = checkData;
        mIsEquals = isEquals;
    }

    //根据ID或者Text查找控件 符合条件的有一个或多个
    public interface IdTextTF {
        @Nullable
        //返回符合条件的所以内容中的第一个
        AccessibilityNodeInfo findFirst(AccessibilityNodeInfo root);
        //返回所有符合条件的内容
        @Nullable
        List<AccessibilityNodeInfo> findAll(AccessibilityNodeInfo root);
    }

    //根据提供的条件进行精确匹配
    public abstract boolean checkOk(AccessibilityNodeInfo thisInfo);


    /**
     * @param idFullName id全称:com.android.xxx:id/tv_main
     */
    public static FindViewNode newId(@NonNull String idFullName) {
        return new IdTF(idFullName);
    }

    /**
     * 根据id进行查找，就是findAccessibilityNodeInfosByViewId方法
     * 和找text一样效率最高，如果能找到，尽量使用这个 使用根据id或Text使用一个即可
     * @param pageName 被查找项目的包名:com.android.xxx
     * @param idName   id值:tv_main
     */
    public static FindViewNode newId(String pageName, String idName) {
        return newId(pageName + ":id/" + idName);
    }

    /**
     * 根据text进行查找，就是findAccessibilityNodeInfosByText方法
     * 和找id一样效率最高，如果能找到，尽量使用这个 使用根据id或Text使用一个即可
     */
    public static FindViewNode newText(@NonNull String text, boolean isEquals) {
        return new TextTF(text, isEquals);
    }

    /**
     * 类似uc浏览器，有text值但无法直接根据text来找到
     */
    public static FindViewNode newWebText(@NonNull String webText, boolean isEquals) {
        return new WebTextTF(webText, isEquals);
    }

    /**
     * 根据控件的ContentDescription属性 进行查找
     */
    public static FindViewNode newContentDescription(@NonNull String cd, boolean isEquals) {
        return new ContentDescriptionTF(cd, isEquals);
    }

    /**
     * 根据ClassName进行查找
     */
    public static FindViewNode newClassName(@NonNull String className) {
        return new ClassNameTF(className, true);
    }

    public static FindViewNode newClassName(@NonNull String className, boolean isEquals) {
        return new ClassNameTF(className, isEquals);
    }

    /**
     * 根据某区域
     * 查找某个区域内的控件
     */
    public static FindViewNode newRect(@NonNull Rect rect) {
        return new RectTF(rect);
    }


    /**
     * 根据id查找，就是findAccessibilityNodeInfosByViewId方法
     * 和找text一样效率最高，如果能找到，尽量使用这个
     */
    private static class IdTF extends FindViewNode<String> implements IdTextTF {
        private IdTF(@NonNull String idFullName) {
            super(idFullName, true);
        }

        @Override
        public boolean checkOk(AccessibilityNodeInfo thisInfo) {
            return true;//此处暂时不需要实现 直接根据Id查找就好 不需要最后在根据ID匹配
        }

        @Nullable
        @Override
        public AccessibilityNodeInfo findFirst(AccessibilityNodeInfo root) {
            List<AccessibilityNodeInfo> list = root.findAccessibilityNodeInfosByViewId(mCheckData);
            if (Tool.isEmpty(list)) {
                return null;
            }
            for (int i = 1; i < list.size(); i++) {//其他的均回收
                list.get(i).recycle();
            }
            return list.get(0);
        }

        @Nullable
        @Override
        public List<AccessibilityNodeInfo> findAll(AccessibilityNodeInfo root) {
            return root.findAccessibilityNodeInfosByViewId(mCheckData);
        }
    }

    /**
     * 普通text，就是findAccessibilityNodeInfosByText方法
     * 和找id一样效率最高，如果能找到，尽量使用这个
     */
    private static class TextTF extends FindViewNode<String> implements IdTextTF {
        private TextTF(@NonNull String text, boolean isEquals) {
            super(text, isEquals);
        }

        @Override
        public boolean checkOk(AccessibilityNodeInfo thisInfo) {
            return true;//此处暂时不需要实现 直接根据Text查找就好 不需要最后在进行Text匹配 Text查找和Id查找效率相同
        }

        @Nullable
        @Override
        public AccessibilityNodeInfo findFirst(AccessibilityNodeInfo root) {
            List<AccessibilityNodeInfo> list = root.findAccessibilityNodeInfosByText(mCheckData);
            if (Tool.isEmpty(list)) {
                return null;
            }
            if (mIsEquals) {
                AccessibilityNodeInfo returnInfo = null;
                for (AccessibilityNodeInfo info : list) {
                    if (returnInfo == null && info.getText() != null && mCheckData.equals(info.getText().toString())) {
                        returnInfo = info;
                    } else {
                        info.recycle();
                    }
                }
                return returnInfo;
            } else {
                return list.get(0);
            }
        }

        @Nullable
        @Override
        public List<AccessibilityNodeInfo> findAll(AccessibilityNodeInfo root) {
            List<AccessibilityNodeInfo> list = root.findAccessibilityNodeInfosByText(mCheckData);
            if (Tool.isEmpty(list)) {
                return null;
            }
            if (mIsEquals) {
                ArrayList<AccessibilityNodeInfo> listNew = new ArrayList<>();
                for (AccessibilityNodeInfo info : list) {
                    if (info.getText() != null && mCheckData.equals(info.getText().toString())) {
                        listNew.add(info);
                    } else {
                        info.recycle();
                    }
                }
                return listNew;
            } else {
                return list;
            }
        }
    }

    /**
     * 类似uc浏览器，有text值但无法直接根据text来找到
     * 通常做法：用 id或者Text字段查找到多个控件 然后再根据这个字段进行精确匹配
     */
    private static class WebTextTF extends FindViewNode<String> {
        private WebTextTF(@NonNull String checkString, boolean isEquals) {
            super(checkString, isEquals);
        }

        @Override
        public boolean checkOk(AccessibilityNodeInfo thisInfo) {
            CharSequence text = thisInfo.getText();
            if (mIsEquals) {
                return text != null && text.toString().equals(mCheckData);
            } else {
                return text != null && text.toString().contains(mCheckData);
            }
        }
    }

    /**
     * 根据 ContentDescription字段查找匹配的控件
     * 通常做法：用 id或者Text字段查找到多个控件 然后再根据这个字段进行精确匹配
     */
    private static class ContentDescriptionTF extends FindViewNode<String> {
        private ContentDescriptionTF(@NonNull String checkString, boolean isEquals) {
            super(checkString, isEquals);
        }

        @Override
        public boolean checkOk(AccessibilityNodeInfo thisInfo) {
            CharSequence text = thisInfo.getContentDescription();
            if (mIsEquals) {
                return text != null && text.toString().equals(mCheckData);
            } else {
                return text != null && text.toString().contains(mCheckData);
            }
        }
    }

    /**
     * 根据ClassName查找
     * 通常做法：用 id或者Text字段查找到多个控件 然后再根据这个字段进行精确匹配
     */
    private static class ClassNameTF extends FindViewNode<String> {
        public ClassNameTF(@NonNull String checkString, boolean isEquals) {
            super(checkString, isEquals);
        }

        @Override
        public boolean checkOk(AccessibilityNodeInfo thisInfo) {
            if (mIsEquals) {
                return thisInfo.getClassName().toString().equals(mCheckData);
            } else {
                return thisInfo.getClassName().toString().contains(mCheckData);
            }
        }
    }

    /**
     * 根据提供的区域进行查找
     * 查找某个区域内容的内容
     */
    private static class RectTF extends FindViewNode<Rect> {
        public RectTF(@NonNull Rect rect) {
            super(rect, true);
        }

        @Override
        public boolean checkOk(AccessibilityNodeInfo thisInfo) {
            thisInfo.getBoundsInScreen(mRecycleRect);
            return mCheckData.contains(mRecycleRect);
        }
    }
}