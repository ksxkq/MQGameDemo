package whu.iss.sric.android;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.meiqia.core.MQMessageManager;
import com.meiqia.meiqiasdk.util.MQUtils;
import com.meiqia.ue.game.MyMQConversation;
import com.meiqia.ue.game.R;
import com.meiqia.ue.game.SPUtil;

import java.util.List;

import cn.bingoogolapple.badgeview.BGABadgeImageView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import whu.iss.sric.view.GameView;
import whu.iss.sric.view.OnStateListener;
import whu.iss.sric.view.OnTimerListener;
import whu.iss.sric.view.OnToolsChangeListener;

public class WelActivity extends Activity
        implements OnClickListener, OnTimerListener, OnStateListener, OnToolsChangeListener, EasyPermissions.PermissionCallbacks {

    private static final int REQUEST_CODE_CONVERSATION_PERMISSIONS = 1;

    private ImageButton btnPlay;
    private ImageButton btnRefresh;
    private ImageButton btnTip;
    private ImageView imgTitle;
    private GameView gameView;
    private SeekBar progress;
    private MyDialog dialog;
    private ImageView clock;
    private TextView textRefreshNum;
    private TextView textTipNum;
    private BGABadgeImageView serviceIv;
    private BGABadgeImageView serviceGameIv;

    private MediaPlayer player;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    dialog = new MyDialog(WelActivity.this, gameView, "胜利", gameView.getTotalTime() - progress.getProgress());
                    dialog.show();
                    break;
                case 1:
                    dialog = new MyDialog(WelActivity.this, gameView, "失败", gameView.getTotalTime() - progress.getProgress());
                    dialog.show();
            }
        }
    };

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        btnPlay = (ImageButton) findViewById(R.id.play_btn);
        btnRefresh = (ImageButton) findViewById(R.id.refresh_btn);
        btnTip = (ImageButton) findViewById(R.id.tip_btn);
        imgTitle = (ImageView) findViewById(R.id.title_img);
        gameView = (GameView) findViewById(R.id.game_view);
        clock = (ImageView) findViewById(R.id.clock);
        progress = (SeekBar) findViewById(R.id.timer);
        textRefreshNum = (TextView) findViewById(R.id.text_refresh_num);
        textTipNum = (TextView) findViewById(R.id.text_tip_num);
        //XXX
        progress.setMax(gameView.getTotalTime());

        btnPlay.setOnClickListener(this);
        btnRefresh.setOnClickListener(this);
        btnTip.setOnClickListener(this);
        gameView.setOnTimerListener(this);
        gameView.setOnStateListener(this);
        gameView.setOnToolsChangedListener(this);
        GameView.initSound(this);

        Animation scale = AnimationUtils.loadAnimation(this, R.anim.scale_anim);
        imgTitle.startAnimation(scale);
        btnPlay.startAnimation(scale);

        player = MediaPlayer.create(this, R.raw.bg);
        player.setLooping(true);//����ѭ������
        player.start();

