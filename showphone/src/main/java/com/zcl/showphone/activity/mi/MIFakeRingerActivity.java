package com.zcl.showphone.activity.mi;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.CallLog;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.xdandroid.hellodaemon.DaemonEnv;
import com.zcl.showphone.IFace.ICallEventListener;
import com.zcl.showphone.R;
import com.zcl.showphone.activity.CallBaseActivity;
import com.zcl.showphone.service.TraceServiceImpl;
import com.zcl.showphone.utils.CallLogUtilities;
import com.zcl.showphone.view.MIUPImageView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

public class MIFakeRingerActivity extends CallBaseActivity implements ICallEventListener {


    private static final int INCOMING_CALL_NOTIFICATION = 1001;
    private static final int MISSED_CALL_NOTIFICATION = 1002;


    @BindView(R.id.iv_call_pic)
    ImageView iv_call_pic;

    @BindView(R.id.tv_number)
    TextView tv_number;

    @BindView(R.id.callDuration)
    TextView callDuration;

    @BindView(R.id.main)
    ViewGroup main;

    @BindView(R.id.iv_animation)
    ImageView iv_animation;

    @BindView(R.id.iv_hangup)
    MIUPImageView iv_hangup;

    @BindView(R.id.iv_answer)
    MIUPImageView iv_answer;

    @BindView(R.id.fl_called_bg)
    ViewGroup fl_called_bg;

    @BindView(R.id.tv_name)
    TextView tv_name;


    private String name;
    private String voice;
    private int duration;
    private String number;
    private String contactImageString;
    private int hangUpAfter;
    private String ringStringUri;


    private Ringtone ringtone;
    private Vibrator vibrator;
    private PowerManager.WakeLock wakeLock;
    private NotificationManager notificationManager;
    private Resources resources;
    private AudioManager audioManager;
    private ContentResolver contentResolver;

    private int currentRingerMode;
    private int currentRingerVolume;
    private int currentMediaVolume;

    private long secs;


    private Runnable hangUP = new Runnable() {
        @Override
        public void run() {
            onNextCall();
            finish();
        }
    };


    @SuppressLint("ResourceType")
    @Override
    protected int getLayout() {
        fullScreen(this);
        return R.layout.activity_fake_ringer_mi;
    }

