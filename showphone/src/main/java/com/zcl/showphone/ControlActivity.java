package com.zcl.showphone;

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
import com.miui.zeus.mimo.sdk.ad.AdWorkerFactory;
import com.miui.zeus.mimo.sdk.ad.IAdWorker;
import com.miui.zeus.mimo.sdk.listener.MimoAdListener;
import com.xdandroid.hellodaemon.DaemonEnv;
import com.xdandroid.hellodaemon.IntentWrapper;
import com.xiaomi.ad.common.pojo.AdType;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.zcl.showphone.IFace.IEventListener;
import com.zcl.showphone.service.TraceServiceImpl;
import com.zcl.showphone.utils.AppInfoUtil;
import com.zcl.showphone.utils.CallSettingUtil;
import com.zcl.showphone.utils.Feedback;
import com.zcl.showphone.utils.MtaUtils;
import com.zcl.showphone.view.CallModeDialogView;
import com.zcl.showphone.view.CallTimeDialogView;
import com.zcl.showphone.view.PicTipDialogView;
import com.zcl.showphone.view.VoiceDialogView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;

import static android.view.View.VISIBLE;
import static com.zcl.showphone.utils.Constant.GP_STORE;
import static com.zcl.showphone.utils.Constant.MI_STORE;
import static com.zcl.showphone.utils.Constant.SYSTEM_CONTACTS_REQ;
import static com.zcl.showphone.utils.Constant.SYSTEM_RING_REQ;
import static com.zcl.showphone.utils.Constant.SYSTEM_VOICE_REQ;

public class ControlActivity extends BaseActivity implements IEventListener {

    public static final String TAG = "ControlActivity";

    @BindView(R.id.iv_add)
    ImageView iv_add;
    @BindView(R.id.iv_splash)
    ImageView iv_splash;

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

    @BindView(R.id.theme_android)
    TextView theme_android;

    @BindView(R.id.theme_mi)
    TextView theme_mi;
    @BindView(R.id.theme_samsung)
    TextView theme_samsung;
    @BindView(R.id.theme_blur)
    TextView theme_blur;

    @BindView(R.id.iv_guagua)
    ImageView iv_guagua;

    @BindView(R.id.fl_main)
    FrameLayout fl_main;

    @BindView(R.id.fl_main_content)
    LinearLayout fl_main_content;

    @BindView(R.id.main_left_drawer_layout)
    FrameLayout main_left_drawer_layout;
    @BindView(R.id.main_right_drawer_layout)
    FrameLayout main_right_drawer_layout;

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

    private IAdWorker mWorker;


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

        if (!MtaUtils.isAppLive()) {
            finish();

        }

        if (BuildConfig.FLAVOR.equals(BuildConfig.mi)) {
            findViewById(R.id.fl_pp).setVisibility(View.GONE);

        }

        Glide.with(this).load(R.mipmap.gif_guagua).into(iv_guagua);


