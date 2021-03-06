package com.funny.call.prank.you.activity;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.funny.call.prank.you.R;
import com.funny.call.prank.you.service.TraceServiceImpl;
import com.funny.call.prank.you.utils.CallLogUtilities;
import com.funny.call.prank.you.utils.PicPathUtils;
import com.funny.call.prank.you.utils.ScreenInfoUtil;
import com.xdandroid.hellodaemon.DaemonEnv;

import java.util.Locale;

import butterknife.BindView;

import static android.content.Context.AUDIO_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;
import static android.content.Context.POWER_SERVICE;

public class BlurRinger extends RingDialogView {


    private static final int INCOMING_CALL_NOTIFICATION = 1001;
    private static final int MISSED_CALL_NOTIFICATION = 1002;

    private ImageButton callActionButton;
    private ImageButton answer;
    private ImageButton decline;
    private ImageButton text;
    private ImageButton endCall;

    private ImageView contactPhoto;

    private ImageView ring;

    private TextView callStatus;
    private TextView callDuration;


    private RelativeLayout callActionButtons;

    private AudioManager audioManager;

    private long secs;

    private int duration;
    private int hangUpAfter;

    private String number;

    private int modeTimes;
    private String mode;
    private String name;
    private String ringStringUri;

    private String voice;

    private Ringtone ringtone;

    private Vibrator vibrator;

    private PowerManager.WakeLock wakeLock;

    private NotificationManager notificationManager;

    private ContentResolver contentResolver;


    private Resources resources;


    private String contactImageString;


    final Handler handler = new Handler();

    private Runnable hangUP = new Runnable() {
        @Override
        public void run() {
            dismiss();
            onNextCall();


        }
    };

    @BindView(R.id.callInfoLayout)
    RelativeLayout mRelativeLayout;


    public BlurRinger(@NonNull Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_fake_ringer_blur;
    }


    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void init() {

        mRelativeLayout.getLayoutParams().height = (int) (ScreenInfoUtil.screenHeight(mContext) * 0.31f);

        TextView phoneNumber = (TextView) findViewById(R.id.phoneNumber);
        TextView callerName = (TextView) findViewById(R.id.callerName);

        final Animation ringExpandAnimation = AnimationUtils.loadAnimation(mContext, R.anim.ring_expand);
        final Animation ringShrinkAnimation = AnimationUtils.loadAnimation(mContext, R.anim.ring_shrink);


        contactPhoto = (ImageView) findViewById(R.id.contactPhoto);


        contentResolver = mContext.getContentResolver();

        resources = mContext.getResources();

        audioManager = (AudioManager) mContext.getSystemService(AUDIO_SERVICE);

        notificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);


        callActionButtons = (RelativeLayout) findViewById(R.id.callActionButtons);

        callActionButton = (ImageButton) findViewById(R.id.callActionButton);

        answer = (ImageButton) findViewById(R.id.callActionAnswer);

        decline = (ImageButton) findViewById(R.id.callActionDecline);

        text = (ImageButton) findViewById(R.id.callActionText);

        endCall = (ImageButton) findViewById(R.id.endCall);
        endCall.setOnClickListener(v -> {
            onClickEndCall(v);
        });

        callStatus = (TextView) findViewById(R.id.callStatus);

        callDuration = (TextView) findViewById(R.id.callDuration);


        ring = (ImageView) findViewById(R.id.ring);

        Bundle extras = mIntent.getExtras();
        mode = extras.getString("mode");
        modeTimes = extras.getInt("modeTimes");
        name = extras.getString("name");

        voice = extras.getString("voice", "");

        duration = extras.getInt("duration");

        number = extras.getString("number");

        contactImageString = extras.getString("contactImage");

        hangUpAfter = extras.getInt("hangUpAfter");
        ringStringUri = extras.getString("ringUri");


