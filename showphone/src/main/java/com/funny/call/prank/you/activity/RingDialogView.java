package com.funny.call.prank.you.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;


import com.funny.call.prank.you.ad.HandLoadInterstitialAd;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public abstract class RingDialogView extends Dialog {
    protected Handler mHandler = new Handler();


    public Context mContext;
    public Intent mIntent;


    public RingDialogView(@NonNull final Context context) {
        super(context);
        mContext = context;


        ((ViewGroup) getWindow().getDecorView()).addView(View.inflate(mContext, getLayoutId(), null));

        //设置window背景，默认的背景会有Padding值，不能全屏。当然不一定要是透明，你可以设置其他背景，替换默认的背景即可。
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));

        //一定要在setContentView之后调用，否则无效
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)

            getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        else
            getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);

        fullScreen(getWindow());

        ButterKnife.bind(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    protected MediaPlayer voicePlayer;


    @Override
    public synchronized void show() {
        if (!isShowing())
            super.show();
    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }


    public abstract int getLayoutId();

    protected abstract void init();


    public RingDialogView setIntent(Intent intent) {
        mIntent = intent;

        return this;
    }


    protected void callFinishAction() {
//        HandLoadInterstitialAd.getInstance(this).showInterstitial(HandLoadInterstitialAd.getInstance(this).getInterstitialAd());
        HandLoadInterstitialAd.getInstance(mContext).showInterstitial(HandLoadInterstitialAd.getInstance(mContext).getSplashAd());
    }

    protected void fullScreen(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
                View decorView = window.getDecorView();
                //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间

                int option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE

                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION//布局fitwindow
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION//窗口

                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;


                decorView.setSystemUiVisibility(option);

                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
                window.setNavigationBarColor(Color.TRANSPARENT);
            } else {
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
                    AssetFileDescriptor fd = mContext.getAssets().openFd(strings[strings.length - 2] + "/" + strings[strings.length - 1]);
                    voicePlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());

                } else
                    voicePlayer.setDataSource(mContext, voiceURI);
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
