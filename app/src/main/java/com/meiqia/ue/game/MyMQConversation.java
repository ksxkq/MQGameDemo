package com.meiqia.ue.game;

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
public class MyMQConversation extends MQConversationActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ImageButton sendTextBtn = (ImageButton) findViewById(com.meiqia.meiqiasdk.R.id.send_text_btn);

        EditText inputEt = (EditText) findViewById(com.meiqia.meiqiasdk.R.id.input_et);
        inputEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    sendTextBtn.performClick();
                    MQUtils.closeKeyboard(MyMQConversation.this);
                    return true;
                }
                return false;
            }
        });

    }
}
