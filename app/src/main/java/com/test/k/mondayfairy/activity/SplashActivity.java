package com.test.k.mondayfairy.activity;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.content.Intent;

import com.test.k.mondayfairy.R;

public class SplashActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaPlayer = MediaPlayer.create(this, R.raw.bgm_fiesta_teaser);
        mediaPlayer.setVolume(0.5f, 0.5f);
        mediaPlayer.start();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mediaPlayer.release();
    }
}