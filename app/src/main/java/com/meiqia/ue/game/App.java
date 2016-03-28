package com.meiqia.ue.game;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import com.meiqia.core.MQManager;
import com.meiqia.core.callback.OnClientInfoCallback;
import com.meiqia.core.callback.OnInitCallback;
import com.meiqia.meiqiasdk.util.MQConfig;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * OnePiece
 * Created by xukq on 3/8/16.
 */
public class App extends Application implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = App.class.getSimpleName();
    private static final int BACKGROUND = 0;
    private static App sInstance;
    private int mActivityState = BACKGROUND;
    private Stack<Activity> mActivityStack = new Stack<>();

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        initMeiqiaSDK();
        initMiPush();
        registerActivityLifecycleCallbacks(this);
    }

    private void initMeiqiaSDK() {
        initMQ(new OnInitCallback() {
            @Override
            public void onSuccess(String s) {

            }

            @Override
            public void onFailure(int i, String s) {
                // 初始化失败
                Toast.makeText(App.getInstance(), "init failure", Toast.LENGTH_SHORT).show();
            }
        });
        MQConfig.ui.backArrowIconResId = R.drawable.mq_ic_back_white;
        MQConfig.ui.titleBackgroundResId = R.color.orange;
        MQConfig.ui.rightChatBubbleColorResId = R.color.orange;
        MQConfig.ui.titleTextColorResId = android.R.color.white;
        MQConfig.ui.titleGravity = MQConfig.ui.MQTitleGravity.LEFT;
    }

    private void initMQ(OnInitCallback onInitCallback) {
        MQManager.init(this, "bf225f4dbdc3d413fc8fd5f94dd66bc5", onInitCallback);
        MQManager.setDebugMode(true);
    }

    private void initMiPush() {
        if (shouldInitMiPush()) {
            MiPushClient.registerPush(this, "2882303761517446654", "5761744632654");
        }
        //打开Log
        LoggerInterface newLogger = new LoggerInterface() {
            @Override
            public void setTag(String tag) {
                // ignore
                Log.d(TAG, "setTag " + tag);
            }

            @Override
            public void log(String content, Throwable t) {
                Log.d(TAG, content, t);
            }

            @Override
            public void log(String content) {
                Log.d(TAG, content);
            }
        };
        Logger.setLogger(this, newLogger);
    }

    private boolean shouldInitMiPush() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    public void uploadPushId(String regId) {
        // 上传别名到服务器
        Map<String, String> values = new HashMap<>();
        String value = Constants.KEY_SDK_PUSH_PREF + regId;
        values.put(Constants.KEY_SDK_PUSH_ID, value);
        values.put(Constants.KEY_APP_NAME, "Meiqia_Android_Game");

        MQManager.getInstance(App.getInstance()).setClientInfo(values, new OnClientInfoCallback() {
            @Override
            public void onFailure(int i, String s) {
                Log.d(TAG, "upload push id failed...");
            }

            @Override
            public void onSuccess() {
                Log.d(TAG, "upload push id success...");
            }
        });
    }

    public static App getInstance() {
        return sInstance;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        mActivityStack.add(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (isAppBackground()) {
            foregroundState(activity);
        }
        mActivityState++;
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        mActivityState--;
        // 进入后台状态
        if (isAppBackground()) {
            backgroundState();
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        mActivityStack.remove(activity);
    }

    public boolean isAppBackground() {
        return mActivityState == BACKGROUND;
    }

    /**
     * App 进入后台
     */
    private void backgroundState() {
        MQManager.getInstance(this).closeMeiqiaService();
    }

    /**
     * App 进入前台
     */
    private void foregroundState(Activity activity) {
        // 保证初始化成功才打开 socket
        initMQ(new OnInitCallback() {
            @Override
            public void onSuccess(String s) {
                MQManager.getInstance(App.getInstance()).openMeiqiaService();
            }

            @Override
            public void onFailure(int i, String s) {
                // 初始化失败
                Toast.makeText(App.getInstance(), "init failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public int getActivityCount() {
        return mActivityStack.size();
    }
}