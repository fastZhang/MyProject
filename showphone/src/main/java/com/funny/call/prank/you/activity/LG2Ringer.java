package com.funny.call.prank.you.activity;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.funny.call.prank.you.IFace.ICallEventListener;
import com.funny.call.prank.you.R;
import com.funny.call.prank.you.service.TraceServiceImpl;
import com.funny.call.prank.you.utils.CallLogUtilities;
import com.funny.call.prank.you.view.MIUPImageView;
import com.xdandroid.hellodaemon.DaemonEnv;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

import static android.content.Context.AUDIO_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;
import static android.content.Context.POWER_SERVICE;

public class LG2Ringer extends RingDialogView {


    private static final int INCOMING_CALL_NOTIFICATION = 1001;
    private static final int MISSED_CALL_NOTIFICATION = 1002;


    @BindView(R.id.iv_call_pic)
    ImageView iv_call_pic;

    @BindView(R.id.tv_number)
    TextView tv_number;

    @BindView(R.id.callDuration)
    TextView callDuration;
    @BindView(R.id.call_state)
    TextView call_state;

    @BindView(R.id.main)
    ViewGroup main;

    @BindView(R.id.iv_hangup)
    ImageView iv_hangup;

    @BindView(R.id.iv_answer)
    ImageView iv_answer;

    @BindView(R.id.fl_called_bg)
    ViewGroup fl_called_bg;
    @BindView(R.id.fl_calling_bg)
    ViewGroup fl_calling_bg;

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


    private long secs;


    private Runnable hangUP = new Runnable() {
        @Override
        public void run() {
            onNextCall();
            dismiss();
        }
    };

    public LG2Ringer(@NonNull Context context) {
        super(context);
    }


    @SuppressLint("InvalidWakeLockTag")
    private void getBaseConfig(Context context) {
        contentResolver = context.getContentResolver();
        resources = context.getResources();
        audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);


        PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "Tag");
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        wakeLock.setReferenceCounted(false);

    }

    private void getIntentData() {

        Bundle extras = mIntent.getExtras();
        name = extras.getString("name");
        voice = extras.getString("voice", "");
        duration = extras.getInt("duration");
        number = extras.getString("number");
        contactImageString = extras.getString("contactImage");
        hangUpAfter = extras.getInt("hangUpAfter");
        ringStringUri = extras.getString("ringUri");

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_fake_ringer_lg2;
    }

    @Override
    protected void init() {

        getBaseConfig(mContext);
        getIntentData();

        setContactImage();


        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(mContext);
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
        ringtone = RingtoneManager.getRingtone(mContext.getApplicationContext(), ringtoneURI);
        ringtone.play();

        vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {1000, 1000, 1000, 1000, 1000};
        vibrator.vibrate(pattern, 0);


    }


    private void setContactImage() {

        if (!(contactImageString == null))
            Glide.with(mContext).load(contactImageString).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    iv_call_pic.setImageBitmap(resource);
                }


            });
        tv_number.setText(number);
        tv_name.setText(name);

        fl_called_bg.setVisibility(View.GONE);
        fl_calling_bg.setVisibility(View.VISIBLE);

        callDuration.setVisibility(View.GONE);
        call_state.setVisibility(View.VISIBLE);
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
        dismiss();

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

        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(mContext);

        nBuilder.setSmallIcon(android.R.drawable.stat_notify_missed_call);

        nBuilder.setContentTitle(name);

        nBuilder.setContentText(resources.getString(R.string.missed_call));

        nBuilder.setColor(Color.rgb(4, 137, 209));

        nBuilder.setAutoCancel(true);

        Intent showCallLog = new Intent(Intent.ACTION_VIEW);

        showCallLog.setType(CallLog.Calls.CONTENT_TYPE);

        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, showCallLog, PendingIntent.FLAG_CANCEL_CURRENT);

        nBuilder.setContentIntent(pendingIntent);

        showCallLog.setType(CallLog.Calls.CONTENT_TYPE);

        notificationManager.notify(MISSED_CALL_NOTIFICATION, nBuilder.build());

        CallLogUtilities.addCallToLog(contentResolver, number, 0, CallLog.Calls.MISSED_TYPE, System.currentTimeMillis());

    }

    private void incomingCall() {

        CallLogUtilities.addCallToLog(contentResolver, number, secs, CallLog.Calls.INCOMING_TYPE, System.currentTimeMillis());

    }


    @Override
    public void dismiss() {
        super.dismiss();

        mHandler.removeCallbacks(hangUP);
        callFinishAction();


        stopVoice();
        notificationManager.cancel(INCOMING_CALL_NOTIFICATION);

        if (secs > 0) {
            incomingCall();
        } else {
            missedCall();
        }

        wakeLock.release();


        stopRinging();
        unMuteAll();


    }

    @Override
    public void onBackPressed() {

    }


    private void onNextCall() {
        String mode = mIntent.getExtras().getString("mode");
        int modeTimes = mIntent.getExtras().getInt("modeTimes");

        if (!mode.equals(mContext.getString(R.string.call_mode_type)) && modeTimes < 3) {
            Intent intent = new Intent(mContext, TraceServiceImpl.class);

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


    @OnClick({R.id.btn_hangup, R.id.iv_answer, R.id.iv_hangup})
    void onClick(View view) {

        actionAp(view);
    }

    public void actionAp(View view) {
        if (view == iv_answer) {

            main.setBackgroundResource(R.mipmap.mi_ring_calling_bg);
            fl_called_bg.setVisibility(View.VISIBLE);
            fl_calling_bg.setVisibility(View.GONE);

            callDuration.setVisibility(View.VISIBLE);
            call_state.setVisibility(View.GONE);

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

        } else {
            mHandler.post(hangUP);

        }

    }


}