        PowerManager powerManager = (PowerManager) mContext.getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "Tag");
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        wakeLock.setReferenceCounted(false);

        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(mContext);
        nBuilder.setSmallIcon(R.mipmap.ic_call);
        nBuilder.setOngoing(true);
        nBuilder.setContentTitle(name);
        nBuilder.setColor(Color.rgb(4, 137, 209));
        nBuilder.setContentText(resources.getString(R.string.incoming_call));
        notificationManager.notify(INCOMING_CALL_NOTIFICATION, nBuilder.build());

        handler.postDelayed(hangUP, hangUpAfter * 1000);

        muteAll();

        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) contactPhoto.getLayoutParams();
        int w = ScreenInfoUtil.screenWidth(mContext) - ScreenInfoUtil.dip2px(mContext, 60);
        lp.width = w;
        lp.height = w;
        contactPhoto.setLayoutParams(lp);

        setContactImage(true);

        callActionButton.setOnTouchListener(new View.OnTouchListener() {

            float x1 = 0, x2 = 0, y1 = 0, y2 = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int a = event.getAction();

                if (a == MotionEvent.ACTION_DOWN) {

                    x1 = event.getX();

                    y1 = event.getY();

                    ring.startAnimation(ringExpandAnimation);

                    answer.setVisibility(View.VISIBLE);

                    decline.setVisibility(View.VISIBLE);

                    text.setVisibility(View.GONE);

                    callActionButton.setVisibility(View.INVISIBLE);

                } else if (a == MotionEvent.ACTION_MOVE) {

                    x2 = event.getX();

                    y2 = event.getY();

                    if ((x2 - 200) > x1) {
                        //接通

                        callActionButtons.removeView(callActionButton);

                        callActionButtons.removeView(ring);

                        callActionButtons.removeView(answer);

                        callActionButtons.removeView(decline);

                        callActionButtons.removeView(text);

//                        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

                        handler.removeCallbacks(hangUP);

                        callStatus.setText("");

                        setContactImage(false);

                        stopRinging();


                        endCall.setVisibility(View.VISIBLE);

                        wakeLock.acquire();

                        playVoice(voice);

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                long min = (secs % 3600) / 60;

                                long seconds = secs % 60;

                                String dur = String.format(Locale.US, "%02d:%02d", min, seconds);

                                secs++;

                                callDuration.setText(dur);

                                handler.postDelayed(this, 1000);

                            }
                        }, 10);

                        handler.postDelayed(new Runnable() {

                            @Override
                            public void run() {

                                onNextCall();
                                dismiss();
                            }

                        }, duration * 1000);


                    } else if ((x2 + 200) < x1) {

                        dismiss();
                        onNextCall();

                    } else if ((y2 + 200) < y1) {

                        dismiss();
                        onNextCall();


                    } else if ((y2 - 200) > y1) {

                        dismiss();
                        onNextCall();


                    }

                } else if (a == MotionEvent.ACTION_UP || a == MotionEvent.ACTION_CANCEL) {

                    answer.setVisibility(View.INVISIBLE);

                    decline.setVisibility(View.INVISIBLE);

                    text.setVisibility(View.INVISIBLE);

                    ring.startAnimation(ringShrinkAnimation);

                    callActionButton.setVisibility(View.VISIBLE);

                }

                return false;

            }
        });

        Animation animCallStatusPulse = AnimationUtils.loadAnimation(mContext.getApplicationContext(), R.anim.call_status_pulse);

        callStatus.startAnimation(animCallStatusPulse);

        phoneNumber.setText("Mobile " + number);

        callerName.setText(name);

        Uri ringtoneURI;

        if (null == ringStringUri) {
            ringtoneURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        } else {
            ringtoneURI = Uri.parse(ringStringUri);
        }

        ringtone = RingtoneManager.getRingtone(mContext.getApplicationContext(), ringtoneURI);
        if (ringtone != null)
            ringtone.play();

        vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);


        long[] pattern = {1000, 1000, 1000, 1000, 1000};

        vibrator.vibrate(pattern, 0);

    }


    private void setContactImage(boolean tint) {

        if (!(contactImageString == null)) {


            Glide.with(mContext).load(PicPathUtils.wrapHeaderPicPath(mContext, contactImageString)).into(new SimpleTarget<GlideDrawable>() {
                @Override
                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {

                    if (tint) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            resource.setTint(mContext.getResources().getColor(R.color.contact_photo_tint));
                            resource.setTintMode(PorterDuff.Mode.DARKEN);
                        }
                    }


                    contactPhoto.setImageDrawable(resource);
                }
            });

        }
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

        handler.removeCallbacks(hangUP);

        callFinishAction();
        //======

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

        System.out.println("modeTimes" + modeTimes);

        Log.d("modeTimes", "finish: " + modeTimes);

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

}
