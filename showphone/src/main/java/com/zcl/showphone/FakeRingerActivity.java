package com.zcl.showphone;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
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
import android.telephony.PhoneNumberUtils;
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

import com.zcl.showphone.utils.CallLogUtilities;
import com.zcl.showphone.utils.ScreenInfoUtil;

import java.io.InputStream;
import java.util.Locale;

import butterknife.BindView;

import static com.zcl.showphone.utils.Constant.ALARM_ID;

public class FakeRingerActivity extends BaseActivity {

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

    private ViewGroup main;

    private RelativeLayout callActionButtons;

    private AudioManager audioManager;

    private long secs;

    private int duration;
    private int hangUpAfter;

    private String number;

    private String name;
    private String ringStringUri;

    private String voice;

    private Ringtone ringtone;

    private Vibrator vibrator;

    private PowerManager.WakeLock wakeLock;

    private NotificationManager notificationManager;

    private ContentResolver contentResolver;

    private MediaPlayer voicePlayer;

    private Resources resources;

    private int currentRingerMode;

    private int currentRingerVolume;

    private String contactImageString;

    private int currentMediaVolume;

    final Handler handler = new Handler();

    private Runnable hangUP = new Runnable() {
        @Override
        public void run() {
            finish();
        }
    };

    @BindView(R.id.callInfoLayout)
    RelativeLayout mRelativeLayout;

    @Override
    protected int getLayout() {
        return R.layout.activity_fake_ringer;
    }

    @Override
    protected void init() {

        mRelativeLayout.getLayoutParams().height = (int) (ScreenInfoUtil.screenHeight(this) * 0.31f);

        TextView phoneNumber = (TextView) findViewById(R.id.phoneNumber);
        TextView callerName = (TextView) findViewById(R.id.callerName);

        final Animation ringExpandAnimation = AnimationUtils.loadAnimation(this, R.anim.ring_expand);
        final Animation ringShrinkAnimation = AnimationUtils.loadAnimation(this, R.anim.ring_shrink);


        contactPhoto = (ImageView) findViewById(R.id.contactPhoto);

        contentResolver = getContentResolver();

        resources = getResources();

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        currentRingerMode = audioManager.getRingerMode();

        currentRingerVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);

        currentMediaVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        callActionButtons = (RelativeLayout) findViewById(R.id.callActionButtons);

        callActionButton = (ImageButton) findViewById(R.id.callActionButton);

        answer = (ImageButton) findViewById(R.id.callActionAnswer);

        decline = (ImageButton) findViewById(R.id.callActionDecline);

        text = (ImageButton) findViewById(R.id.callActionText);

        endCall = (ImageButton) findViewById(R.id.endCall);

        callStatus = (TextView) findViewById(R.id.callStatus);

        callDuration = (TextView) findViewById(R.id.callDuration);

        main =  findViewById(R.id.main);

        ring = (ImageView) findViewById(R.id.ring);

        Bundle extras = getIntent().getExtras();

        name = extras.getString("name");

        voice = extras.getString("voice", "");

        duration = extras.getInt("duration");

        number = extras.getString("number");

        contactImageString = extras.getString("contactImage");

        hangUpAfter = extras.getInt("hangUpAfter");


        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "Tag");
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        wakeLock.setReferenceCounted(false);

        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this);
        nBuilder.setSmallIcon(R.mipmap.ic_call);
        nBuilder.setOngoing(true);
        nBuilder.setContentTitle(name);
        nBuilder.setColor(Color.rgb(4, 137, 209));
        nBuilder.setContentText(resources.getString(R.string.incoming_call));
        notificationManager.notify(INCOMING_CALL_NOTIFICATION, nBuilder.build());

        handler.postDelayed(hangUP, hangUpAfter * 1000);

        muteAll();

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

                    text.setVisibility(View.VISIBLE);

                    callActionButton.setVisibility(View.INVISIBLE);

                } else if (a == MotionEvent.ACTION_MOVE) {

                    x2 = event.getX();

                    y2 = event.getY();

                    if ((x2 - 200) > x1) {

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

                        main.setBackgroundResource(R.mipmap.answered_bg);

                        endCall.setVisibility(View.VISIBLE);

                        wakeLock.acquire();

                        playVoice();

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
                                finish();
                            }

                        }, duration * 1000);


                    } else if ((x2 + 200) < x1) {

                        finish();

                    } else if ((y2 + 200) < y1) {

                        finish();

                    } else if ((y2 - 200) > y1) {

                        finish();

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

        Animation animCallStatusPulse = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.call_status_pulse);

        callStatus.startAnimation(animCallStatusPulse);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            number = PhoneNumberUtils.formatNumber(number, null);
//        }

        phoneNumber.setText("Mobile " + number);

        callerName.setText(name);

        Uri ringtoneURI;

        ringStringUri = extras.getString("ringUri");
        if (null == ringStringUri) {
            ringtoneURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        } else {
            ringtoneURI = Uri.parse(ringStringUri);
        }

        ringtone = RingtoneManager.getRingtone(getApplicationContext(), ringtoneURI);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        ringtone.play();

        long[] pattern = {1000, 1000, 1000, 1000, 1000};

        vibrator.vibrate(pattern, 0);

    }


    private void setContactImage(boolean tint) {

        if (!(contactImageString == null)) {

            Uri contactImageUri = Uri.parse(contactImageString);

            try {

                InputStream contactImageStream = contentResolver.openInputStream(contactImageUri);
                Drawable contactImage = Drawable.createFromStream(contactImageStream, contactImageUri.toString());

                if (tint) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        contactImage.setTint(getResources().getColor(R.color.contact_photo_tint));
                        contactImage.setTintMode(PorterDuff.Mode.DARKEN);
                    }
                }

                contactPhoto.setImageDrawable(contactImage);

            } catch (Exception e) {

            }


        }
    }

    private void playVoice() {

        if (!voice.equals("")) {

            Uri voiceURI = Uri.parse(voice);

            voicePlayer = new MediaPlayer();

            try {
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

//        audioManager.setRingerMode(currentRingerMode);

//        audioManager.setStreamVolume(AudioManager.STREAM_RING, currentRingerVolume, 0);

        stopRinging();

        unMuteAll();

//        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentMediaVolume, 0);

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void finish() {
        String mode = getIntent().getExtras().getString("mode");

        int modeTimes = getIntent().getExtras().getInt("modeTimes");

        if (!mode.equals(getString(R.string.call_mode_type)) && modeTimes < 3) {
            Intent intent = new Intent(this, FakeRingerActivity.class);

            intent.putExtra("contactImage", contactImageString);
            intent.putExtra("number", number);
            intent.putExtra("name", name);

            intent.putExtra("voice", voice);
            intent.putExtra("ringUri", ringStringUri);
            intent.putExtra("mode", mode);
            intent.putExtra("modeTimes", modeTimes++);


            intent.putExtra("duration", duration);
            intent.putExtra("hangUpAfter", hangUpAfter);


            PendingIntent pendingIntent = PendingIntent.getActivity(this, ALARM_ID, intent, PendingIntent.FLAG_ONE_SHOT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            alarmManager.cancel(pendingIntent);

            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10 * 1000, pendingIntent);

        }
        super.finish();

    }
}
