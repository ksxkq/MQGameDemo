package com.meiqia.ue.game.receiver;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.meiqia.ue.game.App;
import com.meiqia.ue.game.MyMQConversationActivity;
import com.meiqia.ue.game.SPUtil;
import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import java.util.List;

/**
 * OnePiece
 * Created by xukq on 3/8/16.
 */
public class XiaomiReceiver extends PushMessageReceiver {
    private static final String TAG = XiaomiReceiver.class.getSimpleName();


    private String mRegId;
    private long mResultCode = -1;
    private String mReason;
    private String mCommand;
    private String mMessage;
    private String mTopic;
    private String mAlias;
    private String mStartTime;
    private String mEndTime;


    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
//        mMessage = message.getContent();
//        if (!TextUtils.isEmpty(message.getTopic())) {
//            mTopic = message.getTopic();
//        } else if (!TextUtils.isEmpty(message.getAlias())) {
//            mAlias = message.getAlias();
//        }
    }


    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {
//        mMessage = message.getContent();
//        if (!TextUtils.isEmpty(message.getTopic())) {
//            mTopic = message.getTopic();
//        } else if (!TextUtils.isEmpty(message.getAlias())) {
//            mAlias = message.getAlias();
//        }
        Intent intent = new Intent(context, MyMQConversationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (App.getInstance().getActivityCount() == 2) {
            intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        }
        context.startActivity(intent);
    }


    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
        mMessage = message.getContent();
        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        }
    }


    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_UNSET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SET_ACCEPT_TIME.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mStartTime = cmdArg1;
                mEndTime = cmdArg2;
            }
        }
    }


    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);


        Log.i(TAG, cmdArg1 + " " + cmdArg2);
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                Log.i(TAG, "注册推送成功");


                mRegId = cmdArg1;


                // 设置别名
                MiPushClient.setAlias(context, mRegId, null);
                MiPushClient.setAcceptTime(context, 0, 0, 23, 59, null);


                App.getInstance().uploadPushId(mRegId);
                SPUtil.putString("mq_customizd_id", mRegId);
            }
        }
    }
}
