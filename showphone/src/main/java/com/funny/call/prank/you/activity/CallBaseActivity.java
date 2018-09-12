package com.funny.call.prank.you.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.funny.call.prank.you.BaseActivity;
import com.funny.call.prank.you.R;
import com.funny.call.prank.you.ad.HandLoadInterstitialAd;

public abstract class CallBaseActivity extends BaseActivity {

    protected MediaPlayer voicePlayer;


    protected void callFinishAction() {
//        HandLoadInterstitialAd.getInstance(this).showInterstitial(HandLoadInterstitialAd.getInstance(this).getInterstitialAd());
        HandLoadInterstitialAd.getInstance(this).showInterstitial(HandLoadInterstitialAd.getInstance(this).getSplashAd());
    }

    protected void fullScreen(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
                Window window = activity.getWindow();
                View decorView = window.getDecorView();
                //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间

                int option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE

                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION//布局fitwindow
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION//窗口

//                        | View.SYSTEM_UI_FLAG_FULLSCREEN
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;


                decorView.setSystemUiVisibility(option);

                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
                window.setNavigationBarColor(Color.TRANSPARENT);
            } else {
                Window window = activity.getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
                attributes.flags |= flagTranslucentStatus;
                attributes.flags |= flagTranslucentNavigation;
                window.setAttributes(attributes);
            }
        }
    }


    public void playVoice(String voice) {

        if (!voice.equals("")) {


            Uri voiceURI = Uri.parse(voice);

            voicePlayer = new MediaPlayer();

            try {

                if (voice.contains("android_asset")) {

                    String[] strings = voice.split("/");
                    AssetFileDescriptor fd = getAssets().openFd(strings[strings.length - 2] + "/" + strings[strings.length - 1]);
                    voicePlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());

                } else
                    voicePlayer.setDataSource(this, voiceURI);
            } catch (Exception e) {
                e.printStackTrace();
            }

            voicePlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);

            voicePlayer.prepareAsync();

            voicePlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });

            // 监听音频播放完的代码，实现音频的自动循环播放
            voicePlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer arg0) {
                    voicePlayer.start();
                    voicePlayer.setLooping(true);
                }
            });

        }

    }


}
