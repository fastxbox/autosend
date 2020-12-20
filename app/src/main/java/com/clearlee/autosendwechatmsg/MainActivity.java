package com.clearlee.autosendwechatmsg;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.clearlee.autosendwechatmsg.constant.SendStatus;
import com.clearlee.autosendwechatmsg.dianping.AutoActionDianPing;
import com.clearlee.autosendwechatmsg.dianping.DianPingTextWrapper;
import com.clearlee.autosendwechatmsg.wework.AutoActionWeWork;
import com.clearlee.autosendwechatmsg.wework.WeChatTextWrapper;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.clearlee.autosendwechatmsg.wework.WechatUtils.CONTENT;
import static com.clearlee.autosendwechatmsg.wework.WechatUtils.NAME;

//import static com.clearlee.autosendwechatmsg.AutoSendMsgService.SEND_STATUS;
//import static com.clearlee.autosendwechatmsg.AutoSendMsgService.SEND_SUCCESS;
//import static com.clearlee.autosendwechatmsg.AutoSendMsgService.hasSend;

/**
 * Created by Clearlee
 * 2017/12/22.
 */
public class MainActivity extends AppCompatActivity {

    //    private TextView start, sendStatus, sendMultiText, sendWechatPhoto, sendSysSetBtn, checkMsgBtn;
    private TextView sendStatus, sendSysSetBtn, sendWework, sendDianping;
    private EditText sendName, sendContent;
    private AccessibilityManager accessibilityManager;
    private String name, content;
    private Boolean stopLoop = false;
    private static final String TAG = "MainActivity";
    private Spinner weworkSpinner;
    private Spinner dianpingSpinner;
    private static final String[] weworkList = AutoActionWeWork.listName().toArray(new String[0]);
    private static final String[] dianpingList = AutoActionDianPing.listName().toArray(new String[0]);
    private ArrayAdapter<String> weworkAdapter;
    private ArrayAdapter<String> dianpingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        sendSysSetBtn = (TextView) findViewById(R.id.sendSysSet);

        sendName = (EditText) findViewById(R.id.sendName);
        sendContent = (EditText) findViewById(R.id.sendContent);
        sendStatus = (TextView) findViewById(R.id.sendStatus);
        sendWework = (TextView) findViewById(R.id.sendWework);
        sendDianping = (TextView) findViewById(R.id.sendDianping);
        initWeworkSpinner();
        initDianpingSpinner();