        theme_android.setTextColor(CallSettingUtil.isThisCalltheme(this, CallSettingUtil.CallTheme.ANDROID5) ? getResources().getColor(R.color.colorMain) : getResources().getColor(R.color.colorMenuTitle));
        theme_mi.setTextColor(CallSettingUtil.isThisCalltheme(this, CallSettingUtil.CallTheme.MI) ? getResources().getColor(R.color.colorMain) : getResources().getColor(R.color.colorMenuTitle));
        theme_blur.setTextColor(CallSettingUtil.isThisCalltheme(this, CallSettingUtil.CallTheme.BLUR) ? getResources().getColor(R.color.colorMain) : getResources().getColor(R.color.colorMenuTitle));
        theme_samsung.setTextColor(CallSettingUtil.isThisCalltheme(this, CallSettingUtil.CallTheme.SAMSUNG) ? getResources().getColor(R.color.colorMain) : getResources().getColor(R.color.colorMenuTitle));
        fl_tigger_match.setVisibility(View.GONE);


    }


    @Override
    protected void onResume() {
        super.onResume();

        if (isOnBackPressed) {

            try {
                mWorker = AdWorkerFactory.getAdWorker(this, fl_main, new MimoAdListener() {
                    @Override
                    public void onAdPresent() {
                        // 开屏广告展示
                        Log.d(TAG, "onAdPresent");

                    }

                    @Override
                    public void onAdClick() {
                        //用户点击了开屏广告
                        Log.d(TAG, "onAdClick");
                    }

                    @Override
                    public void onAdDismissed() {
                        //这个方法被调用时，表示从开屏广告消失。
                        Log.d(TAG, "onAdDismissed");
                    }

                    @Override
                    public void onAdFailed(String s) {
                        Log.e(TAG, "ad fail message : " + s);
                        iv_splash.setVisibility(View.GONE);

                    }

                    @Override
                    public void onAdLoaded(int size) {
                        //do nothing
                        iv_splash.setVisibility(View.GONE);

                    }

                    @Override
                    public void onStimulateSuccess() {
                    }
                }, AdType.AD_SPLASH);

                mWorker.loadAndShow("5c478315816109fba210a0ad2db7478a");
//                mWorker.loadAndShow("b373ee903da0c6fc9c9da202df95a500");//测试
            } catch (Exception e) {
                e.printStackTrace();
                fl_main.setVisibility(View.GONE);
                iv_splash.setVisibility(View.GONE);
            }

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

            R.id.try1, R.id.try2, R.id.try_blur, R.id.try_samsung,

            R.id.fl_theme_mi, R.id.fl_theme_android, R.id.fl_theme_blur, R.id.fl_theme_samsung,
            R.id.fl_rate, R.id.fl_share})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.fl_share:
                toOtherApp(this, null, "Fack Call",
                        "Fack Call: Fack Call for you." +
                                "get it from：" +
                                (AppInfoUtil.isGP() ? GP_STORE : MI_STORE)
                                + "details?id=com.zcl.showphone");
                break;
            case R.id.btn_giftad_delete:
                fl_tigger_match.setVisibility(View.GONE);

                break;

            case R.id.fl_tigger:
                fl_tigger_match.setVisibility(VISIBLE);
                setGifDrawable(R.drawable.ani_home_top_giftad);
                break;

            case R.id.fl_rate:
                Toast.makeText(this, getString(R.string.thinks), Toast.LENGTH_LONG).show();
                rate();
                break;
            case R.id.fl_theme:
                theme();
                break;
            case R.id.fl_theme_mi:
                themeSelect((TextView) theme_mi, CallSettingUtil.CallTheme.MI);

                break;
            case R.id.fl_theme_samsung:
                themeSelect((TextView) theme_samsung, CallSettingUtil.CallTheme.SAMSUNG);

                break;
            case R.id.fl_theme_android:
                themeSelect((TextView) theme_android, CallSettingUtil.CallTheme.ANDROID5);

                break;
            case R.id.fl_theme_blur:
                themeSelect((TextView) theme_blur, CallSettingUtil.CallTheme.BLUR);

                break;
            case R.id.tv_start:

                callTheme = null;
                onStartCall();
                break;

            case R.id.try1:
                callTheme = CallSettingUtil.CallTheme.ANDROID5;
                onStartCall();
                break;
            case R.id.try2:
                callTheme = CallSettingUtil.CallTheme.MI;

                onStartCall();

                break;

            case R.id.try_blur:
                callTheme = CallSettingUtil.CallTheme.BLUR;

                onStartCall();
                break;

            case R.id.try_samsung:
                callTheme = CallSettingUtil.CallTheme.SAMSUNG;

                onStartCall();

                break;
            case R.id.fl_setting:

                drawer_layout.openDrawer(Gravity.START);
//                SettingActivity.startActivity(this);
                break;
            case R.id.iv_add:


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
                    Uri uri = Uri.parse("https://shimo.im/docs/XAiddJOJuLsCV7vP/"); // 浏览器
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

    private void themeSelect(TextView view, CallSettingUtil.CallTheme theme) {

        Toast.makeText(this, this.getString(R.string.text_select_theme), Toast.LENGTH_SHORT).show();

        theme_android.setTextColor(getResources().getColor(R.color.colorMenuTitle));
        theme_mi.setTextColor(getResources().getColor(R.color.colorMenuTitle));
        theme_samsung.setTextColor(getResources().getColor(R.color.colorMenuTitle));
        theme_blur.setTextColor(getResources().getColor(R.color.colorMenuTitle));

        view.setTextColor(getResources().getColor(R.color.colorMain));
        CallSettingUtil.setCallTheme(this, theme);
    }

    private void onStartCall() {
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

    private void theme() {
        drawer_layout.openDrawer(Gravity.RIGHT);

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
    public void setIvAddImage(String url) {
        iv_add.setTag(null);//需要清空tag，否则报错
        if (url == null) {

            Glide.with(this).load(R.mipmap.btn_header).into(iv_add);
            return;
        }

        Glide.with(this).load(url).
                fitCenter()
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(iv_add);
        iv_add.setTag(url);

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

            int idphonePhotoIndex = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI);

            setIvAddImage(cursor.getString(idphonePhotoIndex));

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

    private CallSettingUtil.CallTheme callTheme = null;


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


        if (callTheme != null) {
            time = "0";

            intent.setClass(this, CallSettingUtil.getThemeClass(getApplication(), callTheme));
            startActivity(intent);
            callTheme = null;

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
        // 捕获back键，在展示广告期间按back键，不跳过广告
        if (fl_main.getVisibility() == View.VISIBLE) {
            return;
        } else if (drawer_layout.isDrawerOpen(Gravity.START) || drawer_layout.isDrawerOpen(Gravity.RIGHT))
            drawer_layout.closeDrawers();
        else if (fl_tigger_match.getVisibility() == VISIBLE) {

        } else {
            IntentWrapper.onBackPressed(this);
            isOnBackPressed = true;
        }

    }

    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
            mWorker.recycle();
        } catch (Exception e) {
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