//        GameView.soundPlay.play(GameView.ID_SOUND_BACK2BG, -1);
        serviceIv = (BGABadgeImageView) findViewById(R.id.service_iv);
        serviceGameIv = (BGABadgeImageView) findViewById(R.id.service_game_iv);

        serviceIv.setOnClickListener(this);
        serviceGameIv.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.setMode(GameView.PAUSE);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.play_btn:
                playGame();
                break;
            case R.id.refresh_btn:
                Animation shake01 = AnimationUtils.loadAnimation(this, R.anim.shake);
                btnRefresh.startAnimation(shake01);
                gameView.refreshChange();
                break;
            case R.id.tip_btn:
                Animation shake02 = AnimationUtils.loadAnimation(this, R.anim.shake);
                btnTip.startAnimation(shake02);
                gameView.autoClear();
                break;
            case R.id.service_iv:
            case R.id.service_game_iv:
                conversationWrapper();
                break;
        }
    }

    private void playGame() {
        Animation scaleOut = AnimationUtils.loadAnimation(this, R.anim.scale_anim_out);
        Animation transIn = AnimationUtils.loadAnimation(this, R.anim.trans_in);

        btnPlay.startAnimation(scaleOut);
        btnPlay.setVisibility(View.GONE);
        serviceIv.setVisibility(View.GONE);
        imgTitle.setVisibility(View.GONE);
        gameView.setVisibility(View.VISIBLE);
        serviceGameIv.setVisibility(View.VISIBLE);

        btnRefresh.setVisibility(View.VISIBLE);
        btnTip.setVisibility(View.VISIBLE);
        progress.setVisibility(View.VISIBLE);
        clock.setVisibility(View.VISIBLE);
        textRefreshNum.setVisibility(View.VISIBLE);
        textTipNum.setVisibility(View.VISIBLE);

        btnRefresh.startAnimation(transIn);
        btnTip.startAnimation(transIn);
        gameView.startAnimation(transIn);
        player.pause();
        gameView.startPlay();
    }

    private void replayGame() {

        btnPlay.setVisibility(View.VISIBLE);
        serviceIv.setVisibility(View.VISIBLE);
        imgTitle.setVisibility(View.VISIBLE);
        gameView.setVisibility(View.GONE);
        serviceGameIv.setVisibility(View.GONE);

        btnRefresh.setVisibility(View.GONE);
        btnTip.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
        clock.setVisibility(View.GONE);
        textRefreshNum.setVisibility(View.GONE);
        textTipNum.setVisibility(View.GONE);


        player.start();
        gameView.pauseSound();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode && btnPlay.getVisibility() != View.VISIBLE) {
            replayGame();
            return false;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onTimer(int leftTime) {
        Log.i("onTimer", leftTime + "");
        progress.setProgress(leftTime);
    }

    @Override
    public void OnStateChanged(int StateMode) {
        switch (StateMode) {
            case GameView.WIN:
                handler.sendEmptyMessage(0);
                break;
            case GameView.LOSE:
                handler.sendEmptyMessage(1);
                break;
            case GameView.PAUSE:
                player.stop();
                gameView.player.stop();
                gameView.stopTimer();
                break;
            case GameView.QUIT:
                player.release();
                gameView.player.release();
                gameView.stopTimer();
                break;
        }
    }

    @Override
    public void onRefreshChanged(int count) {
        textRefreshNum.setText("" + gameView.getRefreshNum());
    }

    @Override
    public void onTipChanged(int count) {
        textTipNum.setText("" + gameView.getTipNum());
    }

    public void quit() {
        this.finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerMessageReceiver();
    }

    @Override
    protected void onStop() {
        unRegisterMessageReceiver();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        gameView.setMode(GameView.QUIT);
        super.onDestroy();
    }

    @AfterPermissionGranted(REQUEST_CODE_CONVERSATION_PERMISSIONS)
    private void conversationWrapper() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // 跳转到聊天界面前，先清空当前界面统计的未读消息数，取消监听新消息的广播
            clearCount();
            Intent intent = new Intent(WelActivity.this, MyMQConversation.class);
            startActivity(intent);
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.mq_runtime_permission_tip), REQUEST_CODE_CONVERSATION_PERMISSIONS, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onPermissionsGranted(List<String> perms) {
    }


    @Override
    public void onPermissionsDenied(List<String> perms) {
        MQUtils.show(this, R.string.mq_permission_denied_tip);
    }

    private void registerMessageReceiver() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(MQMessageManager.ACTION_NEW_MESSAGE_RECEIVED));
    }


    private void unRegisterMessageReceiver() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MQMessageManager.ACTION_NEW_MESSAGE_RECEIVED.equals(intent.getAction())) {
                int msgCount = SPUtil.getInt("msg_count");
                msgCount = msgCount + 1;
                SPUtil.putInt("msg_count", msgCount);
                addMessageCount(msgCount > 99 ? "99+" : String.valueOf(msgCount));
            }
        }
    };

    private void addMessageCount(String messageCount) {
        serviceGameIv.showTextBadge(messageCount);
        serviceIv.showTextBadge(messageCount);
    }

    private void clearCount() {
        serviceGameIv.hiddenBadge();
        serviceIv.hiddenBadge();
        SPUtil.putInt("msg_count", 0);
    }
}