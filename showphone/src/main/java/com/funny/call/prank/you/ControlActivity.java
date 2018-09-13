package com.funny.call.prank.you;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.funny.call.prank.you.view.ThemeDialogView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.xdandroid.hellodaemon.DaemonEnv;
import com.xdandroid.hellodaemon.IntentWrapper;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.funny.call.prank.you.IFace.IAdListener;
import com.funny.call.prank.you.IFace.IEventListener;
import com.funny.call.prank.you.ad.HandLoadInterstitialAd;
import com.funny.call.prank.you.ad.LoadInterstitialAd;
import com.funny.call.prank.you.service.TraceServiceImpl;
import com.funny.call.prank.you.utils.AppInfoUtil;
import com.funny.call.prank.you.utils.CallSettingUtil;
import com.funny.call.prank.you.utils.Feedback;
import com.funny.call.prank.you.utils.MtaUtils;
import com.funny.call.prank.you.view.CallModeDialogView;
import com.funny.call.prank.you.view.CallTimeDialogView;
import com.funny.call.prank.you.view.PicTipDialogView;
import com.funny.call.prank.you.view.VoiceDialogView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;

import static android.view.View.VISIBLE;
import static com.funny.call.prank.you.utils.Constant.GP_STORE;
import static com.funny.call.prank.you.utils.Constant.MI_STORE;
import static com.funny.call.prank.you.utils.Constant.SYSTEM_CONTACTS_REQ;
import static com.funny.call.prank.you.utils.Constant.SYSTEM_RING_REQ;
import static com.funny.call.prank.you.utils.Constant.SYSTEM_VOICE_REQ;

public class ControlActivity extends BaseActivity implements IEventListener, IAdListener {

    public static final String TAG = "ControlActivity";

    @BindView(R.id.iv_add)
    ImageView iv_add;
    @BindView(R.id.iv_splash)
    FrameLayout iv_splash;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer_layout;

    @BindView(R.id.et_num)
    EditText et_num;
    @BindView(R.id.et_name)
    EditText et_name;

    @BindView(R.id.tv_time)
    TextView tv_time;
    @BindView(R.id.tv_ring)
    TextView tv_ring;
    @BindView(R.id.tv_voice)
    TextView tv_voice;
    @BindView(R.id.tv_mode)
    TextView tv_mode;
    @BindView(R.id.tv_themeset)
    TextView tv_themeset;

    @BindView(R.id.iv_guagua)
    ImageView iv_guagua;

    @BindView(R.id.fl_main)
    FrameLayout fl_main;

    @BindView(R.id.fl_main_content)
    LinearLayout fl_main_content;

    @BindView(R.id.main_left_drawer_layout)
    FrameLayout main_left_drawer_layout;

    @BindView(R.id.fl_tigger_match)
    FrameLayout fl_tigger_match;
    @BindView(R.id.image_gift_anim)
    ImageView image_gift_anim;

    @BindView(R.id.btn_giftad_delete)
    ImageView btn_giftad_delete;


    private PicTipDialogView mPicTipDialog;
    private VoiceDialogView mVoiceDialogView;
    private CallTimeDialogView mCallTimeDialogView;
    private CallModeDialogView mCallModeDialogView;
    private ThemeDialogView mThemeDialogView;

    LoadInterstitialAd loadInterstitialAd;
    protected HandLoadInterstitialAd ad;
    AdView adView;

//    com.u3k.app.external.InterstitialAd interstitialAd; //艾闪


    public static void startActivity(Activity owner) {
        Intent intent = new Intent(owner, ControlActivity.class);
        owner.startActivity(intent);
    }


    @Override
    protected int getLayout() {
        transparentBar(this);
        return R.layout.activity_control;
    }

    private int getStateBar3() {
        int result = 0;
        int resourceId = this.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = this.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    protected void init() {
//        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fl_main_content.getLayoutParams();
//        layoutParams.topMargin = getStateBar3();
//
//        main_left_drawer_layout.setPadding(0, getStateBar3(), 0, 0);
//        main_right_drawer_layout.setPadding(0, getStateBar3(), 0, 0);

        drawer_layout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                // 得到contentView

                View content = drawer_layout.getChildAt(0);

                int offset = (int) (drawerView.getWidth() * slideOffset);
                content.setTranslationX(drawerView == drawer_layout.getChildAt(1) ? offset : -offset);

            }
        });
        loadInterstitialAd = new LoadInterstitialAd(this);

