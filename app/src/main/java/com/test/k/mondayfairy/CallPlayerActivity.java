package com.test.k.mondayfairy;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.icu.text.LocaleDisplayNames;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;
import com.google.android.exoplayer2.util.Util;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class CallPlayerActivity extends AppCompatActivity implements View.OnClickListener, Player.EventListener {
    private static final String TAG = CallPlayerActivity.class.getSimpleName();
    private final int CALL_OFF_WAIT_TIME = 12000;//millisecond
    private final int START_PLAY_POSITON = 3800;//millisecond
    public static final Uri MP4_URI = Uri.parse("");
    private PlayerView playerView;
    private SimpleExoPlayer player;
    private Ringtone ringtone;
    private Vibrator vibrator;
    private ImageView thumbnailView;
    private ImageButton callPlayBtn;
    private ImageButton callOffBtn;
    private Timer timer;
    private Bitmap userPicture;
    private boolean ringToneStart=true;
    AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WakeLockUtil.acquireCpuWakeLock(this);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_call_player);
        playerView = findViewById(R.id.player_view);
        //player controller hide
        playerView.setUseController(false);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        callPlayBtn = findViewById(R.id.call_play_btn);
        callOffBtn = findViewById(R.id.call_off_btn);
        callPlayBtn.setOnClickListener(this);
        callOffBtn.setOnClickListener(this);
        timer = new Timer();
    }

    @Override
    protected void onStart() {
        super.onStart();

        initPlayer();

    }


    @Override

    protected void onStop() {

        playerView.setPlayer(null);

        player.release();

        player = null;

        if (ringtone != null && ringtone.isPlaying()) {

            ringtone.stop();

            ringtone = null;

        }

        if (vibrator != null) {

            vibrator.cancel();

            vibrator = null;

        }
        //배터리 관리를 위해 사용이 끝나면 wakeLock 해제
        WakeLockUtil.releaseCpuWakeLock();
        super.onStop();

    }


    @Override

    protected void onDestroy() {

        super.onDestroy();

    }


    @Override

    public void onWindowFocusChanged(boolean hasFocus) {

        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {

            hideSystemUI();

        }

    }


    private void hideSystemUI() {

        // Enables regular immersive mode.

        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.

        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        View decorView = getWindow().getDecorView();

        decorView.setSystemUiVisibility(

                View.SYSTEM_UI_FLAG_IMMERSIVE

                        // Set the content to appear under the system bars so that the

                        // content doesn't resize when the system bars hide and show.

                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE

                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

                        // Hide the nav bar and status bar

                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

    }


    // Shows the system bars by removing all the flags

    // except for the ones that make the content appear under the system bars.

    private void showSystemUI() {

        View decorView = getWindow().getDecorView();

        decorView.setSystemUiVisibility(

                View.SYSTEM_UI_FLAG_LAYOUT_STABLE

                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.call_play_btn:
                player.seekTo(START_PLAY_POSITON);
                player.setPlayWhenReady(true);
                break;
            case R.id.call_off_btn:
                moveToCallEndFragment(null);
                break;
        }
    }

    private void initPlayer() {
        try {
            CallVideoManager videoManager = new CallVideoManager();
            player = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());
            if (!player.isLoading()) {
                //Uri sample = RawResourceDataSource.buildRawResourceUri(R.raw.mov_sakura_190603);
                Uri video = videoManager.getMemberVideoUri(this);
                player.addListener(this);
                playerView.setPlayer(player);
                DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                        this,
                        Util.getUserAgent(this, getString(R.string.app_name)));
                ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(video);
                player.prepare(mediaSource);
                userPicture = getBitmapFromView(playerView);
                Log.d(TAG,"picture:"+userPicture.getRowBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private Bitmap getBitmapFromView(PlayerView view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        try {
            bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);
            //TextureView textureView = (TextureView) view.getVideoSurfaceView();
            //Bitmap bitmap = textureView.getBitmap();
        }catch (Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }
    private void callRingStart() {
        if(ringToneStart) {
            switch (audioManager.getRingerMode()) {

                case AudioManager.RINGER_MODE_NORMAL:

                    //현재 자신의 휴대폰 벨소리를 가져온다

                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

                    ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);

                    ringtone.play();

                    break;

                case AudioManager.RINGER_MODE_VIBRATE://진동모드

                    long[] default_vibrate_pattern = {1000, 960, 320, 960, 320, 960, 320, 0};

                    vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                    if (vibrator.hasVibrator()) {

                        vibrator.vibrate(default_vibrate_pattern, 1);

                    }

                    break;

                case AudioManager.RINGER_MODE_SILENT://무음모드

                    //아무것도 재생하지 않는다

                    break;

            }
        }
    }

    private void callRingStop() {
        switch (audioManager.getRingerMode()) {

            case AudioManager.RINGER_MODE_NORMAL:
                ringtone.stop();
                break;

            case AudioManager.RINGER_MODE_VIBRATE://진동모드
                vibrator.cancel();
                break;

            case AudioManager.RINGER_MODE_SILENT://무음모드
                break;

        }
    }

    /*  재생시
        playWhenReady;false, playbackState: 2
        playWhenReady;false, playbackState: 3
        playWhenReady;true, playbackState: 3
        playWhenReady;true, playbackState: 2
        playWhenReady;true, playbackState: 3
        playWhenReady;true, playbackState: 4
        순서로 진행
     */
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        Log.d(TAG, "Ready:" + playWhenReady + ", State:" + playbackState);
        if (!playWhenReady) {//영상 준비
            callRingStart();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    //통화종료 화면으로 이동, 통화 시간은 00:00으로 적용
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            moveToCallEndFragment(null);
                        }
                    });
                }
            };
            timer.schedule(timerTask, CALL_OFF_WAIT_TIME);
        } else if (playWhenReady && playbackState == Player.STATE_ENDED) {//영상 모두 끝났을때
            //영상 재생시간 계산
            long millis = player.getDuration() - START_PLAY_POSITON;
            String playTime = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millis),
                    TimeUnit.MILLISECONDS.toSeconds(millis) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
            );
            Bundle bundle = new Bundle();
            bundle.putString("time", playTime);
            bundle.putString("name", "채원이");
            bundle.putParcelable("picture",userPicture);
            //move call player end fragment
            moveToCallEndFragment(bundle);
            Log.d(TAG, "call end: " + playTime);
        } else if (playWhenReady) {//영상 재생 즁
            callRingStop();
            timer.cancel();
        }
    }

    private void moveToCallEndFragment(Bundle bundle) {
        // Create new fragment and transaction
        Fragment callPlayerEndFragment = new CallPlayerEndFragment();
        callPlayerEndFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.view_container, callPlayerEndFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
        callOffBtn.setVisibility(View.GONE);
        callPlayBtn.setVisibility(View.GONE);
        callRingStop();
        ringToneStart = false;
        showSystemUI();
    }

    private void getFileList() {
        Field[] fields = R.raw.class.getFields();

        for (int i = 0; i < fields.length - 1; i++) {
            String name = fields[i].getName();
            //do your thing here
            Log.d(TAG, "File Name:" + name);

        }
    }

    public interface OnBackPressedListener {
        public void onBack();
    }

    // 리스너 객체 생성
    private OnBackPressedListener mBackListener;

    // 리스너 설정 메소드
    public void setOnBackPressedListener(OnBackPressedListener listener) {
        mBackListener = listener;
    }

    // 뒤로가기 버튼을 눌렀을 때의 오버라이드 메소드
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());

    }

}





































































































































































































































































































