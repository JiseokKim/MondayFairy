package com.test.k.mondayfairy.activity;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.crashlytics.android.Crashlytics;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.test.k.mondayfairy.fragment.CallPlayerEndFragment;
import com.test.k.mondayfairy.CallUser;
import com.test.k.mondayfairy.manager.CallUserManager;
import com.test.k.mondayfairy.R;
import com.test.k.mondayfairy.WakeLockUtil;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import io.fabric.sdk.android.Fabric;

/*월요요정 영상을 재생하는 액티비티*/
public class CallPlayerActivity extends AppCompatActivity implements View.OnClickListener, Player.EventListener {
    private static final String TAG = CallPlayerActivity.class.getSimpleName();
    private int START_PLAY_POSITION = 3800;//millisecond
    private PlayerView playerView;
    private SimpleExoPlayer player;
    private Ringtone ringtone;
    private Vibrator vibrator;
    private ImageButton callPlayBtn;
    private ImageButton callOffBtn;
    private ImageButton cameraBtn;
    private TextView callNameTextView;
    private TextView callTextView;
    private Timer timer;
    private boolean ringToneStart = true;
    private AudioManager audioManager;
    private CallUser callUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, Crashlytics.getInstance());
        WakeLockUtil.acquireCpuWakeLock(this);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_call_player);
        createNotificationChannel();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        timer = new Timer();
        callUserInit();
        uiInit();
        Log.d(TAG, "onCreate()");
    }

    private void uiInit() {
        playerView = findViewById(R.id.player_view);
        //player controller hide
        playerView.setUseController(false);
        callPlayBtn = findViewById(R.id.call_play_btn);
        callOffBtn = findViewById(R.id.call_off_btn);
        cameraBtn = findViewById(R.id.call_camera_btn);
        callPlayBtn.setOnClickListener(this);
        callOffBtn.setOnClickListener(this);
        cameraBtn.setOnClickListener(this);
        callNameTextView = findViewById(R.id.text_call_name_view);
        callTextView = findViewById(R.id.text_call_view);
        //비디오 영상이 전화전용 영상일경우 필요없는 가짜 상태바를 제거한다
        if (callUser.getVideoCode().equals("call")) {
            ConstraintLayout toolbarLayout = findViewById(R.id.toolbar);
            toolbarLayout.setVisibility(View.GONE);
        } else {//비디오 영상이 전화전용 영상이 아닐경우 재생 시작 지점을 0으로 초기화한다
            START_PLAY_POSITION = 0;
            callNameTextView.setText(callUser.getCallName());
            callNameTextView.setVisibility(View.VISIBLE);
            callTextView.setVisibility(View.VISIBLE);
            //영상통화 화면처럼 버튼에 이미지를 씌움
            callPlayBtn.setBackgroundResource(R.drawable.player_start);
            callOffBtn.setBackgroundResource(R.drawable.player_stop);

        }
    }

    private void callUserInit() {
        Intent intent = getIntent();
        if (intent != null && intent.getIntExtra("requestCode", 0) != 101) {
            Log.d(TAG, "onCreate() requestCode: " + intent.getIntExtra("requestCode", 0));
            CallUserManager userManager = new CallUserManager(this);
            userManager.userInit();
            callUser = CallUser.getInstance();
        } else {
            if (intent.getParcelableExtra("callUser") != null) {
                callUser = intent.getParcelableExtra("callUser");
            }
        }
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
        super.onStop();

    }


    @Override

    protected void onDestroy() {
        super.onDestroy();
        //배터리 관리를 위해 사용이 끝나면 wakeLock 해제
        WakeLockUtil.releaseCpuWakeLock();
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

    private Bundle createBundleData() {
        long millis = player.getCurrentPosition();
        String playTime = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );
        BitmapDrawable drawables = (BitmapDrawable) getResources().getDrawable(callUser.getThumbnailId());
        Bitmap userPicture = drawables.getBitmap();
        //영상이름과 관련된 월요요정 이름과 썸네일을 넘기는 기능 구현 필요
        Bundle data = new Bundle();
        data.putString("time", playTime);
        data.putString("name", callUser.getCallName());
        data.putParcelable("picture", userPicture);
        return data;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.call_play_btn:
                callNameTextView.setVisibility(View.GONE);
                callTextView.setVisibility(View.GONE);
                player.seekTo(START_PLAY_POSITION);
                player.setPlayWhenReady(true);
                callPlayBtn.setVisibility(View.GONE);
                ConstraintLayout constraintLayout = findViewById(R.id.view_container);
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(constraintLayout);
                //기존 컴포넌트들끼리 연결 끊기
                constraintSet.clear(callOffBtn.getId(), ConstraintSet.START);
                constraintSet.clear(callOffBtn.getId(), ConstraintSet.BOTTOM);

                constraintSet.clear(cameraBtn.getId(), ConstraintSet.END);
                constraintSet.clear(cameraBtn.getId(), ConstraintSet.BOTTOM);

                if(callUser.getVideoCode().equals("mov")){
                    constraintSet.connect(callOffBtn.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
                    constraintSet.connect(callOffBtn.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
                    constraintSet.connect(callOffBtn.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
                    constraintSet.connect(callOffBtn.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
                    //값이 0에 가까울수록 Top 방향에 가깝다
                    constraintSet.setVerticalBias(callOffBtn.getId(), 0.92f);
                }else {
                    constraintSet.connect(callOffBtn.getId(), ConstraintSet.END, R.id.guideline_right, ConstraintSet.END, 0);
                    constraintSet.connect(callOffBtn.getId(), ConstraintSet.BOTTOM, R.id.guideline_bottom, ConstraintSet.TOP, 0);
                }
                constraintSet.connect(cameraBtn.getId(), ConstraintSet.END, callOffBtn.getId(), ConstraintSet.START, 16);
                constraintSet.connect(cameraBtn.getId(), ConstraintSet.BOTTOM, R.id.guideline_bottom, ConstraintSet.TOP, 0);
                constraintSet.applyTo(constraintLayout);
                break;
            case R.id.call_off_btn:
                if (player.isPlaying()) {
                    player.stop();
                }
                timer.cancel();//사용자가 직접 통화 종료시 알람을 띄우지 않는다
                moveToCallEndFragment(createBundleData());
                break;
            case R.id.call_camera_btn:
                break;

        }
    }

    private void initPlayer() {
        //String videoPath = RawResourceDataSource.buildRawResourceUri(R.raw.mov_sakura_190603).getPath();
        //userPicture = getCreatedThumbnail(videoPath);
        try {
            player = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());
            if (!player.isLoading()) {
                //Uri sample = RawResourceDataSource.buildRawResourceUri(R.raw.mov_sakura_190603);
                Uri video = callUser.getVideoUri();
                player.addListener(this);
                playerView.setPlayer(player);
                DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                        this,
                        Util.getUserAgent(this, getString(R.string.app_name)));
                ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(video);
                player.prepare(mediaSource);
                callRingStart();
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        //시간내에 통화전화를 받지 않으면 부재중 알림을 띄운다
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                missedCallNotification(callUser.getCallName());
                                onBackPressed();
                            }
                        });
                    }
                };
                final int CALL_OFF_WAIT_TIME = 12000;//millisecond
                timer.schedule(timerTask, CALL_OFF_WAIT_TIME);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callRingStart() {
        if (ringToneStart) {
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
        if (playbackState == Player.STATE_ENDED) {//영상 모두 끝났을때
            //영상 재생시간 계산
            long millis = player.getDuration() - START_PLAY_POSITION;
            String playTime = String.format(Locale.US, "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millis),
                    TimeUnit.MILLISECONDS.toSeconds(millis) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
            );
            BitmapDrawable drawables = (BitmapDrawable) getResources().getDrawable(callUser.getThumbnailId());
            Bitmap userPicture = drawables.getBitmap();
            //영상이름과 관련된 월요요정 이름과 썸네일을 넘기는 기능 구현 필요
            Bundle bundle = new Bundle();
            bundle.putString("time", playTime);
            bundle.putString("name", callUser.getCallName());
            bundle.putParcelable("picture", userPicture);
            //move call player end fragment
            moveToCallEndFragment(bundle);
            Log.d(TAG, "call end: " + playTime);
        } else if (playWhenReady) {//영상 재생 즁
            playerView.setKeepScreenOn(true);//잠금화면에서 화면이 꺼지지 않게 설정
            callRingStop();
            timer.cancel();
//            Log.d(TAG, "ringtone is playing:"+ringtone.isPlaying());
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
        playerView.setKeepScreenOn(false);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        callRingStop();
        ringToneStart = false;
        showSystemUI();
    }

    final String CHANNEL_ID = "1212";

    private void missedCallNotification(String name) {
        // Create an explicit intent for an Activity in your app
        Intent notifyIntent = new Intent(this, CallPlayerActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int requestCode = 101;
        notifyIntent.putExtra("requestCode", requestCode);
        notifyIntent.putExtra("callUser", callUser);
        //PendingIntent flag를 0으로 지정하면 액티비티에서 인텐트를 받지 못하는 현상이 있음. 이것땜에 시간 너무 날렸다
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_call_missed_24dp)
                .setContentTitle("부재중 알림")
                .setContentText("월요요정 " + name + "왔어요!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        int notificationId = 0;
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, builder.build());


    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
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





































































































































































































































































