//        interstitialAd = new com.u3k.app.external.InterstitialAd(App.mAppIdKey, this, App.AD_ID);
//        interstitialAd.setAdListener(new com.u3k.app.external.InterstitialAdListener() {
//
//            @Override
//            public void onError(Ad ad, int i, String s) {
//                Log.d(TAG, "onError: ");
//            }
//
//            @Override
//            public void onAdLoaded(Ad ad) {
//                onASAdLoaded(interstitialAd);
//                Log.d(TAG, "onError: ");
//
//            }
//
//            @Override
//            public void onAdClicked(Ad ad) {
//                Log.d(TAG, "onError: ");
//
//            }
//
//            @Override
//            public void onLoggingImpression(Ad ad) {
//                Log.d(TAG, "onError: ");
//
//            }
//
//            @Override
//            public void onInterstitialDisplayed(Ad ad) {
//                Log.d(TAG, "onError: ");
//
//            }
//
//            @Override
//            public void onInterstitialDismissed(Ad ad) {
//                startGameUK(interstitialAd);
//                Log.d(TAG, "onError: ");
//
//
//            }
//        });
//        startGameUK(interstitialAd);


        ad = HandLoadInterstitialAd.getInstance(this);

        if (!MtaUtils.isAppLive()) {
            finish();

        }

        if (BuildConfig.FLAVOR.equals(BuildConfig.mi)) {
            findViewById(R.id.fl_pp).setVisibility(View.GONE);

        }

        Glide.with(this).load(R.mipmap.gif_guagua).into(iv_guagua);


//        initBanner();
        fl_tigger_match.setVisibility(View.GONE);
        setCallThemeText(CallSettingUtil.getCallTheme(this).name());


    }

//    public void startGameUK(com.u3k.app.external.InterstitialAd ad) {
//        if (ad != null && !ad.isAdLoaded()) {
//            ad.loadAd();
//        }
//
//    }

    @Override
    public void onAdLoaded(InterstitialAd ad) {

        if (ad == loadInterstitialAd.getSplashAd()) {


            if (iv_splash.getVisibility() == View.GONE) return;

            mHandler.removeCallbacks(null);
            mHandler.removeCallbacksAndMessages(null);
            isOnBackPressed = false;

            if (AppInfoUtil.isGP()) {
                loadInterstitialAd.showInterstitial(loadInterstitialAd.getSplashAd());

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        iv_splash.setVisibility(View.GONE);

                    }
                }, 200);

            }
        }

        if (ad == loadInterstitialAd.getTiggerAd()) {

            if (AppInfoUtil.isGP() && fl_tigger_match.getVisibility() == VISIBLE) {
                loadInterstitialAd.showInterstitial(loadInterstitialAd.getTiggerAd());
                fl_tigger_match.postDelayed(() -> {
                    fl_tigger_match.setVisibility(View.GONE);
                }, 100);

            }

        }
    }

