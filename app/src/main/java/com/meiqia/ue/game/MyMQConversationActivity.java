package com.meiqia.ue.game;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.meiqia.meiqiasdk.activity.MQConversationActivity;
import com.meiqia.meiqiasdk.util.MQUtils;

/**
 * OnePiece
 * Created by xukq on 3/10/16.
 */
public class MyMQConversationActivity extends MQConversationActivity {

    ImageButton sendTextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sendTextBtn = (ImageButton) findViewById(com.meiqia.meiqiasdk.R.id.send_text_btn);

        EditText inputEt = (EditText) findViewById(com.meiqia.meiqiasdk.R.id.input_et);
        inputEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    sendTextBtn.performClick();
                    MQUtils.closeKeyboard(MyMQConversationActivity.this);
                    return true;
                }
                return false;
            }
        });

    }

}