        sendWework.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selected = weworkSpinner.getSelectedItemPosition();
                if (selected == AutoActionWeWork.sendOnceText.getType()) {
                    WeChatTextWrapper.autoActionWeWork = AutoActionWeWork.sendOnceText;
                } else if (selected == AutoActionWeWork.sendOnceImage.getType()) {
                    WeChatTextWrapper.autoActionWeWork = AutoActionWeWork.sendOnceImage;
                } else if (selected == AutoActionWeWork.sendMultiText.getType()) {
                    if (WeChatTextWrapper.autoActionWeWork == AutoActionWeWork.sendMultiText) {
                        WeChatTextWrapper.sendStatus = SendStatus.send_done;
                        stopLoop = true;
                        WeChatTextWrapper.autoActionWeWork = AutoActionWeWork.none;
                        sendWework.setText("开始");
                    } else {
                        WeChatTextWrapper.sendStatus = SendStatus.send_waiting;
                        WeChatTextWrapper.autoActionWeWork = AutoActionWeWork.sendMultiText;
                        stopLoop = false;
                        sendWework.setText("自动营销中");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                randomSendText();
                            }
                        }).start();
                    }
                } else if (selected == AutoActionWeWork.checkMsg.getType()) {
                    if (WeChatTextWrapper.autoActionWeWork == AutoActionWeWork.checkMsg) {
                        WeChatTextWrapper.sendStatus = SendStatus.send_done;
                        stopLoop = true;
                        WeChatTextWrapper.autoActionWeWork = AutoActionWeWork.none;
                        sendWework.setText("开始");
                    } else {
                        WeChatTextWrapper.sendStatus = SendStatus.send_waiting;
                        WeChatTextWrapper.autoActionWeWork = AutoActionWeWork.checkMsg;
                        stopLoop = false;
                        sendWework.setText("检查消息中");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                checkMsg();
                            }
                        }).start();
                    }
                }

                if (WeChatTextWrapper.autoActionWeWork != AutoActionWeWork.none) {
                    Toast.makeText(MainActivity.this, "选中" + WeChatTextWrapper.autoActionWeWork.getName(), Toast.LENGTH_SHORT);
                    checkAndStartService();
                }
            }
        });

        sendDianping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selected = dianpingSpinner.getSelectedItemPosition();
                if (selected == AutoActionDianPing.fetchFoodShopInfo.getType()) {
                    DianPingTextWrapper.autoActionDianping = AutoActionDianPing.fetchFoodShopInfo;
                }
                if (DianPingTextWrapper.autoActionDianping != AutoActionDianPing.none) {
                    Toast.makeText(MainActivity.this, "选中" + WeChatTextWrapper.autoActionWeWork.getName(), Toast.LENGTH_SHORT);
                }
            }
        });

        sendSysSetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openService();
            }
        });
    }

    public void initWeworkSpinner() {
        weworkSpinner = (Spinner) findViewById(R.id.spinnerWeWork);
        //将可选内容与ArrayAdapter连接起来
        weworkAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, weworkList);

        //设置下拉列表的风格
        weworkAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //将adapter 添加到spinner中
        weworkSpinner.setAdapter(weworkAdapter);

        //设置默认值
        weworkSpinner.setVisibility(View.VISIBLE);
    }

    public void initDianpingSpinner() {
        dianpingSpinner = (Spinner) findViewById(R.id.spinnerDianping);
        //将可选内容与ArrayAdapter连接起来
        dianpingAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dianpingList);

        //设置下拉列表的风格
        dianpingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //将adapter 添加到spinner中
        dianpingSpinner.setAdapter(dianpingAdapter);

        //设置默认值
        dianpingSpinner.setVisibility(View.VISIBLE);
    }

    private int goWechat() {
        try {
            setValue(name, content);
            WeChatTextWrapper.sendStatus = SendStatus.send_waiting;
            Intent intent = new Intent();
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            intent.setClassName(WeChatTextWrapper.PACKAGENAME, WeChatTextWrapper.WechatClass.WECHAT_CLASS_LAUNCHUI);
            startActivity(intent);

            while (true) {
                if (WeChatTextWrapper.sendStatus == SendStatus.send_done) {
                    return SendStatus.send_done.ordinal();
                } else {
                    try {
                        Thread.sleep(500);
                    } catch (Exception e) {
                        openService();
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return WeChatTextWrapper.sendStatus.ordinal();
        }
    }

    private void openService() {
        try {
            //打开系统设置中辅助功能
            Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
            Toast.makeText(MainActivity.this, "找到微信自动发送消息，然后开启服务即可", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkAndStartService() {
        accessibilityManager = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
        name = sendName.getText().toString();
        content = sendContent.getText().toString();
        WeChatTextWrapper.sendStatus = SendStatus.send_doing;
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(MainActivity.this, "联系人不能为空", Toast.LENGTH_SHORT);
        }
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(MainActivity.this, "内容不能为空", Toast.LENGTH_SHORT);
        }

        if (!accessibilityManager.isEnabled()) {
            openService();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    statusHandler.sendEmptyMessage(goWechat());
                }
            }).start();
        }
    }

    Handler statusHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            setSendStatusText(msg.what);

            if (WeChatTextWrapper.autoActionWeWork != AutoActionWeWork.sendMultiText)
                WeChatTextWrapper.autoActionWeWork = AutoActionWeWork.none;
        }
    };

    private void setSendStatusText(int status) {
        if (status == SendStatus.send_done.ordinal()) {
            sendStatus.setText("微信发送成功");
        } else {
            sendStatus.setText("微信发送失败");
        }
    }

    public void setValue(String name, String content) {
        NAME = name;
        CONTENT = content;
//        hasSend = false;
    }


    private void randomSendText() {
        if (WeChatTextWrapper.sendStatus != SendStatus.send_waiting) {
            return;
        } else {
            checkAndStartService();
        }

        while (true) {
            if (stopLoop) {
                break;
            }
            try {
                Thread.sleep(10000);
                WeChatTextWrapper.sendStatus = SendStatus.send_waiting;
                checkAndStartService();
            } catch (Exception e) {
            }
        }
    }

    private void checkMsg() {
        if (WeChatTextWrapper.sendStatus != SendStatus.send_waiting) {
            return;
        } else {
            checkAndStartService();
        }

        while (true) {
            if (stopLoop) {
                break;
            }
            WeChatTextWrapper.autoActionWeWork = AutoActionWeWork.checkMsg;
            try {
                Thread.sleep(5000);
                WeChatTextWrapper.sendStatus = SendStatus.send_waiting;
                checkAndStartService();
            } catch (Exception e) {
            }
        }
    }
}