    private void getBaseConfig() {
        contentResolver = getContentResolver();
        resources = getResources();
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        currentRingerMode = audioManager.getRingerMode();
        currentRingerVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
        currentMediaVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);


        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "Tag");
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        wakeLock.setReferenceCounted(false);

    }

    private void getIntentData() {

        Bundle extras = getIntent().getExtras();
        name = extras.getString("name");
        voice = extras.getString("voice", "");
        duration = extras.getInt("duration");
        number = extras.getString("number");
        contactImageString = extras.getString("contactImage");
        hangUpAfter = extras.getInt("hangUpAfter");
        ringStringUri = extras.getString("ringUri");

    }


    @Override
    protected void init() {

        getBaseConfig();
        getIntentData();

        setContactImage();


        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this);
        nBuilder.setSmallIcon(R.mipmap.ic_call);
        nBuilder.setOngoing(true);
        nBuilder.setContentTitle(name);
        nBuilder.setColor(Color.rgb(4, 137, 209));
        nBuilder.setContentText(resources.getString(R.string.incoming_call));
        notificationManager.notify(INCOMING_CALL_NOTIFICATION, nBuilder.build());

        mHandler.postDelayed(hangUP, hangUpAfter * 1000);

        muteAll();

        Uri ringtoneURI;
        if (null == ringStringUri) {
            ringtoneURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        } else {
            ringtoneURI = Uri.parse(ringStringUri);
        }
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), ringtoneURI);
        ringtone.play();

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {1000, 1000, 1000, 1000, 1000};
        vibrator.vibrate(pattern, 0);


    }


    private void setContactImage() {

        if (!(contactImageString == null))
            Glide.with(this).load(contactImageString).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    iv_call_pic.setImageBitmap(resource);
                }


            });
        tv_number.setText(number);
        tv_name.setText(name);

        Animation animCallStatusPulse = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.call_tip_up);
        iv_animation.startAnimation(animCallStatusPulse);

        fl_called_bg.setVisibility(View.GONE);
    }


    private void muteAll() {

        audioManager.setStreamMute(AudioManager.STREAM_ALARM, true);

        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);

    }

    private void unMuteAll() {

        audioManager.setStreamMute(AudioManager.STREAM_ALARM, false);

        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);

    }

    public void onClickEndCall(View view) {

        onNextCall();
        stopVoice();
        finish();

    }

    private void stopVoice() {

        if (voicePlayer != null && voicePlayer.isPlaying()) {
            voicePlayer.stop();
        }

    }

    private void stopRinging() {

        vibrator.cancel();
        ringtone.stop();

    }

    // adds a missed call to the log and shows a notification
    private void missedCall() {

        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this);

        nBuilder.setSmallIcon(android.R.drawable.stat_notify_missed_call);

        nBuilder.setContentTitle(name);

        nBuilder.setContentText(resources.getString(R.string.missed_call));

        nBuilder.setColor(Color.rgb(4, 137, 209));

        nBuilder.setAutoCancel(true);

        Intent showCallLog = new Intent(Intent.ACTION_VIEW);

        showCallLog.setType(CallLog.Calls.CONTENT_TYPE);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, showCallLog, PendingIntent.FLAG_CANCEL_CURRENT);

        nBuilder.setContentIntent(pendingIntent);

        showCallLog.setType(CallLog.Calls.CONTENT_TYPE);

        notificationManager.notify(MISSED_CALL_NOTIFICATION, nBuilder.build());

        CallLogUtilities.addCallToLog(contentResolver, number, 0, CallLog.Calls.MISSED_TYPE, System.currentTimeMillis());

    }

    private void incomingCall() {

        CallLogUtilities.addCallToLog(contentResolver, number, secs, CallLog.Calls.INCOMING_TYPE, System.currentTimeMillis());

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopVoice();
        notificationManager.cancel(INCOMING_CALL_NOTIFICATION);

        if (secs > 0) {
            incomingCall();
        } else {
            missedCall();
        }

        wakeLock.release();

        audioManager.setRingerMode(currentRingerMode);
        audioManager.setStreamVolume(AudioManager.STREAM_RING, currentRingerVolume, 0);

        stopRinging();
        unMuteAll();

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentMediaVolume, 0);

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void finish() {
        super.finish();
        mHandler.removeCallbacks(hangUP);
        callFinishAction();

    }

    private void onNextCall() {
        String mode = getIntent().getExtras().getString("mode");
        int modeTimes = getIntent().getExtras().getInt("modeTimes");

        if (!mode.equals(getString(R.string.call_mode_type)) && modeTimes < 3) {
            Intent intent = new Intent(this, TraceServiceImpl.class);

            intent.putExtra("contactImage", contactImageString);
            intent.putExtra("number", number);
            intent.putExtra("name", name);

            intent.putExtra("voice", voice);
            intent.putExtra("ringUri", ringStringUri);
            intent.putExtra("mode", mode);
            modeTimes++;
            intent.putExtra("modeTimes", modeTimes);

            intent.putExtra("time", Integer.parseInt(mode));

            intent.putExtra("duration", duration);
            intent.putExtra("hangUpAfter", hangUpAfter);

            TraceServiceImpl.sShouldStopService = false;
            DaemonEnv.startServiceMayBindByIntentData(intent, TraceServiceImpl.class);

        }
    }


    @OnClick({R.id.btn_hangup})
    void onClick(View view) {
        mHandler.post(hangUP);
    }

    @Override
    public void actionAp(View view) {
        if (view == iv_answer) {

            main.setBackgroundResource(R.mipmap.mi_ring_calling_bg);
            fl_called_bg.setVisibility(View.VISIBLE);
            iv_animation.clearAnimation();

            mHandler.removeCallbacks(hangUP);
            stopRinging();
            wakeLock.acquire();
            playVoice(voice);

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    long min = (secs % 3600) / 60;
                    long seconds = secs % 60;
                    String dur = String.format(Locale.US, "%02d:%02d", min, seconds);
                    secs++;
                    callDuration.setText(dur);
                    mHandler.postDelayed(this, 1000);
                }
            }, 10);

            mHandler.postDelayed(hangUP, duration * 1000);

        } else if (view == iv_hangup) {
            mHandler.post(hangUP);

        }

    }

    @Override
    public void actionDown(View view) {

    }
}