//    @Override
//    public void onASAdLoaded(com.u3k.app.external.InterstitialAd ad) {
//
//
//        if (iv_splash.getVisibility() == View.GONE) return;
//
//        mHandler.removeCallbacks(null);
//        mHandler.removeCallbacksAndMessages(null);
//        isOnBackPressed = false;
//
//        if (AppInfoUtil.isGP()) {
//            // Show the ad if it's ready. Otherwise toast and restart the game.
////            if (ad != null && ad.isAdLoaded()) {
////                ad.show();
////            }
//
//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    iv_splash.setVisibility(View.GONE);
//
//                }
//            }, 200);
//
//        }
//
//    }

    @Override
    public void onAdClosed(InterstitialAd ad) {
//        if (ad == loadInterstitialAd.getStartAd()) {
////            onClickSchedule();
//
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isOnBackPressed) {
            loadBanner(adView);
            if (BuildConfig.FLAVOR.equals(BuildConfig.gp) && loadInterstitialAd.getSplashAd().isLoaded()) {

                loadInterstitialAd.showInterstitial(loadInterstitialAd.getSplashAd());//显示

            }
//            else if (BuildConfig.FLAVOR.equals(BuildConfig.gp) && interstitialAd.isAdLoaded()) {
//                interstitialAd.show();
//
//            }
            else {
                iv_splash.setVisibility(VISIBLE);

            }


            if (!loadInterstitialAd.getSplashAd().isLoaded()) {
                loadInterstitialAd.startGame(loadInterstitialAd.getSplashAd());//加载

            }

//            if (BuildConfig.FLAVOR.equals(BuildConfig.gp) && !interstitialAd.isAdLoaded()) {
//                startGameUK(interstitialAd); //加载
//            }


            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (iv_splash.getVisibility() == View.GONE) return;
                    if (!loadInterstitialAd.showInterstitial(loadInterstitialAd.getSplashAd())) {

//                        interstitialAd.show();
                    }

                }
            }, 4800);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    iv_splash.setVisibility(View.GONE);
                }
            }, 5000);

            isOnBackPressed = false;
        }

    }

    @OnClick({R.id.tv_start, R.id.fl_setting,
            R.id.iv_add, R.id.iv_contacts,
            R.id.cv_calltime, R.id.cv_callring,
            R.id.cv_callvolice, R.id.cv_callmode,
            R.id.iv_finish, R.id.fl_pp,
            R.id.fl_feedback, R.id.fl_about,
            R.id.fl_tigger, R.id.btn_giftad_delete,
            R.id.fl_theme,


            R.id.fl_rate, R.id.fl_share})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.fl_share:
                toOtherApp(this, null, "Fack Call",
                        "Fack Call: Fack Call for you." +
                                "get it from：" +
                                (AppInfoUtil.isGP() ? GP_STORE : MI_STORE)
                                + "details?id=" + getPackageName());
                break;
            case R.id.btn_giftad_delete:
                fl_tigger_match.setVisibility(View.GONE);

                break;

            case R.id.fl_tigger:
                fl_tigger_match.setVisibility(VISIBLE);
                setGifDrawable(R.drawable.ani_home_top_giftad);
                loadInterstitialAd.startGame(loadInterstitialAd.getTiggerAd());
                break;

            case R.id.fl_rate:
                Toast.makeText(this, getString(R.string.thinks), Toast.LENGTH_LONG).show();
                rate();
                break;
            case R.id.fl_theme:
                theme();
                break;

            case R.id.tv_start:
                if (mThemeDialogView != null)
                    mThemeDialogView.setCallTheme(null);
                onStartCall();
                break;


            case R.id.fl_setting:

                drawer_layout.openDrawer(Gravity.START);
//                SettingActivity.startActivity(this);
                break;
            case R.id.iv_add:

                loadInterstitialAd.startGame(loadInterstitialAd.getHeaderAd());

                onToAddpic();

                break;

            case R.id.iv_contacts:
                onToContacts();

                break;
            case R.id.cv_calltime:
                if (null == mCallTimeDialogView) {
                    mCallTimeDialogView = new CallTimeDialogView(this);
                }
                mCallTimeDialogView.show();

                break;
            case R.id.cv_callring:

                toChooseRing();

                break;
            case R.id.cv_callvolice:

                toChooseVolice();

                break;
            case R.id.cv_callmode:
                if (null == mCallModeDialogView) {
                    mCallModeDialogView = new CallModeDialogView(this);
                }
                mCallModeDialogView.show();

                break;

            case R.id.iv_finish:
                drawer_layout.closeDrawer(Gravity.START);

                break;
            case R.id.fl_pp:

                try {
                    Uri uri = Uri.parse("https://shimo.im/docs/iXvDcOVAc1Mwe5dn"); // 浏览器
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    this.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case R.id.fl_feedback:
                try {
                    Feedback feedback = new Feedback(this);
                    feedback.startFeedback();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.fl_about:
                AboutActivity.startActivity(this);
                break;

        }
    }


    @Override
    public void onStartCall() {
        AndPermission.with(this).runtime().
                permission(new String[]{Permission.READ_CALL_LOG, Permission.WRITE_CALL_LOG, Manifest.permission.WAKE_LOCK}).
                onGranted(permission -> {

                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !notificationManager.isNotificationPolicyAccessGranted()) {
                        Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                        getApplicationContext().startActivity(intent);
                    } else {
//                        if ((callTheme != null) || !loadInterstitialAd.showInterstitial(loadInterstitialAd.getStartAd()))
                        onClickSchedule();

                    }

                }).
                onDenied(permission -> {
                    AndPermission.with(this).runtime().setting().start();
                    Toast.makeText(this, this.getString(R.string.text_permission_call_log), Toast.LENGTH_LONG).show();
                }).
                start();
    }

    @Override
    public void setCallThemeText(String s) {
        tv_themeset.setText(s);
    }

    private void theme() {
        if (mThemeDialogView == null) {
            mThemeDialogView = new ThemeDialogView(this);

        }
        mThemeDialogView.show();
    }

    private void toChooseVolice() {
        AndPermission
                .with(this)
                .runtime()
                .permission(Permission.Group.STORAGE)
                .onGranted(permissions -> {
                    if (mVoiceDialogView == null) {
                        mVoiceDialogView = new VoiceDialogView(this);
                    }

                    mVoiceDialogView.show();


                })
                .onDenied(permissions -> {
                    AndPermission.with(this).runtime().setting().start();
                    Toast.makeText(this, this.getString(R.string.text_permission), Toast.LENGTH_LONG).show();
                }).start();
    }

    //选择铃声
    public void toChooseRing() {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Setting Ring");
        Uri selUri = (Uri) tv_ring.getTag();
        if (null != selUri) {
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, selUri);
        }
        startActivityForResult(intent, SYSTEM_RING_REQ);
    }


    private void onToAddpic() {
        AndPermission
                .with(this)
                .runtime()
                .permission(Permission.Group.STORAGE, Permission.Group.CAMERA)
                .onGranted(permissions -> {

                    if (null == mPicTipDialog) {
                        mPicTipDialog = new PicTipDialogView(this);
                    }
                    mPicTipDialog.show();

                })
                .onDenied(permissions -> {
                    AndPermission.with(this).runtime().setting().start();
                    Toast.makeText(this, this.getString(R.string.text_permission), Toast.LENGTH_LONG).show();
                }).start();
    }

    private void onToContacts() {
        AndPermission
                .with(this)
                .runtime()
                .permission(Permission.Group.CONTACTS)
                .onGranted(permissions -> {
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intent, SYSTEM_CONTACTS_REQ);


                })
                .onDenied(permissions -> {
                    AndPermission.with(this).runtime().setting().start();
                    Toast.makeText(this, this.getString(R.string.text_permission_contacts), Toast.LENGTH_LONG).show();
                }).start();

    }

    @Override
    public void setIvAddImage(String name, String url) {
        iv_add.setTag(null);//需要清空tag，否则报错

        Glide.with(this).load(url).
                fitCenter()
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(iv_add);
        iv_add.setTag(url);

        et_name.setText(name);

        loadInterstitialAd.showInterstitial(loadInterstitialAd.getHeaderAd());
    }

    @Override
    public void setTimeTextView(String time, String timeNum) {
        if (!TextUtils.isEmpty(time)) {

            tv_time.setText(time);
            tv_time.setTag(timeNum);
        }

    }

    @Override
    public void setModeTextView(String text, String mode) {
        if (!TextUtils.isEmpty(text)) {
            tv_mode.setText(text);
            tv_mode.setTag(mode);
        }

    }

    @Override
    public void setVoiceTextView(String name, Uri uri) {
        tv_voice.setText(name);
        tv_voice.setTag(uri);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == SYSTEM_CONTACTS_REQ) {
            try {
                parseContactsData(data);

            } catch (Exception e) {

            } catch (Throwable e) {

            }

        } else if (resultCode == RESULT_OK && requestCode == SYSTEM_RING_REQ) {
            parseRingData(data);

        } else if (resultCode == RESULT_OK && requestCode == SYSTEM_VOICE_REQ) {
            if (null != mVoiceDialogView)
                mVoiceDialogView.onActivityResult(requestCode, resultCode, data);


        } else if (null != mPicTipDialog) {
            mPicTipDialog.onActivityResult(requestCode, resultCode, data);

        }


    }


    private void parseRingData(Intent data) {

        try {
            Uri pickedUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (null == pickedUri)
                return;

            Cursor cursor = managedQuery(pickedUri, null, null, null, null);

            if (cursor.moveToFirst()) {
                for (int i = 0; i < 10; i++) {

//                    song.song = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
//                    song.singer = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
//                    song.path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
//                    song.duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
//                    song.size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));

                    String name = "";
                    try {
                        name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                    } catch (Exception e) {
                        name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                    }

                    if (TextUtils.isEmpty(name)) {
                        tv_ring.setText(name);
                    }
                }

                tv_ring.setTag(pickedUri);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseContactsData(Intent data) {
        String name = "", phoneNumber = "";
        if (data == null) {
            return;
        }
        Uri contactData = data.getData();
        if (contactData == null) {
            return;
        }
        if (true) {
            String[] strings = getPhoneContacts(contactData);
            et_name.setText(strings[0]);
            et_num.setText(strings[1]);
            return;
        }


//        这个问题其实很简单  估计大家在查询数据库的时候用的是这个函数
//        viedoCursor = context.managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoColumns,null, null, null);
//        只要换成这样就可以了：
//        ContentResolver cr = context.getContentResolver();
//        viedoCursor = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoColumns,null, null, null);
//        我也是无意间发现有着两种写法的 然后试了一下问题就解决了

        Cursor cursor = managedQuery(contactData, null, null, null, null);
        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

            if (hasPhone.equalsIgnoreCase("1")) {
                hasPhone = "true";
            } else {
                hasPhone = "false";
            }

            if (Boolean.parseBoolean(hasPhone)) {
                Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
                while (phones.moveToNext()) {
                    phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                }
                phones.close();
            }

//            cursor.close();//managedQuery时不要close
        }
        et_num.setText(phoneNumber);
        et_name.setText(name);

    }


    private String[] getPhoneContacts(Uri contactData) {
        String[] contact = new String[2];

        Cursor cursor = getContentResolver().query(contactData, null, null, null, null);
        //Key联系人姓名,Value联系人手机号
        Map<String, String> phoneMap = this.getContactPhone(cursor);
        if (!cursor.isClosed()) {
            cursor.close();
        }
        if (null != phoneMap && !phoneMap.isEmpty()) {
            Set<String> keySet = phoneMap.keySet();
            if (null != keySet && !keySet.isEmpty()) {
                Object[] keys = keySet.toArray();
                contact[0] = (String) keys[0];
                contact[1] = phoneMap.get(contact[0]);
            }
        }
        return contact;
    }

    /**
     * 获取联系人姓名及手机号
     *
     * @param cursor
     * @return Key为联系人姓名, Value为联系人手机号
     */
    private Map<String, String> getContactPhone(Cursor cursor) {
        Map<String, String> resultMap = new HashMap<String, String>();
        String phoneName = null;// 姓名
        String mobilePhoneNo = null;// 手机号

        if (null != cursor) {
            cursor.moveToFirst();

            // 获得联系人的ID号
            int idFieldIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            String contactId = cursor.getString(idFieldIndex);
            // 联系人姓名
            int idphoneNameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            phoneName = cursor.getString(idphoneNameIndex);

            // 获得联系人的电话号码的cursor;
            Cursor allPhones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{contactId}, null);

            // 所以联系电话（包话电话和手机号）
            List<String> allPhoneNumList = new ArrayList<String>();
            if (allPhones.moveToFirst()) {

                // 遍历所有的电话号码
                for (; !allPhones.isAfterLast(); allPhones.moveToNext()) {
                    int telNoTypeIndex = allPhones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
                    int telNoType = allPhones.getInt(telNoTypeIndex);

                    int telNoIndex = allPhones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    String telNo = allPhones.getString(telNoIndex);
                    allPhoneNumList.add(telNo);

                    if (2 == telNoType) {// 手机号（原生态的SDK定义：mobile是2，home是1，work是3，other是7）
                        mobilePhoneNo = telNo;
                        break;
                    }
                }
                if (!allPhones.isClosed()) {
                    allPhones.close();
                }

                if (null == mobilePhoneNo) {// 没有存贮手机号
                    if (!allPhoneNumList.isEmpty()) {// 存在其它号码
                        for (String tel : allPhoneNumList) {
                            if (null != tel) {// 取属于手机号格式
                                mobilePhoneNo = tel;
                                break;
                            }
                        }
                    }
                }
            }
            if (!cursor.isClosed()) {
                cursor.close();
            }

            resultMap.put(phoneName, mobilePhoneNo);
        }
        return resultMap;
    }


    public static final int HANA_UP_AFTER = 120;
    public static final int DURATION = 120;


    public void onClickSchedule() {


        String headPicUri = (iv_add.getTag() == null) ? null : iv_add.getTag().toString();

        String number = (TextUtils.isEmpty(et_num.getText())) ? getResources().getString(R.string.unknown_number) : et_num.getText().toString();
        String name = (TextUtils.isEmpty(et_name.getText())) ? getResources().getString(R.string.unknown) : et_name.getText().toString();

        String time = (tv_time.getTag() == null) ? "3" : tv_time.getTag().toString();
        String voiceUri = (tv_voice.getTag() == null) ? null : tv_voice.getTag().toString();
        String ringUri = (tv_ring.getTag() == null) ? null : tv_ring.getTag().toString();
        String mode = (tv_mode.getTag() == null) ? getString(R.string.call_mode_type) : tv_mode.getTag().toString();


        String hangUpAfter = Integer.toString(HANA_UP_AFTER);
        String duration = Integer.toString(DURATION);


        if (TextUtils.isEmpty(number)) {
            Toast.makeText(this, R.string.number_null, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent();


        intent.putExtra("contactImage", headPicUri);
        intent.putExtra("number", number);
        intent.putExtra("name", name);

        intent.putExtra("voice", voiceUri);
        intent.putExtra("ringUri", ringUri);
        intent.putExtra("mode", mode);

        intent.putExtra("modeTimes", 1);


        intent.putExtra("time", Integer.parseInt(time));

        intent.putExtra("duration", Integer.parseInt(duration));
        intent.putExtra("hangUpAfter", Integer.parseInt(hangUpAfter));


        if (mThemeDialogView != null && mThemeDialogView.getCallTheme() != null) {
            time = "0";

            intent.setClass(this, CallSettingUtil.getThemeClass(getApplication(), mThemeDialogView.getCallTheme()));
            startActivity(intent);
            mThemeDialogView.setCallTheme(null);

            return;


        } else {
            intent.setClass(getApplication(), TraceServiceImpl.class);
        }


        TraceServiceImpl.sShouldStopService = false;
//        DaemonEnv.startServiceMayBind(TraceServiceImpl.class);
        DaemonEnv.startServiceMayBindByIntentData(intent, TraceServiceImpl.class);


//        PendingIntent pendingIntent = PendingIntent.getActivity(this, ALARM_ID, intent, PendingIntent.FLAG_ONE_SHOT);
//        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + Integer.parseInt(time) * 1000, pendingIntent);
        Toast.makeText(this, R.string.call_ready, Toast.LENGTH_SHORT).show();
        ad.startGame(ad.getSplashAd());

//        loadInterstitialAd.showInterstitial(loadInterstitialAd.getStartAd());
//        finish();

    }


    public void onClickTest(View v) {
//            case R.id.btn_start:

        TraceServiceImpl.sShouldStopService = false;
        DaemonEnv.startServiceMayBind(TraceServiceImpl.class);

//            case R.id.btn_white:
        IntentWrapper.whiteListMatters(this, "轨迹跟踪服务的持续运行");
//            case R.id.btn_stop:
        TraceServiceImpl.stopService();
    }


    protected void rate() {
        try {
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + getPackageName())));
        } catch (ActivityNotFoundException var3) {
            Toast.makeText(this, "No Store installed on device", Toast.LENGTH_SHORT).show();
        } finally {
        }
    }


    protected boolean toOtherApp(Activity activity, String packageName, String title, String text) {
        try {
            Intent shareIntent = new Intent("android.intent.action.SEND");
            shareIntent.setType("text/plain");
            shareIntent.putExtra("android.intent.extra.SUBJECT", title);
            shareIntent.putExtra("android.intent.extra.TEXT", text);
            if (packageName != null) {
                shareIntent.setPackage(packageName);
            }

            activity.startActivity(shareIntent);
            return true;
        } catch (Exception var5) {
            return false;
        }
    }

    //防止华为机型未加入白名单时按返回键回到桌面再锁屏后几秒钟进程被杀
    boolean isOnBackPressed = true;

    @Override
    public void onBackPressed() {
        if (drawer_layout.isDrawerOpen(Gravity.START) || drawer_layout.isDrawerOpen(Gravity.RIGHT))
            drawer_layout.closeDrawers();
        else if (fl_tigger_match.getVisibility() == VISIBLE) {

        } else {
            IntentWrapper.onBackPressed(this);
            isOnBackPressed = true;
        }

    }

    // TODO: 2018/8/28  
    private void initBanner() {
        adView = new AdView(this);
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId("");

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {

                super.onAdClosed();
            }
        });

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.BOTTOM | Gravity.CENTER;
        fl_main.addView(adView, 1, lp);
    }


    private void loadBanner(AdView ad) {
        if (ad != null && !ad.isLoading()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            ad.loadAd(adRequest);
        }

    }

    public void setGifDrawable(int resId) {
        if (image_gift_anim != null) {
            image_gift_anim.setBackgroundResource(resId);
            image_gift_anim.setVisibility(VISIBLE);
            Drawable drawable = image_gift_anim.getBackground();
            if (drawable instanceof AnimationDrawable) {
                ((AnimationDrawable) drawable).start();
            }

        }
    }

}